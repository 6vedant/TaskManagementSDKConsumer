package com.example.swiftandroidexample.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swiftandroidexample.R
import com.example.swiftandroidexample.adapter.CreateSubTaskRecyclerAdapter
import com.example.swiftandroidexample.adapter.CreateSubTaskRecyclerItemListener
import com.example.swiftandroidexample.databinding.FragmentUpdateTaskBinding
import com.example.swiftandroidexample.model.SubTask
import com.example.swiftandroidexample.model.Task
import com.example.swiftandroidexample.util.StringUtils
import com.example.swiftandroidexample.viewmodel.TaskViewModel
import com.example.swiftandroidexample.viewmodel.TaskViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class UpdateTaskFragment : Fragment() {
    companion object {
        private const val TAG = "UpdateTaskFragment"
    }
    private lateinit var binding: FragmentUpdateTaskBinding
    private val args: UpdateTaskFragmentArgs by navArgs()
    var subTasksList = mutableListOf<SubTask>()
    private lateinit var createSubTaskRecyclerAdapter: CreateSubTaskRecyclerAdapter
    var tagsList = mutableListOf<String>()
    private lateinit var chipGroupTags: ChipGroup

    private val taskViewModel: TaskViewModel by activityViewModels<TaskViewModel> {
        TaskViewModelFactory()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateTaskBinding.inflate(inflater, container, false)
        binding.task = args.taskArg
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Update Task"


        return binding.root
    }

    private fun addTag() {
        val tagTitle = binding.editTextNewTag.text.toString().trim()
        if (TextUtils.isEmpty(tagTitle)) {
            Toast.makeText(context, "Empty tag text", Toast.LENGTH_SHORT).show()
            return
        }
        tagsList.add(tagTitle)
        addChipToGroup(tagTitle)
        binding.editTextNewTag.text?.clear()
    }

    private fun addChipToGroup(tagTitle: String) {
        val chip = Chip(context).apply {
            text = tagTitle
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                chipGroupTags.removeView(this)
                tagsList.remove(tagTitle)
            }
        }
        chipGroupTags.addView(chip)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.task.let {
            it?.subTasks.let {
                subTasksList = (binding.task?.subTasks ?: emptyList<SubTask>()).toMutableList()
            }
        }

        chipGroupTags = binding.chipGroupTags
        // Prepopulate the chips with tags
        binding.task?.tags?.let { tags ->
            tagsList = tags.toMutableList()
            for (tag in tags) {
                addChipToGroup(tag)
            }
        }

        // Auto-select the priority radio button
        binding.task?.let { task ->
            when (task.priority) {
                0 -> binding.radioButtonPriority0.isChecked = true
                1 -> binding.radioButtonPriority1.isChecked = true
                2 -> binding.radioButtonPriority2.isChecked = true
                3 -> binding.radioButtonPriority3.isChecked = true
            }
        }

        // attach the adapter to the subtasks recyclerview
        createSubTaskRecyclerAdapter =
            CreateSubTaskRecyclerAdapter(subTasksList, CreateSubTaskRecyclerItemListener {
                   createSubTaskRecyclerAdapter.deleteSubTask(it)
            }, taskViewModel)
        binding.recyclerViewSubTasks.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewSubTasks.setHasFixedSize(true)
        binding.recyclerViewSubTasks.adapter = createSubTaskRecyclerAdapter

        binding.buttonSaveTask.setOnClickListener {
            updateTask()
        }

        binding.buttonAddSubtask.setOnClickListener {
            showDialogToAddSubTask()
        }

        binding.buttonAddTag.setOnClickListener {
            addTag()
        }

        binding.deleteTaskButton.setOnClickListener {
            deleteTask()
        }

    }

    private fun deleteTask() {
        taskViewModel.deleteTaskLiveData(binding.task!!).apply {
            Toast.makeText(context, "Task updated!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                UpdateTaskFragmentDirections.actionNavigateToDisplayTaskFragmentFromUpdateFragment()
            )
        }
    }

    fun updateTask() {
        val currTitle = binding.editTextTitle.text.toString().trim()
        val currDescription = binding.editTextDescription.text.toString().trim()
        val priority = getSelectedPriority()
        val isTaskCompleted = binding.checkBoxCompleted.isChecked
        if (TextUtils.isEmpty(currTitle)) {
            Toast.makeText(context, "Title is Empty!", Toast.LENGTH_SHORT).show()
            return
        }
        val updatedTask = Task(
            taskID = binding.task!!.taskID,
            title = currTitle,
            description = currDescription,
            isCompleted = isTaskCompleted,
            null,
            tags = tagsList,
            date = System.currentTimeMillis().toDouble(),
            priority = priority
        )
        taskViewModel.updateTaskLiveData(updatedTask).apply {
            taskViewModel.deleteSubTasks(binding.task?.subTasks?: emptyList()).apply {
                Log.d(TAG, "SubTask removed for taskID: ${binding.task!!.taskID}")
            }
            taskViewModel.createSubTasks(subTasksList).apply {
                Log.d(TAG, "Subtasks are updated")
            }

            Toast.makeText(context, "Task updated!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                UpdateTaskFragmentDirections.actionNavigateToDisplayTaskFragmentFromUpdateFragment()
            )
        }

    }

    private fun getSelectedPriority(): Int {
        return when (binding.radioGroupPriority.checkedRadioButtonId) {
            R.id.radioButtonPriority0 -> 0
            R.id.radioButtonPriority1 -> 1
            R.id.radioButtonPriority2 -> 2
            R.id.radioButtonPriority3 -> 3
            else -> 0
        }
    }


    fun showDialogToAddSubTask() {
        // Create an EditText for input
        val input = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_TEXT
            hint = "Enter subtask title"
        }

        // Create the AlertDialog
        AlertDialog.Builder(context).apply {
            setTitle("Add Subtask")
            setView(input)
            setPositiveButton("Add") { dialog, which ->
                val subTaskTitle = input.text.toString()
                if (subTaskTitle.isNotEmpty()) {
                    val newSubTask =
                        SubTask(
                            subTaskID = "subtid${StringUtils.generateUniqueID()}",
                            parentTaskID = binding.task!!.taskID,
                            title = subTaskTitle,
                            isCompleted = false
                        )
                    subTasksList.add(newSubTask)
                    createSubTaskRecyclerAdapter.notifyDataSetChanged()
                }
            }
            setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
            show()
        }
    }
}