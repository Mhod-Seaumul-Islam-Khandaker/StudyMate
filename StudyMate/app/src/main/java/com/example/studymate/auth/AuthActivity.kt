package com.example.studymate.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.studymate.R
import com.example.studymate.databinding.ActivityAuthBinding
import com.example.studymate.utils.TextToSpeechHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    @Inject
    lateinit var ttsHelper: TextToSpeechHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)

        // Start with LoginFragment by default
        if (savedInstanceState == null) {
            loadFragment(LoginFragment(), "Login Screen")
        }
    }

    /**
     * Load a fragment into the authFragmentContainer
     */
    fun loadFragment(fragment: Fragment, screenName: String = "") {
        supportFragmentManager.beginTransaction()
            .replace(R.id.authFragmentContainer, fragment)
            .commit()
            
        if (screenName.isNotEmpty()) {
            ttsHelper.speak("Navigating to $screenName")
        }
    }

    /**
     * Switch to signup fragment
     */
    fun switchToSignup() {
        loadFragment(SignupFragment(), "Create Account Screen")
        supportActionBar?.title = "Create Account"
    }

    /**
     * Switch to login fragment
     */
    fun switchToLogin() {
        loadFragment(LoginFragment(), "Login Screen")
        supportActionBar?.title = "Welcome Back"
    }
}