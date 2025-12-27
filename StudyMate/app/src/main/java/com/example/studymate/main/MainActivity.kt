package com.example.studymate.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.studymate.R
import com.example.studymate.auth.AuthActivity
import com.example.studymate.data.repository.UserRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    @Inject
    lateinit var userRepository: UserRepository

    private val TAG = "MainActivity"
    
    // Track previous destination for "from X" context
    private var previousDestinationLabel: CharSequence? = null

    // TextToSpeech for non-TalkBack scenarios
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check authentication
        if (userRepository.getLoggedInUserId() == -1L) {
            Log.d(TAG, "onCreate: No user logged in, redirecting to AuthActivity")
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        Log.d(TAG, "onCreate: Activity started")
        setContentView(R.layout.activity_main)

        // Initialize TTS
        tts = TextToSpeech(this, this)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        
        bottomNavigationView.setupWithNavController(navController)
        
        // Accessibility Navigation Listener
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val currentLabel = destination.label
            Log.d(TAG, "Navigated to $currentLabel")
            
            // Only announce if we have a previous destination (skips initial load)
            // and if the label is actually different
            if (previousDestinationLabel != null && currentLabel != null && previousDestinationLabel != currentLabel) {
                val announcement = "Navigating to $currentLabel from $previousDestinationLabel"
                announceForAccessibility(announcement)
            }
            
            // Update state for next navigation
            previousDestinationLabel = currentLabel
        }
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

    /**
     * Announces a message. 
     * If TalkBack (Touch Exploration) is ON, sends an AccessibilityEvent.
     * If TalkBack is OFF, speaks using TextToSpeech.
     */
    private fun announceForAccessibility(message: String) {
        val accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        
        // Check specifically for Touch Exploration (TalkBack) to avoid double-speaking
        // if another non-speaking accessibility service is running.
        if (accessibilityManager.isEnabled && accessibilityManager.isTouchExplorationEnabled) {
            val event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT)
            event.text.add(message)
            event.className = MainActivity::class.java.name
            event.packageName = packageName
            
            accessibilityManager.sendAccessibilityEvent(event)
            Log.d(TAG, "Accessibility Announcement (Event): \"$message\"")
        } else {
            // Fallback to TTS if TalkBack is not driving the feedback
            if (isTtsInitialized) {
                tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "NAV_ANNOUNCEMENT")
                Log.d(TAG, "Accessibility Announcement (TTS): \"$message\"")
            } else {
                Log.w(TAG, "TTS not ready, skipping announcement: \"$message\"")
            }
        }
    }

    override fun onDestroy() {
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
        super.onDestroy()
    }
}
