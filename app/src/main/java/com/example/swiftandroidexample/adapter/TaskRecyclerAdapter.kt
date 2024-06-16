package com.example.swiftandroidexample.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.swiftandroidexample.R
import com.example.swiftandroidexample.adapter.SubTaskItemClickListener
import com.example.swiftandroidexample.adapter.SubTaskNestedRecyclerAdapter
import com.example.swiftandroidexample.databinding.TaskRecyclerItemBinding
import com.example.swiftandroidexample.model.Task
import com.example.swiftandroidexample.util.StringUtils
import com.example.swiftandroidexample.viewmodel.TaskViewModel

public class TaskRecyclerAdapter constructor(
    var tasks: List<Task>,
    val clickListener: TaskItemClickListener,
    private val viewModel: TaskViewModel,
) : RecyclerView.Adapter<TaskRecyclerAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            TaskRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun updateTasks(newTasksList: List<Task>) {
        val sortedTasks = newTasksList.sortedByDescending { it.date }
        tasks = sortedTasks
        notifyDataSetChanged()
        viewModel.setSortedTasks(sortedTasks)
        Log.d("TAGDATA", "REcyclerview adapter; data: set 37")

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(tasks[position], clickListener, viewModel)
    }

    class ItemViewHolder constructor(private val binding: TaskRecyclerItemBinding) :
        ViewHolder(binding.root) {
        fun bind(item: Task?, clickListener: TaskItemClickListener, viewModel: TaskViewModel) {
            item.let {
                binding.task = it
                binding.clickListener = clickListener
                binding.textViewDate.text = StringUtils.formatDate(epoch = item?.date ?: 1.0)


                val subTaskAdapter = SubTaskNestedRecyclerAdapter(
                    it!!,
                    SubTaskItemClickListener { subTask, parentTask ->
                        Toast.makeText(
                            binding.root.context,
                            "Clicked on sub-task: ${subTask.title}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }, viewModel
                )

                binding.subTasksRecyclerView.layoutManager =
                    LinearLayoutManager(binding.root.context)
                binding.subTasksRecyclerView.setHasFixedSize(true)
                binding.subTasksRecyclerView.adapter = subTaskAdapter

                // Dynamically add tags
                binding.tagsContainer.removeAllViews()
                for (tag in it.tags!!) {
                    val chip = LayoutInflater.from(binding.root.context)
                        .inflate(R.layout.item_chip, binding.tagsContainer, false) as TextView
                    chip.text = tag
                    binding.tagsContainer.addView(chip)
                }

                // Add OnCheckedChangeListener for CheckBox
                binding.checkBoxTask.setOnCheckedChangeListener { buttonView, isChecked ->
                    it.isCompleted = isChecked
                    if (isChecked) {
                        for (subtask in it.subTasks!!) {
                            subtask.let {
                                subtask.isCompleted = isChecked
                            }
                        }
                    }
                    viewModel.updateTaskLiveData(it)
                }
                // Toggle visibility of expandable layout
                // binding.expandableLayout.visibility = if (it!!.isSubTaskExpandable) View.VISIBLE else View.GONE

                binding.root.setOnClickListener {
                    clickListener.onItemClick(adapterPosition)
                }

                // update the priority text
                val priorityValue = binding.task?.priority ?: 0
                if (priorityValue == 0) {
                    binding.textViewPriority.background =
                        itemView.context.getDrawable(R.drawable.priority_background_high_red)
                    binding.textViewPriority.text = "High Priority"
                } else if (priorityValue == 1 || priorityValue == 2) {
                    binding.textViewPriority.background =
                        itemView.context.getDrawable(R.drawable.priority_background_mid_blue)
                    binding.textViewPriority.text = "Mid Priority"

                } else {
                    binding.textViewPriority.background =
                        itemView.context.getDrawable(R.drawable.priority_background_low_lightgreen)
                    binding.textViewPriority.text = "High Priority"
                }

                binding.executePendingBindings()


            }
        }
    }
}

class TaskItemClickListener(
    val itemClickListener: (position: Int) -> Unit
) {
    fun onItemClick(position: Int) = itemClickListener(position)

}
