package com.example.core

import com.example.core.home.model.DeleteCalendarTaskResponse
import com.example.core.home.model.DeleteCalenderTaskRequest
import com.example.core.home.model.GetCalendarTaskRequest
import com.example.core.home.model.GetCalendarTaskResponse
import com.example.core.home.model.StoreCalendarTaskRequest
import com.example.core.home.model.StoreCalendarTaskResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/storeCalendarTask")
    suspend fun storeCalendarTask(@Body requestBody: StoreCalendarTaskRequest): StoreCalendarTaskResponse

    @POST("api/getCalendarTaskList")
    suspend fun getCalendarTask(@Body requestBody: GetCalendarTaskRequest): GetCalendarTaskResponse

    @POST("api/deleteCalendarTask")
    suspend fun deleteCalendarTask(@Body requestBody: DeleteCalenderTaskRequest): DeleteCalendarTaskResponse
}