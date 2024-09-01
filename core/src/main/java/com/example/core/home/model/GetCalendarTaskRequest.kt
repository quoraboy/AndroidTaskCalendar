package com.example.core.home.model

import com.google.gson.annotations.SerializedName

data class GetCalendarTaskRequest(
    @SerializedName("user_id")
    val userId: Int = 123
)