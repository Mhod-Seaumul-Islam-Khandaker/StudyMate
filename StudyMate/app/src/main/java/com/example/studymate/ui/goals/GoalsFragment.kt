package com.example.studymate.ui.goals

import android.app.AlertDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studymate.R
import com.example.studymate.data.model.GoalEntity
import com.example.studymate.databinding.FragmentGoalsBinding
import com.example.studymate.ui.common.UiState
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GoalsViewModel by viewModels()
    private val TAG = "GoalsFragment"
    private lateinit var adapter: GoalAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: Inflating layout")
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = GoalAdapter(
            onGoalChecked = { goal, isChecked ->
                viewModel.updateGoalStatus(goal, isCompleted = isChecked)
            },
            onGoalClicked = { goal ->
                showEditGoalDialog(goal)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        // Handle loading/error states if needed
                    }
                }
                launch {
                    viewModel.goals.collectLatest { goals ->
                        Log.d(TAG, "Submitting ${goals.size} goals to adapter")
                        adapter.submitList(goals)
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            showAddGoalDialog()
        }
    }

    private fun showAddGoalDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_goal, null)
        
        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val statementInput = dialogView.findViewById<TextInputEditText>(R.id.editStatement)
                
                val statement = statementInput.text.toString()
                if (statement.isBlank()) return@setPositiveButton

                viewModel.addGoal(statement)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditGoalDialog(goal: GoalEntity) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_goal, null)
        
        // Pre-fill fields
        val statementInput = dialogView.findViewById<TextInputEditText>(R.id.editStatement)
        val titleText = dialogView.findViewById<android.widget.TextView>(androidx.core.R.id.title)
        // Note: The dialog XML has a TextView "New Goal", but standard ID might not be reachable easily if it doesn't have an ID in XML or if we reuse layout.
        // Actually the layout has a TextView with text "New Goal". We can find it and change text if we want, but user didn't strictly ask.
        // But for clarity, let's leave it or try to update if possible.
        
        statementInput.setText(goal.statement)

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val statement = statementInput.text.toString()
                if (statement.isBlank()) return@setPositiveButton

                val updatedGoal = goal.copy(
                    statement = statement
                )
                
                viewModel.updateGoal(updatedGoal)
            }
            .setNeutralButton("Delete") { _, _ ->
                viewModel.deleteGoal(goal)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
