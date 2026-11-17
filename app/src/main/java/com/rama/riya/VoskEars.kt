package com.rama.riya

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioFormat
import android.media.AudioManager
import android.os.Build
import android.media.AudioRecord
import android.media.MediaRecorder
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.SpeakerModel
import java.io.File
import java.io.FileOutputStream

/** Words the music guard listens for while music is playing. Only these words can ever be recognised, so crowd and traffic noise does not turn into commands. */
object MusicWords {
    const val GRAMMAR = "[\"stop\", \"pause\", \"music\", \"song\", \"songs\", \"the\", \"next\", \"previous\", \"play\", \"skip\", \"last\", \"track\", \"please\", \"turn\", \"off\", \"[unk]\"]"
    private val stopRe = Regex("\\b(stop|pause|turn off)\\b.*\\b(music|songs?)\\b|\\b(music|songs?)\\b.*\\b(stop|pause|off)\\b")
    private val nextRe = Regex("\\b(next|skip)\\b.*\\b(song|songs|music|track)\\b")
    private val prevRe = Regex("\\b(previous|last)\\b.*\\b(song|songs|music|track)\\b")

    fun parse(json: String): String? {
        val o = try { JSONObject(json) } catch (e: Exception) { return null }
        val t = (o.optString("text").ifEmpty { o.optString("partial") }).lowercase()
        return when {
            stopRe.containsMatchIn(t) -> "stop"
            nextRe.containsMatchIn(t) -> "next"
            prevRe.containsMatchIn(t) -> "previous"
            else -> null
        }
    }
}

/**
 * Offline speech-to-text using Vosk. Owns the microphone while running.
 * Audio is thrown away while [pause]d (so Riya never hears her own voice).
 */
