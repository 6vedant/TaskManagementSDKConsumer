package com.example.swiftandroidexample.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.swiftandroidexample.R;
import com.example.swiftandroidexample.model.SubTask;
import com.example.swiftandroidexample.model.Task;
import com.example.swiftandroidexample.util.StringUtils;
import com.example.swiftandroidexample.viewmodel.TaskViewModel;
import com.example.swiftandroidexample.viewmodel.TaskViewModelFactory;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private TaskViewModel taskViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            // initializing swift runtime.
            // The first argument is a pointer to java context (activity in this case).
            // The second argument should always be false.
            org.swift.swiftfoundation.SwiftFoundation.Initialize(this, false);
        } catch (Exception err) {
            android.util.Log.e("SwiftAndroidExample", "Can't initialize swift foundation: " + err.toString());
        }

        // loading dynamic library containing swift code
        System.loadLibrary("SwiftAndroidExample");

        taskViewModel = new ViewModelProvider(this, new TaskViewModelFactory()).get(TaskViewModel.class);

        initTaskManager();
        taskViewModel.getCreateTaskLiveData().observe(this, new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                if (task != null) {
                    Log.d("TAGNEW", "task to be created: " + task.toString());
                    addTask(task.getTaskID(), task.getTitle(), task.getDescription(), task.isCompleted(), StringUtils.Companion.getTagsStringEquivalent(task.getTags()), task.getPriority());
                } else {
                    Log.d("TAGNEW", "task is null");
                }
            }
        });

        taskViewModel.getCreateSubTaskLiveData().observe(this, new Observer<List<SubTask>>() {
            @Override
            public void onChanged(List<SubTask> subTasks) {
                if (subTasks != null) {
                    Log.d("TAGNEW", "task: " + subTasks.toString());
                    for (SubTask subTask : subTasks) {
                        addSubTask(subTask.getSubTaskID(), subTask.getParentTaskID(), subTask.getTitle());
                    }
                } else {
                    Log.d("TAGNEW", "task is null");
                }
            }
        });

        taskViewModel.getDeleteSubTasksLiveData().observe(this, new Observer<List<SubTask>>() {
            @Override
            public void onChanged(List<SubTask> subTasks) {
                if (subTasks != null) {
                    Log.d("TAGNEW", "task: " + subTasks.toString());
                    for (SubTask subTask : subTasks) {
                        deleteSubTask(subTask.getSubTaskID());
                    }
                } else {
                    Log.d("TAGNEW", "task is null");
                }
            }
        });

        taskViewModel.getUpdateTaskLiveData().observe(this, new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                if (task != null) {
                    Log.d("TAGNEW", "task to be updated: " + task.toString());
                    updateTask(task.getTaskID(), task.getTitle(), task.getDescription(), task.isCompleted(), StringUtils.Companion.getTagsStringEquivalent(task.getTags()), task.getPriority());
                } else {
                    Log.d("TAGNEW", "task is null");
                }
            }
        });

        taskViewModel.getDeleteTaskLiveData().observe(this, new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                if (task != null) {
                    deleteTask(task.getTaskID());
                }
            }
        });

    }

    // method is called from swift code, and result is sent as input parameter
    public void getAllUpdatedTasks(String[] taskIDs, String[] taskTitles,
                                   String[] taskDescriptions, boolean[] isCompletedArray,
                                   double[] dateCreated,
                                   String[] tagsArray, int[] priorityArray) {

        Boolean[] tasksCompletedBoolean = new Boolean[taskIDs.length];
        for (int i = 0; i < tasksCompletedBoolean.length; i++) {
            tasksCompletedBoolean[i] = isCompletedArray[i];
        }

        Integer[] tasksPriorityInteger = new Integer[taskIDs.length];
        for (int i = 0; i < tasksPriorityInteger.length; i++) {
            tasksPriorityInteger[i] = priorityArray[i];
        }

        Double[] dateCreatedDouble = new Double[dateCreated.length];
        for (int i = 0; i < dateCreatedDouble.length; i++) {
            dateCreatedDouble[i] = dateCreated[i];
        }

        taskViewModel.updateTasksList(taskIDs, taskTitles, taskDescriptions, dateCreatedDouble, tasksCompletedBoolean, tagsArray, tasksPriorityInteger);
    }

    public void getAllUpdatedSubTasks(String[] subTaskIDs,
                                      String[] subTaskParentIDs,
                                      String[] subTaskTitles,
                                      boolean[] isSubTaskCompleted
    ) {
        Boolean isCompleted[] = new Boolean[isSubTaskCompleted.length];
        for (int i = 0; i < isCompleted.length; i++) {
            isCompleted[i] = isSubTaskCompleted[i];
        }

        taskViewModel.updateSubTasksList(subTaskIDs, subTaskParentIDs, subTaskTitles, isCompleted);
    }


    public native void addTask(String taskID, String taskTitle, String taskDescription, boolean isCompleted, String tags, int priority);

    public native void addSubTask(String subTaskID, String parentTaskID, String subTaskTitle);

    public native void deleteTask(String taskID);

    public native void updateTask(String taskID, String newTaskTitle, String newDescription, boolean isCompleted, String tagsStr, int priority);

    public native void deleteSubTask(String subTaskID);

    // custom method to be called from java
    // implementation of method is in Swift file
    private native void initTaskManager();


}