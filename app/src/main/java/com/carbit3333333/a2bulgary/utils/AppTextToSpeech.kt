package com.carbit3333333.a2bulgary.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class AppTextToSpeech(context: Context) {

    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    init {
        textToSpeech = TextToSpeech(context.applicationContext) { status ->
            isInitialized = status == TextToSpeech.SUCCESS
            if (isInitialized) {
                textToSpeech?.setSpeechRate(0.9f)
            }
        }
    }

    fun speakBulgarian(text: String) {
        speak(text, Locale.forLanguageTag("bg-BG"))
    }

    fun speakRussian(text: String) {
        speak(text, Locale.forLanguageTag("ru-RU"))
    }

    fun speak(text: String) {
        speakBulgarian(text)
    }

    fun speak(text: String, locale: Locale) {
        if (!isInitialized || text.isBlank()) return

        val result = textToSpeech?.setLanguage(locale)
        val isLanguageReady = result != TextToSpeech.LANG_MISSING_DATA &&
            result != TextToSpeech.LANG_NOT_SUPPORTED

        if (!isLanguageReady) return

        textToSpeech?.stop()
        textToSpeech?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "lesson_tts_${System.currentTimeMillis()}",
        )
    }

    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
    }
}
