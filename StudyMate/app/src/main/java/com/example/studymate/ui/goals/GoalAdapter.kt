package com.example.studymate.ui.goals

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.studymate.data.model.GoalEntity
import com.example.studymate.data.model.GoalStatus
import com.example.studymate.databinding.ItemGoalBinding

class GoalAdapter(
    private val onGoalChecked: (GoalEntity, Boolean) -> Unit,
    private val onGoalClicked: (GoalEntity) -> Unit
) : ListAdapter<GoalEntity, GoalAdapter.GoalViewHolder>(GoalDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = ItemGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GoalViewHolder(private val binding: ItemGoalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(goal: GoalEntity) {
            binding.goalStatement.text = goal.statement
            binding.checkbox.setOnCheckedChangeListener(null)
            binding.checkbox.isChecked = goal.status == GoalStatus.COMPLETED
            
            if (goal.status == GoalStatus.COMPLETED) {
                binding.goalStatement.alpha = 0.5f
                binding.goalStatement.paintFlags = binding.goalStatement.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.goalStatement.alpha = 1.0f
                binding.goalStatement.paintFlags = binding.goalStatement.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                Log.d("GoalAdapter", "Checkbox changed for ${goal.id} to $isChecked")
                onGoalChecked(goal, isChecked)
            }
            
            binding.root.setOnClickListener {
                onGoalClicked(goal)
            }
        }
    }

    class GoalDiffCallback : DiffUtil.ItemCallback<GoalEntity>() {
        override fun areItemsTheSame(oldItem: GoalEntity, newItem: GoalEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: GoalEntity, newItem: GoalEntity): Boolean = oldItem == newItem
    }
}