class VoskEars(
    private val ctx: Context,
    private val modelName: String,
    private val onReady: () -> Unit,
    private val onPartial: (String) -> Unit,
    private val onText: (List<String>) -> Unit,
    private val onFail: (String) -> Unit
) {
    /** Voice Match: x-vector of the last finished sentence (null if the model is missing or the sentence was too short). */
    @Volatile var lastSpk: FloatArray? = null
    @Volatile var lastSpkFrames = 0
    @Volatile var speakerReady = false
    @Volatile var wantSpeaker = false
    /** Microphone loudness 0..1 (drives the Google-style bars). */
    @Volatile var onLevel: ((Float) -> Unit)? = null

    // The speaker recogniser ends its own sentences; keep that voice print instead of losing it.
    private var cachedSpk: FloatArray? = null
    private var cachedFrames = 0
    private var cachedAt = 0L

    private fun readSpk(json: String): Pair<FloatArray, Int>? {
        return try {
            val o = JSONObject(json)
            val arr = o.optJSONArray("spk") ?: return null
            Pair(FloatArray(arr.length()) { arr.getDouble(it).toFloat() }, o.optInt("spk_frames", 0))
        } catch (e: Throwable) { null }
    }

    @Volatile private var alive = true
    @Volatile private var paused = true
    @Volatile private var resumeAt = 0L

    fun start() {
        val t = Thread {
            try { android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO) } catch (e: Throwable) { }
            runLoop()
        }
        t.isDaemon = true
        t.start()
    }

    /** True when the live words already look like "name + command": then finish the sentence sooner. */
    @Volatile var looksComplete: ((String) -> Boolean)? = null
    @Volatile var onStopWord: (() -> Unit)? = null
    /** Music guard: while music plays, a tiny word-list recogniser reacts to "stop music" even in noise. */
    @Volatile var musicMode = false
    @Volatile var onMusicWord: ((String) -> Unit)? = null
    @Volatile private var guarding = false
    /** Start a fresh sentence right now (used after a command was acted on from the live words). */
    @Volatile var flushNow = false
    /** While Riya speaks (mic paused), keep listening for just \"stop\". */
    fun guardStart() { guarding = true }
    fun guardEnd() { guarding = false }

    fun pause() { paused = true }

    fun resume() {
        if (paused) {
            resumeAt = System.currentTimeMillis() + 150   // skip the tail of Riya's own voice
            paused = false
        }
    }

    fun stop() { alive = false }

    // With alternatives on, Vosk returns {"alternatives":[{"text":..},..]}; otherwise {"text":..}
    private fun parseTexts(json: String): List<String> {
        val o = JSONObject(json)
        val out = ArrayList<String>()
        val alts = o.optJSONArray("alternatives")
        if (alts != null) {
            for (i in 0 until alts.length()) {
                val t = alts.getJSONObject(i).optString("text").trim()
                if (t.isNotEmpty() && t !in out) out.add(t)
            }
        } else {
            val t = o.optString("text").trim()
            if (t.isNotEmpty()) out.add(t)
        }
        return out
    }

    private fun grabSpk(sr: Recognizer?) {
        var best: FloatArray? = null
        var frames = 0
        if (sr != null) {
            val r = readSpk(sr.finalResult)
            if (r != null) { best = r.first; frames = r.second }
        }
        val c = cachedSpk
        if (c != null && System.currentTimeMillis() - cachedAt < 4000 && cachedFrames > frames) { best = c; frames = cachedFrames }
        cachedSpk = null
        cachedFrames = 0
        lastSpk = best
        lastSpkFrames = frames
    }

    @Volatile private var usedComm = false

    /**
     * Earbuds: a wired / USB headset mic is used automatically. A Bluetooth earbud mic is used only when
     * "Use earbuds microphone" is ON (Bluetooth then switches to call quality). Otherwise the phone mic hears you
     * and the music keeps its quality. Returns true when a Bluetooth communication device was switched on.
     */
    private fun pickMic(a: AudioRecord): Boolean {
        try {
            val am = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val ins = am.getDevices(AudioManager.GET_DEVICES_INPUTS)
            val wired = ins.firstOrNull { it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET || it.type == AudioDeviceInfo.TYPE_USB_HEADSET }
            if (wired != null) { a.setPreferredDevice(wired); return false }
            if (!ctx.getSharedPreferences("riya", Context.MODE_PRIVATE).getBoolean("earbud_mic", false)) return false
            if (Build.VERSION.SDK_INT >= 31) {
                val dev = am.availableCommunicationDevices.firstOrNull {
                    it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO || it.type == AudioDeviceInfo.TYPE_BLE_HEADSET }
                if (dev != null && am.setCommunicationDevice(dev)) {
                    Thread.sleep(400)
                    ins.firstOrNull { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO || it.type == AudioDeviceInfo.TYPE_BLE_HEADSET }
                        ?.let { a.setPreferredDevice(it) }
                    return true
                }
            } else {
                val bt = ins.firstOrNull { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO }
                if (bt != null) {
                    @Suppress("DEPRECATION") am.startBluetoothSco()
                    Thread.sleep(900)
                    a.setPreferredDevice(bt)
                    return true
                }
            }
        } catch (e: Throwable) { }
        return false
    }

    private fun runLoop() {
        var rec: Recognizer? = null
        var guardRec: Recognizer? = null
        var musicRec: Recognizer? = null
        var spkRec: Recognizer? = null
        var mic: AudioRecord? = null
        try {
            val m = VoskModels.model(ctx, modelName)     // shared + preloaded: no disk load on lock / unlock
            val r = Recognizer(m, 16000.0f)
            rec = r
            guardRec = Recognizer(m, 16000.0f, StopWords.GRAMMAR)
            musicRec = try { Recognizer(m, 16000.0f, MusicWords.GRAMMAR) } catch (e: Throwable) { null }
            if (wantSpeaker) {
                try {
                    val sm = VoskModels.speaker(ctx)
                    val sr = Recognizer(m, 16000.0f)
                    sr.setSpeakerModel(sm)
                    spkRec = sr
                    speakerReady = true
                } catch (e: Throwable) { speakerReady = false }
            }
            r.setMaxAlternatives(5)   // keep the 5 best guesses so a misheard name can still be matched

            val minBuf = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
            val a = AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION, 16000,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                maxOf(minBuf, 16000 * 2)
            )
            mic = a
            usedComm = pickMic(a)
            try { if (android.media.audiofx.AcousticEchoCanceler.isAvailable()) android.media.audiofx.AcousticEchoCanceler.create(a.audioSessionId)?.enabled = true } catch (e: Throwable) { }
            try { if (android.media.audiofx.NoiseSuppressor.isAvailable()) android.media.audiofx.NoiseSuppressor.create(a.audioSessionId)?.enabled = true } catch (e: Throwable) { }
            if (a.state != AudioRecord.STATE_INITIALIZED) {
                if (alive) onFail("microphone could not be opened")
                return
            }
            a.startRecording()
            onReady()

            val buf = ShortArray(800)       // 50 ms chunks = quicker reaction
            var lastPartial = ""
            var lastChange = 0L
            var needReset = true
            var wasGuarding = false
            var wasMusic = false
            var dead = 0
            while (alive) {
                val n = a.read(buf, 0, buf.size)
                if (n <= 0) {
                    // the system silenced the mic (screen locked, other app took it): fail so the service restarts the ears
                    if (++dead > 150) throw IllegalStateException("microphone stopped")
                    Thread.sleep(20); continue
                }
                dead = 0
                if (paused || System.currentTimeMillis() < resumeAt) {
                    needReset = true
                    val gr = guardRec
                    if (paused && guarding && gr != null) {
                        if (!wasGuarding) { gr.reset(); wasGuarding = true }
                        val full = gr.acceptWaveForm(buf, n)
                        if (StopWords.isStop(if (full) gr.result else gr.partialResult)) {
                            guarding = false
                            gr.reset()
                            onStopWord?.invoke()
                        }
                    } else wasGuarding = false
                    continue
                }
                wasGuarding = false
                if (flushNow) { flushNow = false; needReset = true }
                if (needReset) { r.reset(); spkRec?.reset(); cachedSpk = null; cachedFrames = 0; lastPartial = ""; needReset = false }
                run {
                    var sum = 0.0
                    for (i in 0 until n) { val v = buf[i].toDouble(); sum += v * v }
                    val rms = Math.sqrt(sum / n) / 32768.0
                    onLevel?.invoke((rms * 9.0).toFloat().coerceIn(0f, 1f))
                }
                if (musicMode) {
                    val mr = musicRec
                    if (mr != null) {
                        if (!wasMusic) { mr.reset(); wasMusic = true }
                        val fullM = mr.acceptWaveForm(buf, n)
                        val cmd = MusicWords.parse(if (fullM) mr.result else mr.partialResult)
                        if (cmd != null) { mr.reset(); onMusicWord?.invoke(cmd) }
                    }
                } else wasMusic = false
                val sp = spkRec
                if (sp != null && sp.acceptWaveForm(buf, n)) {
                    val got = readSpk(sp.result)
                    if (got != null && got.second >= cachedFrames) { cachedSpk = got.first; cachedFrames = got.second; cachedAt = System.currentTimeMillis() }
                }
                if (r.acceptWaveForm(buf, n)) {
                    val texts = parseTexts(r.result)
                    grabSpk(spkRec)
                    lastPartial = ""
                    if (texts.isNotEmpty()) onText(texts)
                } else {
                    val p = JSONObject(r.partialResult).optString("partial").trim()
                    val now = System.currentTimeMillis()
                    if (p.isNotEmpty() && p != lastPartial) {
                        lastPartial = p
                        lastChange = now
                        onPartial(p)
                    } else if (p.isNotEmpty() && now - lastChange > (if (looksComplete?.invoke(p) == true) 190L else 270L)) {
                        // You stopped talking: don't wait for Vosk's slow silence detector, finish right now.
                        val texts = parseTexts(r.finalResult)
                        r.reset()
                        grabSpk(spkRec)
                        lastPartial = ""
                        if (texts.isNotEmpty()) onText(texts)
                    }
                }
            }
        } catch (e: Throwable) {
            if (alive) onFail(e.message ?: e.javaClass.simpleName)
        } finally {
            if (usedComm) try { (ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager).let { am ->
                if (Build.VERSION.SDK_INT >= 31) am.clearCommunicationDevice() else @Suppress("DEPRECATION") am.stopBluetoothSco() } } catch (e: Throwable) { }
            try { mic?.stop() } catch (e: Throwable) { }
            try { mic?.release() } catch (e: Throwable) { }
            try { rec?.close() } catch (e: Throwable) { }
            try { guardRec?.close() } catch (e: Throwable) { }
            try { musicRec?.close() } catch (e: Throwable) { }
            try { spkRec?.close() } catch (e: Throwable) { }
        }
    }
}
