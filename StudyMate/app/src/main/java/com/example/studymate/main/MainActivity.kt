package com.example.studymate.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.studymate.R
import com.example.studymate.auth.AuthActivity
import com.example.studymate.data.repository.UserRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.log

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: starting ")
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
        
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d(TAG, "Navigated to ${destination.label}")
        }
    }
}
