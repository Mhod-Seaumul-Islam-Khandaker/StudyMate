package com.example.studymate.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.studymate.databinding.FragmentSignupBinding
import com.example.studymate.main.MainActivity
import com.example.studymate.utils.SupabaseClient
// ✅ FIX 1: Add these Supabase imports
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signupButton.setOnClickListener {
            val emailText = binding.emailInput.text.toString().trim() // Renamed to avoid conflict
            val passwordText = binding.passwordInput.text.toString().trim()

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (passwordText.length < 6) {
                Toast.makeText(
                    requireContext(),
                    "Password should be at least 6 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Show loading state
            binding.signupButton.isEnabled = false
            binding.signupButton.text = "Creating account..."

            // Perform Supabase signup with auth module
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // ✅ FIX 2: Updated syntax for Supabase v3
                    // The .auth property is now visible because of the import above
                    SupabaseClient.client.auth.signUpWith(Email) {
                        email = emailText
                        password = passwordText
                    }

                    // Signup successful - auto login after signup
                    SupabaseClient.client.auth.signInWith(Email) {
                        email = emailText
                        password = passwordText
                    }

                    withContext(Dispatchers.Main) {
                        binding.signupButton.isEnabled = true
                        binding.signupButton.text = "Sign Up"

                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        binding.signupButton.isEnabled = true
                        binding.signupButton.text = "Sign Up"

                        val errorMessage = when {
                            e.message?.contains("User already registered") == true ->
                                "Account already exists. Please login instead."
                            else -> "Signup failed: ${e.message}"
                        }

                        Toast.makeText(
                            requireContext(),
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        // Navigate to login fragment
        binding.loginText.setOnClickListener {
            (requireActivity() as? AuthActivity)?.switchToLogin()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
