package com.example.core.home.data

import com.example.core.home.model.GetCalendarTask
import com.example.core.home.model.GetCalendarTaskResponse
import com.example.core.home.model.SimpleStatusResponse
import com.example.core.home.model.Task
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface HomeRepository {
    fun storeCalendarTask(title: String, description: String): Flow<SimpleStatusResponse>
    fun getCalendarTask(): Flow<Result<List<GetCalendarTask>>>
    fun deleteCalendarTask(taskId: Int): Flow<SimpleStatusResponse>
}