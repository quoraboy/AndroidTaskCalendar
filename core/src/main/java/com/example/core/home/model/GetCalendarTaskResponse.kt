package com.example.core.home.model

import com.google.gson.annotations.SerializedName

data class GetCalendarTaskResponse(val tasks: List<GetCalendarTask>)

data class GetCalendarTask(
    @SerializedName("task_id") val taskId: Int?,
    @SerializedName("task_detail") val taskDetail: TaskDetail

)

data class TaskDetail(
    val title: String,
    val description: String
)