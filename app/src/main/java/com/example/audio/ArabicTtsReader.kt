package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class ArabicTtsReader(context: Context) {

    private var tts: TextToSpeech? = null
    private var isReady = false
    private var onUtteranceCompleted: ((String) -> Unit)? = null

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _arabicSupported = MutableStateFlow(true)
    val arabicSupported: StateFlow<Boolean> = _arabicSupported.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val engine = tts ?: return@TextToSpeech
                val res = engine.setLanguage(Locale("ar"))
                isReady = true
                _arabicSupported.value =
                    res != TextToSpeech.LANG_MISSING_DATA && res != TextToSpeech.LANG_NOT_SUPPORTED
                engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                        if (utteranceId != null) {
                            onUtteranceCompleted?.invoke(utteranceId)
                        }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        _isSpeaking.value = false
                    }
                })
            } else {
                _arabicSupported.value = false
            }
        }
    }

    fun speak(
        text: String,
        utteranceId: String = "noor_tts_${System.currentTimeMillis()}",
        speechRate: Float = 0.95f,
        onDone: (() -> Unit)? = null
    ) {
        val engine = tts ?: return
        if (!isReady) return
        onUtteranceCompleted = { completedId ->
            if (completedId == utteranceId) {
                onDone?.invoke()
            }
        }
        engine.setSpeechRate(speechRate.coerceIn(0.6f, 1.5f))
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        onUtteranceCompleted = null
        tts?.stop()
        _isSpeaking.value = false
    }

    fun release() {
        onUtteranceCompleted = null
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
