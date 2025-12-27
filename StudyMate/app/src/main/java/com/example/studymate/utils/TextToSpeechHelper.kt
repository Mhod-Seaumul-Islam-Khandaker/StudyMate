package com.example.studymate.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextToSpeechHelper @Inject constructor(
    @ApplicationContext private val context: Context
) : TextToSpeech.OnInitListener {

    private val TAG = "TextToSpeechHelper"
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "TTS: Language not supported")
            } else {
                isTtsInitialized = true
                Log.d(TAG, "TTS: Initialized successfully")
            }
        } else {
            Log.e(TAG, "TTS: Initialization failed")
        }
    }

    fun speak(message: String) {
        // If TalkBack is enabled, prefer AccessibilityEvent to avoid conflict/duplication
        if (accessibilityManager.isEnabled && accessibilityManager.isTouchExplorationEnabled) {
            val event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT)
            event.text.add(message)
            event.className = this::class.java.name
            event.packageName = context.packageName
            accessibilityManager.sendAccessibilityEvent(event)
            Log.d(TAG, "Announced via AccessibilityEvent: $message")
        } else {
            // Fallback to TTS
            if (isTtsInitialized) {
                tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "TTS_MESSAGE")
                Log.d(TAG, "Spoken via TTS: $message")
            } else {
                Log.w(TAG, "TTS not ready, cannot speak: $message")
            }
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
