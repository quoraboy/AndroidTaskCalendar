package com.example.core.home.model

import com.google.gson.annotations.SerializedName

data class StoreCalendarTaskRequest(
    @SerializedName("user_id") val userId: Int = 123,
    val task: Task
)

data class Task(
    val title: String,
    val description: String
)
