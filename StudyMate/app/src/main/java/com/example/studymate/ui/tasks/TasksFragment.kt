package com.example.studymate.ui.tasks

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studymate.R
import com.example.studymate.data.model.Priority
import com.example.studymate.databinding.FragmentTasksBinding
import com.example.studymate.ui.common.UiState
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TasksViewModel by viewModels()
    private val TAG = "TasksFragment"
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: Inflating layout")
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(
            onTaskChecked = { task, isChecked ->
                viewModel.updateTaskStatus(task, isCompleted = isChecked)
            },
            onTaskClicked = { task ->
                // TODO: Edit task dialog
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
                    viewModel.tasks.collectLatest { tasks ->
                        Log.d(TAG, "Submitting ${tasks.size} tasks to adapter")
                        adapter.submitList(tasks)
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_task, null)
        
        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val titleInput = dialogView.findViewById<TextInputEditText>(R.id.editTitle)
                val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroupPriority)
                val datePicker = dialogView.findViewById<DatePicker>(R.id.datePicker)
                
                val title = titleInput.text.toString()
                if (title.isBlank()) return@setPositiveButton

                val priority = when (radioGroup.checkedRadioButtonId) {
                    R.id.radioHigh -> Priority.HIGH
                    R.id.radioMedium -> Priority.MEDIUM
                    else -> Priority.LOW
                }

                val calendar = Calendar.getInstance()
                calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
                
                viewModel.addTask(title, priority, calendar.timeInMillis)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
