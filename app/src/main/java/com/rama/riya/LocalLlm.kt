package com.rama.riya

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import java.io.File
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * A small chat model that runs fully on the phone (MediaPipe LLM Inference, free, offline).
 *
 * MediaPipe changed its streaming API between versions, so streaming is wired up through reflection:
 * whichever form this MediaPipe version has gets used, and if none exists the answer is produced in one piece.
 */
class LocalLlm(private val ctx: Context) {

    companion object {
        fun file(ctx: Context) = File(ctx.filesDir, "llm/model.task")
        fun installed(ctx: Context): Boolean {
            val f = file(ctx)
            return f.exists() && f.length() > 20_000_000L
        }
    }

    @Volatile private var llm: LlmInference? = null
    @Volatile private var sink: ((String, Boolean) -> Unit)? = null
    private var listenerInOptions = false

    fun isLoaded() = llm != null

    private fun listenerProxy(type: Class<*>): Any {
        val h = InvocationHandler { _: Any?, method: Method, args: Array<out Any?>? ->
            when (method.name) {
                "run" -> {
                    if (args != null && args.size >= 2) {
                        sink?.invoke((args[0] as? String) ?: "", (args[1] as? Boolean) ?: false)
                    }
                    null
                }
                "toString" -> "RiyaListener"
                "hashCode" -> 0
                "equals" -> false
                else -> null
            }
        }
        return Proxy.newProxyInstance(type.classLoader, arrayOf(type), h)
    }

    /** Returns null when loaded, otherwise a message explaining what went wrong. */
    @Synchronized
    fun load(): String? {
        if (llm != null) return null
        if (!installed(ctx)) return "Offline brain file is not installed yet."
        return try {
            val b = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(file(ctx).absolutePath)
                .setMaxTokens(160)
            // Older MediaPipe versions take the streaming listener here; newer ones don't have this method.
            listenerInOptions = false
            try {
                val m = LlmInference.LlmInferenceOptions.Builder::class.java.methods
                    .firstOrNull { it.name == "setResultListener" && it.parameterTypes.size == 1 }
                if (m != null) {
                    m.invoke(b, listenerProxy(m.parameterTypes[0]))
                    listenerInOptions = true
                }
            } catch (e: Throwable) { listenerInOptions = false }
            llm = LlmInference.createFromOptions(ctx, b.build())
            null
        } catch (e: Throwable) {
            "Could not load the offline brain: " + (e.message ?: e.javaClass.simpleName)
        }
    }

    private fun buildPrompt(name: String, past: List<Pair<String, String>>, question: String): String {
        val sb = StringBuilder()
        sb.append("<|im_start|>system\n")
        sb.append("You are ").append(name).append(", a voice assistant. Reply in one very short sentence, under 18 words. No lists.<|im_end|>\n")
        for ((role, text) in past) {
            sb.append("<|im_start|>").append(role).append("\n").append(text).append("<|im_end|>\n")
        }
        sb.append("<|im_start|>user\n").append(question).append("<|im_end|>\n<|im_start|>assistant\n")
        return sb.toString()
    }

    /** Starts generating. Pieces of the answer arrive through [onPart] (text, finished). */
    fun askStream(name: String, past: List<Pair<String, String>>, question: String, onPart: (String, Boolean) -> Unit): Boolean {
        val m = llm ?: return false
        sink = onPart
        val prompt = buildPrompt(name, past, question)
        try {
            val methods = LlmInference::class.java.methods.filter { it.name == "generateResponseAsync" }
            // 1) listener was given in the options: just start it
            if (listenerInOptions) {
                val one = methods.firstOrNull { it.parameterTypes.size == 1 }
                if (one != null) { one.invoke(m, prompt); return true }
            }
            // 2) newer API: generateResponseAsync(prompt, listener)
            val two = methods.firstOrNull { it.parameterTypes.size == 2 && it.parameterTypes[0] == String::class.java }
            if (two != null) { two.invoke(m, prompt, listenerProxy(two.parameterTypes[1])); return true }
        } catch (e: Throwable) { /* fall through to the plain call */ }
        // 3) no streaming available: generate in one piece on a worker thread
        val t = Thread {
            val text = try { m.generateResponse(prompt) } catch (e: Throwable) { null }
            sink?.invoke(text ?: "", true)
        }
        t.isDaemon = true
        t.start()
        return true
    }

    @Synchronized
    fun close() {
        try { llm?.close() } catch (e: Throwable) { }
        llm = null
    }
}
