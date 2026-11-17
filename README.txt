VERSION 3.3 (Telugu / Hindi commands run on the phone)
- "కాల్ టు వెంకటేష్", "వెంకటేష్ కి కాల్ చేయి", "ఇన్‌స్టాగ్రామ్ ఓపెన్ చేయి", "యూట్యూబ్ తెరవండి", "रिया वेंकटेश को कॉल करो", "यूट्यूब खोलो" ... are understood on the phone at once (no internet, no Gemini wait): call / open / play / lock / unlock.
- The assistant's name written in Telugu or Hindi script (రియా, रिया) is now recognised as the wake name. Before, only English letters matched, so a command with the name in Telugu was ignored.
- Names written in Telugu / Hindi script are matched with saved contacts by sound (వెంకటేష్ = Venkatesh), also when the contact itself is saved in Telugu.
- If "Say the name before commands" is ON in Settings, say the name (Riya / రియా) first, or switch it OFF. Other Telugu sentences still go to Gemini.

VERSION 3.2 (she finishes what she is saying; only "stop" cuts her off)
- While she is speaking, ordinary talk, other commands and noise can no longer cut her off. The "stop" listener now accepts only the word "stop" (alone or with her name: "Riya, stop"), so it no longer fires falsely.
- A new reply that comes while she is talking waits and is spoken AFTER the current one (before: it cut the current sentence). Streamed answers keep going sentence by sentence; a new command heard between two sentences no longer drops the rest of the answer.
- After "stop" she is quiet at once and listens again right away; the next command works even without her name for 8 seconds.
- Not changed: an incoming call is still announced at once (it is urgent).

VERSION 3.1 (unlock by voice made reliable)
- "Riya, unlock phone" is understood in more spellings (unlock, un lock, unlocked, "on lock ...") and is checked FIRST, so it can no longer be taken for "phone ..." (a call).
- When she cannot unlock she now SAYS the exact reason (before: always "save your PIN", even when the PIN was saved): no PIN saved | auto-unlock switched off | Accessibility off | Voice Match not trained (default "Only my voice can unlock" is ON: train your voice, or switch it off in Settings > Phone unlock) | not a number PIN | number pad not found | wrong PIN.
- The number pad is found on more phone brands (more key names, "2 ABC" style keys, any lock-screen window), looked for at once instead of after a fixed wait, retried once if a key could not be pressed (never after a wrong PIN), and the voice sheet is closed twice so it cannot cover the pad.
- Unlocking can no longer get stuck "busy" (25 second safety reset), so it works again and again.

VERSION 3.0 (speed: lock screen and everywhere)
- LOCK / UNLOCK / VIDEO / SIGNAL CHANGES NO LONGER MAKE HER DEAF: the offline speech model is now loaded once when Riya starts and shared (it used to be reloaded from storage every time the screen locked, unlocked, a video started or the signal changed - several seconds of not hearing you). Switching ears now takes a fraction of a second.
- FASTER FINISH: when you say her name plus a command she acts about 100 ms sooner, and the ears run at audio priority.
- INSTANT "HEARD YOU": a tiny buzz the moment she hears her name.
- LOCK SCREEN: the voice sheet was being launched again on every partial word; now it is launched once.
- SHORTER WAITS: after unlock (0.12 s instead of 0.4 s), pending command after unlock (0.45 s), offline-ears retry (0.4 s), Google silence limits, offline-voice fallback (1.3 s), slightly faster speaking speed (1.1).

VERSION 2.9 (next one every time, payment commands)
- "Riya, next one" / "next" / "previous" / "pause" / "mute": the command is now understood in many more spellings (next on, next wan, nextone, nest one, next reels, next one.), and the best of the engine's guesses is used, not just the first.
- While a video is playing and talking, the command is also taken from the live words (no need to wait for the sentence to end), and talk that follows it in the same sentence is ignored. This is the likely reason it worked once and then not again.
- The main screen's yellow text now says what she did: "Next: swiped the reel" / "pressed the player button" / "sent the media key" (and tells you if Accessibility is OFF).
- PAYMENTS: "open Google Pay / Phone Pay send ..." (two-word app names) never matched before: fixed. Also understood now: rupee sign, "500 rs", "500/-", "please", "pay team" (Paytm mis-heard). The "yes" step accepts more words, listens to all of the engine's guesses and asks again (twice) instead of cancelling on noise.

