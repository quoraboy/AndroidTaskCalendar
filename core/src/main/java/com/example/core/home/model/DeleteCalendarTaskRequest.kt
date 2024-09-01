package com.example.core.home.model

import com.google.gson.annotations.SerializedName

data class DeleteCalendarTaskRequest(
    @SerializedName("user_id")
    val userId: Int = 123,
    @SerializedName("task_id")
    val taskId: Int
)
