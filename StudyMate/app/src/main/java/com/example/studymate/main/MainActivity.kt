package com.example.studymate.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    private val TAG = "MainActivity"
    
    // Track previous destination for "from X" context
    private var previousDestinationLabel: CharSequence? = null

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

    /**
     * Sends an accessibility announcement using the native Android framework.
     * This will be spoken by TalkBack if enabled.
     */
    private fun announceForAccessibility(message: String) {
        val accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        
        if (accessibilityManager.isEnabled) {
            val event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT)
            event.text.add(message)
            event.className = MainActivity::class.java.name
            event.packageName = packageName
            
            accessibilityManager.sendAccessibilityEvent(event)
            Log.d(TAG, "Accessibility Announcement Sent: \"$message\"")
        } else {
            Log.d(TAG, "Accessibility disabled, skipping announcement: \"$message\"")
        }
    }
}