VERSION 2.8 (unlock by voice, Maya / name fixes)
- VOICE TRAINING is now 10 short sentences of 3 words each ("Open the camera", "Maya, next one" ...). Train again after updating.
- REELS / SHORTS: next / previous use a stronger swipe, YouTube Shorts is detected better, mute works even on phones that ignore the mute key, and mis-heard words (nest, paws, mood ...) are corrected.
- UNLOCK BY VOICE: Settings > Phone unlock (PIN) > type your phone PIN once > SAVE PIN. The PIN is stored encrypted (Android Keystore) on the phone only; it is NOT in the source code. When the phone is locked and a command needs it open (call, open app, play, WhatsApp, payment, Wi-Fi ...) Riya wakes the screen, swipes up, types the PIN and then does the task. "Riya, unlock" also works. Music commands (next / pause) never need unlocking.
- SAFETY: "Only my voice can unlock" is ON by default and needs Voice Match (Settings > TRAIN MY VOICE, then switch "Only respond to my voice" ON). Switch it off only if you accept that anyone who says the name can unlock the phone.
- PAYMENTS: if the payment app asks to be unlocked (app PIN / passcode / screen lock) Riya types the same PIN, at most twice. She never types the UPI PIN and refuses any screen that mentions UPI, a bank or an amount. She still stops at the UPI PIN screen for you to type it.
- NEEDS: Accessibility ON, number-PIN lock screen (not pattern / fingerprint only). Some phone brands hide the lock-screen keypad from apps; then Riya says she could not unlock and waits for you.
- NAME NOT HEARD (e.g. "Maya, next"): extra spellings (maia, mia, mya, "my a" ...) are accepted; while music plays they are accepted only together with next / previous / pause / play. If she still ignores you, the main screen now shows what she heard (yellow text). YouTube: she presses the player's own "Next video" button when it is on screen, otherwise the media Next key.

VERSION 2.7 (silent calls, Wi-Fi / Bluetooth, quiet while videos play)
- DURING A CALL Riya is completely silent: she never speaks and ignores ordinary talk. She keeps listening with the on-phone ears (Google speech is not used, so the call keeps the microphone) and reacts ONLY when you say her name first:
  "Riya, cut the call" (also: end the call, hang up) | "Riya, speaker on" / "speaker off" | "Riya, video call" (turns on video) | "Riya, turn on Wi-Fi" / "turn off Wi-Fi" | "Riya, turn on Bluetooth" / "turn off Bluetooth".
  Feedback is a small buzz: one buzz = done, two buzzes = could not (the reason is shown on the main screen).
  Works for normal phone calls and for WhatsApp / Meet style calls.
- WI-FI / BLUETOOTH: "Riya, turn on Wi-Fi", "turn off Bluetooth" also work any time (not only in calls). Android does not let apps switch them directly any more, so Riya opens Quick Settings and presses the tile for you (Accessibility must be ON, phone unlocked). Before this, "turn off Bluetooth" could stop Riya herself: fixed.
- QUIET WHILE VIDEO / MOVIE / MUSIC PLAYS: when anything is playing (YouTube, a movie in any app, music) Riya stays muted. Nothing is accepted without her name: no bare next / pause / stop, no music guard, no panel. Say "Riya" and then the command (or "Riya, next"). Turn it off in Features > Music by voice > "Stay quiet while a video / movie / music plays".
- LIMITS you should know: (1) some phones silence the microphone for other apps during a call, then she cannot hear you until the call ends; (2) video during a normal phone call only works if your phone, SIM and network offer video calling, otherwise there is no video button to press; (3) when the call is on speaker, the other person's voice can reach the microphone: turn on Voice Match so only you can give commands; (4) Quick Settings tiles differ per phone brand, so Wi-Fi / Bluetooth may need a small adjustment on some models.

