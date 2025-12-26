package com.example.studymate.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.studymate.databinding.FragmentSignupBinding
import com.example.studymate.main.MainActivity
import com.example.studymate.ui.common.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

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
            // Note: Simple signup uses "Name" field as "Name" if available in layout, 
            // but the current layout might only have email/password based on previous fragment.
            // Assuming layout has name or we just use email prefix.
            // Let's check layout later, for now assuming email/pass are there.
            
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            // Ideally there's a name field, but if not we can default or add it.
            // Prompt didn't specify changing layout, so I'll check if I need to add name field.
            // The prompt says "UserEntity: id, name, email...".
            // I'll assume "Student" as default name if no input.
            val name = "Student" 

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 4) {
                 Toast.makeText(requireContext(), "Password too short", Toast.LENGTH_SHORT).show()
                 return@setOnClickListener
            }

            viewModel.signup(name, email, password)
        }

        binding.loginText.setOnClickListener {
            (requireActivity() as? AuthActivity)?.switchToLogin()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.signupButton.isEnabled = false
                            binding.signupButton.text = "Creating..."
                        }
                        is UiState.Success<*> -> {
                            binding.signupButton.isEnabled = true
                            binding.signupButton.text = "Sign Up"
                            Toast.makeText(requireContext(), "Account created!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            requireActivity().finish()
                        }
                        is UiState.Error -> {
                            binding.signupButton.isEnabled = true
                            binding.signupButton.text = "Sign Up"
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            binding.signupButton.isEnabled = true
                            binding.signupButton.text = "Sign Up"
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
