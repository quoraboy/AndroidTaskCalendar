package com.example.core

import com.example.core.home.model.DeleteCalendarTaskRequest
import com.example.core.home.model.GetCalendarTaskRequest
import com.example.core.home.model.GetCalendarTaskResponse
import com.example.core.home.model.StoreCalendarTaskRequest
import com.example.core.home.model.SimpleStatusResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/storeCalendarTask")
    suspend fun storeCalendarTask(@Body requestBody: StoreCalendarTaskRequest): SimpleStatusResponse

    @POST("api/getCalendarTaskList")
    suspend fun getCalendarTaskList(@Body requestBody: GetCalendarTaskRequest): GetCalendarTaskResponse

    @POST("api/deleteCalendarTask")
    suspend fun deleteCalendarTask(@Body requestBody: DeleteCalendarTaskRequest): SimpleStatusResponse
}