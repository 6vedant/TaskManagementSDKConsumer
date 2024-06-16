package com.example.swiftandroidexample.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swiftandroidexample.adapter.TaskItemClickListener
import com.example.swiftandroidexample.adapter.TaskRecyclerAdapter
import com.example.swiftandroidexample.databinding.FragmentDisplayTaskBinding
import com.example.swiftandroidexample.model.SubTask
import com.example.swiftandroidexample.model.Task
import com.example.swiftandroidexample.viewmodel.TaskViewModel
import com.example.swiftandroidexample.viewmodel.TaskViewModelFactory
import java.lang.Exception

class DisplayTaskFragment : Fragment() {
    private lateinit var binding: FragmentDisplayTaskBinding
    private lateinit var taskRecyclerAdapter: TaskRecyclerAdapter

    private val taskViewModel: TaskViewModel by activityViewModels<TaskViewModel> {
        TaskViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDisplayTaskBinding.inflate(inflater, container, false)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Tasks"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.fabAddTask.setOnClickListener {

            this.findNavController().navigate(
                DisplayTaskFragmentDirections.actionNavigateToCreateTaskFragment()
            )

        }

        // init the adapter and bind it to the recyclerview
        val taskRecyclerAdapter =

                TaskRecyclerAdapter(emptyList(), TaskItemClickListener(itemClickListener = {
                    this.findNavController().navigate(
                        DisplayTaskFragmentDirections.actionNavigateToUpdateTaskFragment(taskArg = taskViewModel.sortedTasks.value?.get(it))
                    )
                }), viewModel = taskViewModel)


        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewTasks.setHasFixedSize(true)
        binding.recyclerViewTasks.adapter = taskRecyclerAdapter

        taskViewModel.tasksList.observe(viewLifecycleOwner, Observer {
            try {
                // Update the adapter's data and notify changes
                Log.d("TAGDATA" , "REcyclerview adapter; data: "+it.toString())

                taskRecyclerAdapter?.updateTasks(it ?: mutableListOf())

            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    override fun onResume() {
        super.onResume()

        taskViewModel.tasksList.observe(viewLifecycleOwner, Observer {
            try {
                // Update the adapter's data and notify changes
                taskRecyclerAdapter?.updateTasks(it ?: mutableListOf())

            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }
}