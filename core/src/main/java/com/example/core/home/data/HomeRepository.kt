package com.example.core.home.data

import com.example.core.home.model.GetCalendarTask
import com.example.core.home.model.SimpleStatusResponse
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun storeCalendarTask(title: String, description: String): Flow<SimpleStatusResponse>
    fun getCalendarTask(): Flow<List<GetCalendarTask>>
    fun deleteCalendarTask(taskId: Int): Flow<SimpleStatusResponse>
}