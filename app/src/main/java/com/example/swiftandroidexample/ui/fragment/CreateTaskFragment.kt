package com.example.swiftandroidexample.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swiftandroidexample.adapter.CreateSubTaskRecyclerAdapter
import com.example.swiftandroidexample.adapter.CreateSubTaskRecyclerItemListener
import com.example.swiftandroidexample.databinding.FragmentCreateTaskBinding
import com.example.swiftandroidexample.viewmodel.TaskViewModel
import com.example.swiftandroidexample.viewmodel.TaskViewModelFactory
import android.app.AlertDialog
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.swiftandroidexample.R
import com.example.swiftandroidexample.model.SubTask
import com.example.swiftandroidexample.model.Task
import com.example.swiftandroidexample.util.StringUtils
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlin.random.Random

class CreateTaskFragment : Fragment() {
    companion object {
        private const val TAG = "CreateTaskFragment"
    }

    private lateinit var binding: FragmentCreateTaskBinding
    private lateinit var createSubTaskRecyclerAdapter: CreateSubTaskRecyclerAdapter
    private lateinit var currTaskID: String
    var subTasksList = mutableListOf<SubTask>()
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
        binding = FragmentCreateTaskBinding.inflate(inflater, container, false)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Create Task"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chipGroupTags = binding.chipGroupTags

        currTaskID = "tid${StringUtils.generateUniqueID()}"

        // attach the adapter to the subtasks recyclerview
        createSubTaskRecyclerAdapter =
            CreateSubTaskRecyclerAdapter(subTasksList, CreateSubTaskRecyclerItemListener {
                createSubTaskRecyclerAdapter.deleteSubTask(it)
            }, taskViewModel)
        binding.recyclerViewSubTasks.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewSubTasks.setHasFixedSize(true)
        binding.recyclerViewSubTasks.adapter = createSubTaskRecyclerAdapter

        binding.buttonSaveTask.setOnClickListener {
            saveTask()
        }

        binding.buttonAddSubtask.setOnClickListener {
            showDialogToAddSubTask()
        }

        binding.buttonAddTag.setOnClickListener {
            addTag()
        }

    }

    fun saveTask() {
        val currTitle = binding.editTextTitle.text.toString().trim()
        val currDescription = binding.editTextDescription.text.toString().trim()
        val isTaskCompleted = binding.checkBoxCompleted.isChecked
        if (TextUtils.isEmpty(currTitle)) {
            Toast.makeText(context, "Title is Empty!", Toast.LENGTH_SHORT).show()
            return
        }
        val createTask = Task(
            taskID = currTaskID,
            title = currTitle,
            description = currDescription,
            isCompleted = isTaskCompleted,
            null,
            tags = tagsList,
            date = System.currentTimeMillis().toDouble(),
            priority = getSelectedPriority()
        )
        taskViewModel.createTask(createTask).apply {
            taskViewModel.createSubTasks(subTasksList).apply {
                Log.d(TAG, "SubTask created for taskID: ${currTaskID}")
            }
            Toast.makeText(context, "Task created!", Toast.LENGTH_SHORT)
                .show()
            findNavController().navigate(
                CreateTaskFragmentDirections.actionNavigateToDisplayTaskFragmentFromCreateFragment()
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

    fun addTag() {
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
                            parentTaskID = currTaskID,
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