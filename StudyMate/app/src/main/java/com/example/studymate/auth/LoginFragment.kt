package com.example.studymate.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.auth
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.studymate.databinding.FragmentLoginBinding
import com.example.studymate.main.MainActivity
import com.example.studymate.utils.SupabaseClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var loginJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            loginJob?.cancel()
            binding.loginButton.isEnabled = false
            binding.loginButton.text = "Logging in..."

            loginJob = CoroutineScope(Dispatchers.IO).launch {
                try {
                    // NEW (v3) Syntax
                    SupabaseClient.client.auth.signInWith(Email) {
                        this.email = email
                        this.password = password
                    }


                    withContext(Dispatchers.Main) {
                        binding.loginButton.isEnabled = true
                        binding.loginButton.text = "Login"

                        Toast.makeText(
                            requireContext(),
                            "Login successful!",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        binding.loginButton.isEnabled = true
                        binding.loginButton.text = "Login"

                        val errorMessage = when {
                            e.message?.contains("Invalid login credentials") == true ->
                                "Invalid email or password"
                            e.message?.contains("Email not confirmed") == true ->
                                "Please verify your email first"
                            else -> "Login failed: ${e.message ?: "Unknown error"}"
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

        binding.forgotPasswordText.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter your email first",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // ✅ CORRECT RESET PASSWORD METHOD for Supabase v3.x
                    SupabaseClient.client.auth.resetPasswordForEmail(email)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Password reset email sent! Check your inbox.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Failed to send reset email: ${e.message ?: "Unknown error"}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.signupText.setOnClickListener {
            (requireActivity() as? AuthActivity)?.switchToSignup()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loginJob?.cancel()
        _binding = null
    }
}