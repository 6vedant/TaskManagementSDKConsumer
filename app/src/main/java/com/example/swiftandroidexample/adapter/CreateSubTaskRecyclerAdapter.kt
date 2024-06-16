package com.example.swiftandroidexample.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.swiftandroidexample.databinding.CreateSubtaskRecyclerviewItemBinding
import com.example.swiftandroidexample.databinding.SubtaskRecyclerItemBinding
import com.example.swiftandroidexample.model.SubTask
import com.example.swiftandroidexample.model.Task
import com.example.swiftandroidexample.viewmodel.TaskViewModel

class CreateSubTaskRecyclerAdapter(
    val createSubTasks: MutableList<SubTask>,
    private val clickListener: CreateSubTaskRecyclerItemListener,
    private val viewModel: TaskViewModel
) :
    RecyclerView.Adapter<CreateSubTaskRecyclerAdapter.SubTaskItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTaskItemViewHolder {
        val binding =
            CreateSubtaskRecyclerviewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CreateSubTaskRecyclerAdapter.SubTaskItemViewHolder(binding, createSubTasks)
    }

    override fun getItemCount(): Int {
        return createSubTasks.size
    }

    override fun onBindViewHolder(holder: SubTaskItemViewHolder, position: Int) {
        holder.bind(createSubTasks.get(position), clickListener, viewModel)
    }

    fun deleteSubTask(subTask: SubTask) {
        createSubTasks.remove(subTask)
        notifyDataSetChanged()
    }

    class SubTaskItemViewHolder(
        val binding: CreateSubtaskRecyclerviewItemBinding,
        val subTasks: List<SubTask>
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: SubTask?,
            clickListener: CreateSubTaskRecyclerItemListener,
            viewModel: TaskViewModel
        ) {
            item.let {
                binding.subTask = it
                binding.clickListener = clickListener

                binding.imageViewDelete.setOnClickListener {

                }

                binding.checkBoxSubTask.setOnCheckedChangeListener { buttonView, isChecked ->
                    it?.isCompleted = isChecked
                    Log.d("TAG", "createtask: $subTasks")
                }
                binding.executePendingBindings()
            }
        }
    }
}

class CreateSubTaskRecyclerItemListener(
    val subTaskItemClickListener: (createSubTask: SubTask) -> Unit
) {
    fun onSubTaskDelete(createSubTask: SubTask) = subTaskItemClickListener(createSubTask)

}
