package com.example.swiftandroidexample.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import com.example.swiftandroidexample.model.SubTask
import com.example.swiftandroidexample.model.Task
import com.example.swiftandroidexample.util.StringUtils
import java.lang.Exception
import java.lang.IllegalArgumentException

class TaskViewModel : ViewModel() {
    // livedata for holding the task data from database
    private val _tasksList: MutableLiveData<List<Task>?> = MutableLiveData<List<Task>?>()
    val tasksList: MutableLiveData<List<Task>?> = _tasksList
    val sortedTasks: MutableLiveData<List<Task>?> = MutableLiveData<List<Task>?>()


    fun setSortedTasks(newTasks: List<Task>) {
        sortedTasks.value = newTasks.sortedByDescending { it.date }
    }

    private val _createTaskLiveData = MutableLiveData<Task>()
    val createTaskLiveData: LiveData<Task> get() = _createTaskLiveData

    private val _createSubTaskLiveData = MutableLiveData<List<SubTask>>()
    val createSubTaskLiveData: LiveData<List<SubTask>> get() = _createSubTaskLiveData

    private val _updateTaskLiveData = MutableLiveData<Task>()
    val updateTaskLiveData: LiveData<Task> get() = _updateTaskLiveData

    private val _deleteTaskLiveData = MutableLiveData<Task>()
    val deleteTaskLiveData: LiveData<Task> get() = _deleteTaskLiveData

    private val _deleteSubTasksLiveData = MutableLiveData<List<SubTask>>()
    val deleteSubTasksLiveData: LiveData<List<SubTask>> get() = _deleteSubTasksLiveData


    // Function to create a task
    fun createTask(task: Task) {
        _createTaskLiveData.value = task
    }

    fun deleteTaskLiveData(task: Task) {
        _deleteTaskLiveData.value = task
    }

    fun updateTaskLiveData(task: Task) {
        _updateTaskLiveData.value = task
    }

    // Function to create a task
    fun createSubTasks(subTasks: List<SubTask>) {
        _createSubTaskLiveData.value = subTasks
    }

    fun deleteSubTasks(subTasks: List<SubTask>) {
        _deleteSubTasksLiveData.value = subTasks
    }

    fun updateTask(updatedTask: Task) {
        val currentList = _tasksList.value ?: return

        val updatedList = currentList.map { task ->
            if (task.taskID == updatedTask.taskID) {
                updatedTask
            } else {
                task
            }
        }
        _tasksList.value = updatedList
    }

    fun addTask(newTask: Task) {
        val currentList = _tasksList.value ?: listOf()
        val updatedList = currentList + newTask
        _tasksList.value = updatedList
    }

    fun deleteTask(taskID: String): Boolean {
        val currentList = _tasksList.value ?: return false
        val updatedList = currentList.filter { it.taskID != taskID }
        _tasksList.value = updatedList
        return true
    }

    fun updateTasksList(
        taskIDs: Array<String>,
        taskTitles: Array<String>,
        taskDescriptions: Array<String>?,
        tasksDateCreated: Array<Double>?,
        isCompleted: Array<Boolean>?,
        taskTags: Array<String>?,
        tasksPriority: Array<Int>

    ) {
        val tasks = mutableListOf<Task>()
        for (i in taskIDs.indices) {
            try {
                val task = Task(
                    taskID = taskIDs[i],
                    taskTitles[i],
                    taskDescriptions?.get(i) ?: " a",
                    isCompleted = isCompleted?.get(i) ?: false,
                    subTasks = null,
                    tags = StringUtils.getStringArray(taskTags?.get(i)),
                    date = tasksDateCreated?.get(i),
                    priority = tasksPriority[i]
                )
                if(task.taskID.equals("-1")) continue
                tasks.add(task)

            } catch (e: Exception) {
                Log.d("TAGDATAERROR", e.localizedMessage)
                e.printStackTrace()
            }
            _tasksList.value = tasks
        }

    }

    fun updateSubTasksList(
        subTaskIDs: Array<String>,
        parentTaskIDs: Array<String>,
        subTaskTitles: Array<String>,
        isCompletedList: Array<Boolean>?
    ) {
        Log.d("TAGData", "subTaskIDs: ${subTaskIDs.size}")
        Log.d("TAGData", "parentTaskIDs: ${parentTaskIDs.toString()}")

        val subTasks = mutableListOf<SubTask>()
        val tasks = _tasksList.value
        for (i in subTaskIDs.indices) {
            try {
                val subTask = SubTask(
                    subTaskID = subTaskIDs[i],
                    parentTaskID = parentTaskIDs[i],
                    subTaskTitles[i], isCompletedList?.get(i) ?: false
                )
                subTasks.add(subTask)

            } catch (e: Exception) {
                Log.d("TAGData", "error: ${e.toString()}")

                e.printStackTrace()
            }
            Log.d("TAGData", "Subtasks: ${subTasks.toString()}")

            if (tasks != null) {
                for (task in tasks) {
                    val subTaskArrayForTask = mutableListOf<SubTask>()
                    for (subTask in subTasks) {
                        if (task.taskID == subTask.parentTaskID) {
                            Log.d("TAGData", "parentTaskID: ${subTask.parentTaskID}, subTaskID: ${subTask.subTaskID}")
                            subTaskArrayForTask.add(subTask)
                        }
                    }
                    task.subTasks = subTaskArrayForTask
                }
            }
            _tasksList.value = tasks
        }

    }

}

class TaskViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class!")
    }
}