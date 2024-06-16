package com.example.swiftandroidexample.model

import java.io.Serializable

data class Task(
    val taskID: String,
    val title: String,
    val description: String?,
    var isCompleted: Boolean = false,
    var subTasks: List<SubTask>?,
    val tags: List<String>?,
    val date: Double?,
    var priority: Int = 1
) : Serializable
