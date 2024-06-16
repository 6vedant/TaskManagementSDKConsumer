package com.example.swiftandroidexample.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.swiftandroidexample.databinding.SubtaskRecyclerItemBinding
import com.example.swiftandroidexample.model.SubTask
import com.example.swiftandroidexample.model.Task
import com.example.swiftandroidexample.viewmodel.TaskViewModel

class SubTaskNestedRecyclerAdapter(
    val task: Task,
    private val clickListener: SubTaskItemClickListener,
    private val viewModel: TaskViewModel
) :
    RecyclerView.Adapter<SubTaskNestedRecyclerAdapter.SubTaskItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTaskItemViewHolder {
        val binding =
            SubtaskRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubTaskNestedRecyclerAdapter.SubTaskItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return task.subTasks?.size ?: 0
    }

    override fun onBindViewHolder(holder: SubTaskItemViewHolder, position: Int) {
        holder.bind(task.subTasks?.get(position), task, clickListener, viewModel)
    }

    class SubTaskItemViewHolder(val binding: SubtaskRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: SubTask?,
            task: Task?,
            clickListener: SubTaskItemClickListener,
            viewModel: TaskViewModel
        ) {
            item.let {
                binding.subTask = it
                binding.task = task
                binding.clickListener = clickListener

                // Add OnCheckedChangeListener for CheckBox
                binding.checkBoxSubTask.setOnCheckedChangeListener { buttonView, isChecked ->
                    it?.isCompleted = isChecked
                    task?.subTasks?.let { subTasks ->
                        for (index in subTasks.indices) {
                            if (subTasks[index].subTaskID == it?.subTaskID) {
                                task.subTasks!!.get(index).isCompleted = isChecked
                            }
                        }
                    }
                    task.let {
                        viewModel.updateTask(updatedTask = task!!)
                    }
                }
                binding.executePendingBindings()
            }
        }
    }
}

class SubTaskItemClickListener(
    val subTaskItemClickListener: (subTask: SubTask, task: Task) -> Unit
) {
    fun onSubTaskClick(subTask: SubTask, task: Task) = subTaskItemClickListener(subTask, task)

}