VERSION 2.6 (instant caller name, weak-signal mode, new WhatsApp-style app)
- NEW LOOK: top bar with live status and a network chip, four swipeable screens with a bottom bar like WhatsApp: Riya (live chat bubbles + animated glow), Features (stories row + searchable list of EVERYTHING Riya can do, tap a feature to see how to use it, each shows Ready / Set up), Setup (progress bar + all steps + offline brain), Settings.
- INCOMING CALLS, 0-1 SECOND: contacts are kept in memory, so the name is found instantly. Order: saved name, then likely name (same number saved with another prefix, or the name the call log remembers), then the number digit by digit. The sentence is short ("Vinod is calling") and uses the on-phone voice, which starts immediately even with no signal. The microphone is paused only after the voice has started.
- FASTEST START: Setup > "Instant caller name" (choose Riya as call screening app). Android then gives Riya the number BEFORE the phone rings. Riya never blocks or changes a call. Without it the normal phone broadcast is used (waits at most a quarter of a second for the number).
- WEAK / NO SIGNAL: Riya watches the connection (Android's own report plus the speed of her real requests). When it is weak she automatically (1) hears you with the on-phone Vosk ears instead of Google speech, (2) speaks with the on-phone voice instead of a network voice, (3) answers non-live questions with the on-phone brain at once, and for live questions waits longer on the network. A network voice that has not started within 1.8 seconds is repeated with the on-phone voice. When the signal is good again she switches back by herself. The chip at the top shows Online / Weak / Offline.
- For the best weak-signal result: install the offline brain once (Setup) and keep the offline hearing models in the build.

VERSION 2.5: "Riya, play next song" / "play previous song" (also "next song", "skip song", "previous song", "go back one song") work in any music app and also by the music guard without the name. If music was paused it starts playing; if no music app is open it opens one.

VERSION 2.4 (music by voice + instant answers)
- MUSIC: "Riya, stop music" / "pause music" pauses whatever is playing, in ANY music app (Spotify, JioSaavn, PLAYit, YouTube Music, Gaana, Wynk ...). "Riya, start playing music" resumes it, or opens your music app and starts playing by itself (Spotify first). Name another app any time: "Riya, play music on JioSaavn". Choose your default in the app: Music by voice > CHOOSE MUSIC APP. Small buzz instead of a spoken reply, so your music is not interrupted.
- MUSIC GUARD: while music plays, a tiny word-list ear listens for only "stop music", "pause music", "next song", "previous song". Crowd and traffic talk cannot become any other command. The name is not needed for these.
- ONLY MY VOICE: for the best protection in a crowd, turn on Voice Match (TRAIN MY VOICE once). Then the music guard and every other command accept only your voice. Without Voice Match, anyone who says "stop music" near the phone can stop it. Keep the phone near you, or use earphones with a mic, in very loud places.
- Needs: Accessibility ON (backup for apps that ignore the pause/play keys), Display over other apps, and the phone unlocked to open a music app.
- INSTANT ANSWERS: Gemini now streams its answer and Riya starts speaking the first sentence right away (about one second) instead of waiting for the whole answer. Google first, offline brain only if Google fails.

VERSION 2.3 (Google first, faster, Siri-like voice)
- Every answer is asked from Google Gemini FIRST (with Google Search for live facts and on the lock screen). The offline brain answers only if Gemini fails, times out (9 seconds) or there is no internet. If the offline brain is not installed, a keyless web answer is tried, then a spoken message.
- Faster: Gemini "thinking" switched off, shorter answers, strict time limit, so a slow network never leaves you waiting.
- Voice: Riya now picks a Siri-like voice automatically (natural US English, female, network/neural when available) at a calm speed. Apple's real Siri voice cannot be installed on Android. To change: main screen > Riya's voice > CHOOSE VOICE. The first entry is "Auto: Siri-like", the ones marked with a star are the closest, the rest are below.
- For the best voice: Phone Settings > Languages > Text-to-speech > Google > install "English (United States)" voices and download the high-quality ones.
- Everyone's saved voice is reset once so the new default applies. Pick again if you want another.

VERSION 2.2 (pay by voice)
- "Riya, open PhonePe send Vinod 500" (also Paytm, Google Pay / gpay, BHIM; also "send 500 to Vinod on gpay"). Several contacts with the same name: she reads numbers, you say the number. She then repeats name and amount and waits for "yes". After yes she opens the app, finds the person, types the amount, presses pay and stops at the UPI PIN screen. YOU type the PIN; Riya never reads, hears or stores it. Limit 1,00,000 rupees. Say "stop" to cancel. Needs Accessibility ON.
- Payment apps change their screens often. If she gets stuck, the main screen shows the labels she saw (yellow text); send them to the developer to adjust the steps.

VERSION 2.1 (videos)
- While a video / reel is playing Riya switches to the offline mic, which does not pause the video, and ignores voices coming from the video (they are only commands if you say her name or Voice Match recognises you).
- Reels / Shorts control (needs Accessibility ON): next, previous, pause, play, mute, unmute. First say "Riya, next"; for the next 25 seconds just say "next" / "previous". With Voice Match on, no name is needed at all. These give a small buzz and no spoken answer, so the video is not interrupted.

VERSION 2 CHANGES
- Lock screen / online: questions are answered from the internet (Gemini + Google Search if you have a Gemini key; otherwise weather, Wikipedia and DuckDuckGo with no key). The offline brain is only the fallback.
- Google-style voice panel: four coloured bars that follow your voice, live transcript and answer. Overlay on an unlocked phone, full sheet on the lock screen. While typing it becomes a small pill at the top.
- Type by voice: no spoken messages any more (small buzz only), types exactly at the cursor / the box you last tapped, never jumps to another box.
- Voice Match: fixed lost voice prints, matches against every training sentence, new Strict/Normal/Relaxed setting. Please TRAIN MY VOICE once again after updating.
- New main screen with animated glow and bars.

BUILD THE APK
Upload this folder to GitHub, open the Actions tab, wait for "Build APK", download the Riya-APK artifact.
(Or open in Android Studio > Build > Build APK(s).)

FIRST RUN - tap the buttons in the app in order
1 Allow microphone, contacts, calls & notifications
2 Allow lock screen (Device admin)
3 Allow display over other apps
4 Allow background running (battery)  -> choose Allow
5 Allow Do Not Disturb access for Riya (this lets it mute the listening beep)
6 Enable Accessibility -> Riya -> On (only used to press Send in WhatsApp)
   (Android 13+: if it is greyed out, Settings > Apps > Riya > 3-dot menu > Allow restricted settings)
7 (Offline chat) In the app, 'Offline voice chat' card: tap DOWNLOAD (about 1.6 GB, Wi-Fi) once, or CHOOSE FILE if you already have the model file
8 Type the name (e.g. Janu) and tap START

COMMANDS (start with the name you set in the app, default Riya)
open <ANY app name>  (e.g. open Instagram, open Settings, open PhonePe)
lock screen | open camera
play <song> | play <song> on YouTube
call <name>   (if several match, Riya reads them numbered - just say the number, e.g. "three", no name needed)
send WhatsApp to <name> saying <message>
flashlight on / off
volume up / down / max / mute / 50
what's the time | what's the date
stop


NEW IN THIS VERSION
- Colourful screen with a live orb: Listening / Thinking / Speaking, plus what it heard and replied.
- Setup list shows a green tick for every step already done.
- Faster: one reused speech recogniser, instant restart, picks a contact the moment you say the number.
- Talks with you: after Riya speaks you can answer WITHOUT the name for 8 seconds.
- Small talk works offline (hello, how are you, jokes, battery, thanks, bye).
- Optional AI brain: paste an Anthropic API key in the app and Riya answers any question out loud.

OFFLINE VOICE CHAT
- Hearing: Vosk speech recognition, bundled in the APK (the GitHub build downloads the two speech models).
- Answers: Qwen2.5-1.5B running on the phone (MediaPipe). Say any question; no name needed (wake-word switch is OFF by default).
- Phone commands (open app, call, WhatsApp, flashlight...) still work the same way.

WORKS WITHOUT INTERNET: open apps, call, WhatsApp compose, flashlight, volume, time/date/battery, camera, lock screen,
close all apps, home/back/recents/notifications/screenshot, alarm and timer, wifi/bluetooth settings, local music, and chat (offline brain).
NEEDS INTERNET: YouTube/Spotify, sending the WhatsApp message itself, weather/news-type questions.

BRAINS: live questions (weather/news/score/price) use the online AI key when internet is on; everything else uses the on-phone brain, which also works offline.

SPEED: answers start speaking sentence-by-sentence while the model is still thinking; speech end is detected after about half a second. For the quickest answers use the FAST BRAIN (0.5B) button.

INCOMING CALLS: Riya reads out the caller's contact name, or the number digit by digit if it is not saved (works offline). Say 'answer' or 'reject'. Allow the Phone permissions when asked.

VOICE MATCH (only your voice)
- In the app, 'Voice Match' card: tap TRAIN MY VOICE and read the 5 sentences Riya says (quiet room, normal voice).
- After that Riya ignores other voices. If she rejects you, the screen shows the match score; train again in a quiet place. REMOVE deletes your voice.
- The GitHub build downloads the speaker model (vosk-model-spk-0.4) automatically.

TYPE BY VOICE
- Put the cursor in any text box (WhatsApp, mail, notes ...) and say: Riya, start typing. Then speak; Riya types your words with capitals, punctuation and spelling fixes.
- Say: send | new line | delete that | clear text | stop typing. Spoken punctuation works: comma, full stop, question mark.
- One sentence only: Riya, type I will be late today.
- Chats: 'open WhatsApp Ramu' or 'open WhatsApp Business Ramu' opens the chat; then speak your message and say 'send'.
- Needs Accessibility switched ON. For best accuracy use Google hearing (offline hearing OFF) and a Gemini key for smart auto-correct.

WHATSAPP CALLS
- 'Riya, open WhatsApp Business Ramu call'  -> normal WhatsApp voice call.
- 'Riya, open WhatsApp Ramu video call' / 'video call Ramu' / 'call Ramu on WhatsApp' -> video or voice call in WhatsApp or WhatsApp Business.
- Works best for contacts that are on WhatsApp (they have a call shortcut). Otherwise Riya opens the chat and taps the call button (needs Accessibility ON).

SEVERAL PEOPLE WITH THE SAME NAME
- 'Riya, open WhatsApp Rama video call' with contacts Rama, Ramya Krishna and Rama Reddy: Riya reads them numbered ('1, Rama. 2, Ramya Krishna. 3, Rama Reddy. Say the number to start the video call'). Say 'two' (or the name) and she does it. Say 'cancel' to stop.
- Names that sound alike (Rama / Ramya / Ramu) are all offered. Works for phone calls, WhatsApp / WhatsApp Business chats, voice calls, video calls and messages.
