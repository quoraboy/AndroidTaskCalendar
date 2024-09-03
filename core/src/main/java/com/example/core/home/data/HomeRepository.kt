package com.example.core.home.data

import com.example.core.home.model.SimpleStatusResponse
import com.example.core.home.model.TaskViewState
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun storeCalendarTask(title: String, description: String): Flow<SimpleStatusResponse>
    fun getCalendarTask(): Flow<Result<List<TaskViewState>>>
    fun deleteCalendarTask(taskId: Int): Flow<SimpleStatusResponse>
}