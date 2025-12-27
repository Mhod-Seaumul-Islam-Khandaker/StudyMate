package com.example.studymate.ui.progress

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.studymate.databinding.FragmentProgressBinding
import com.example.studymate.ui.common.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProgressFragment : Fragment() {

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProgressViewModel by viewModels()
    private val TAG = "ProgressFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: Inflating layout")
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.progressPercentage.collectLatest { percentage ->
                        Log.d(TAG, "Progress updated to UI: $percentage")
                        binding.progressIndicator.progress = percentage
                        binding.progressText.text = "$percentage%"
                        
                        binding.motivationalText.text = when {
                            percentage == 100 -> "Outstanding! All complete!"
                            percentage >= 75 -> "Great job! Almost there!"
                            percentage >= 50 -> "Halfway through! Keep going!"
                            percentage >= 25 -> "Good start! Keep it up!"
                            else -> "Let's get started!"
                        }
                    }
                }
                
                launch {
                    viewModel.completedItems.collectLatest { completed ->
                        viewModel.totalItems.collectLatest { total ->
                            binding.statsText.text = "$completed / $total completed"
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
