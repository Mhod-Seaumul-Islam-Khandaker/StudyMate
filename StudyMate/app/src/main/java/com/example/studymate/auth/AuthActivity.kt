package com.example.studymate.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.studymate.R
import com.example.studymate.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)

        // Start with LoginFragment by default
        if (savedInstanceState == null) {
            loadFragment(LoginFragment())
        }
    }

    /**
     * Load a fragment into the authFragmentContainer
     */
    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.authFragmentContainer, fragment)
            .commit()
    }

    /**
     * Switch to signup fragment
     */
    fun switchToSignup() {
        loadFragment(SignupFragment())
        supportActionBar?.title = "Create Account"
    }

    /**
     * Switch to login fragment
     */
    fun switchToLogin() {
        loadFragment(LoginFragment())
        supportActionBar?.title = "Welcome Back"
    }
}