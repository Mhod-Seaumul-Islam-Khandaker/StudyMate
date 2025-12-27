package com.example.studymate.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.studymate.R
import com.example.studymate.auth.AuthActivity
import com.example.studymate.data.repository.UserRepository
import com.example.studymate.utils.TextToSpeechHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var ttsHelper: TextToSpeechHelper

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
                ttsHelper.speak(announcement)
            }
            
            // Update state for next navigation
            previousDestinationLabel = currentLabel
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
             ttsHelper.shutdown()
        }
    }
}
