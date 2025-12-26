package com.example.studymate.ui.dashboard

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
import com.example.studymate.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()
    private val TAG = "DashboardFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Setting up UI")
        
        setupObservers()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.pendingCount.collectLatest { count ->
                        binding.pendingTasksText.text = "$count pending"
                    }
                }
                
                launch {
                    viewModel.completedCount.collectLatest { count ->
                        binding.completedTasksText.text = "$count tasks completed"
                    }
                }

                launch {
                    viewModel.overdueCount.collectLatest { count ->
                        binding.overdueTasksText.text = "$count overdue tasks"
                        if (count > 0) {
                            binding.overdueTasksText.setTextColor(android.graphics.Color.RED)
                        } else {
                            binding.overdueTasksText.setTextColor(android.graphics.Color.GRAY)
                        }
                    }
                }

                launch {
                    viewModel.progress.collectLatest { percent ->
                        binding.progressPercent.text = "$percent%"
                        // In a real implementation, we would update a CircularProgressBar view here
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
