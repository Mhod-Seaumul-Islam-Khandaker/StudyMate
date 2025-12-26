package com.example.studymate.ui.tasks

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.studymate.data.model.Priority
import com.example.studymate.data.model.TaskEntity
import com.example.studymate.data.model.TaskStatus
import com.example.studymate.databinding.ItemTaskBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskAdapter(
    private val onTaskChecked: (TaskEntity, Boolean) -> Unit,
    private val onTaskClicked: (TaskEntity) -> Unit
) : ListAdapter<TaskEntity, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: TaskEntity) {
            binding.taskTitle.text = task.title
            binding.taskDate.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(task.dueDate))
            binding.checkbox.setOnCheckedChangeListener(null) // Clear listener to avoid triggering during bind
            binding.checkbox.isChecked = task.status == TaskStatus.COMPLETED
            
            // Priority border color
            val color = when (task.priority) {
                Priority.HIGH -> Color.parseColor("#FF5252") // Red
                Priority.MEDIUM -> Color.parseColor("#FFD740") // Amber
                Priority.LOW -> Color.parseColor("#69F0AE") // Green
            }
            binding.priorityIndicator.setBackgroundColor(color)

            // Strikethrough and opacity for completed
            if (task.status == TaskStatus.COMPLETED) {
                binding.taskTitle.alpha = 0.5f
                binding.taskTitle.paintFlags = binding.taskTitle.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.taskTitle.alpha = 1.0f
                binding.taskTitle.paintFlags = binding.taskTitle.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                Log.d("TaskAdapter", "Checkbox changed for ${task.id} to $isChecked")
                onTaskChecked(task, isChecked)
            }
            
            binding.root.setOnClickListener {
                onTaskClicked(task)
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean = oldItem == newItem
    }
}
