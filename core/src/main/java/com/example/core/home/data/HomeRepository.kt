package com.example.core.home.data

import com.example.core.home.model.DeleteCalendarTaskResponse
import com.example.core.home.model.GetCalendarTask
import com.example.core.home.model.GetCalendarTaskResponse
import com.example.core.home.model.StoreCalendarTaskResponse
import com.example.core.home.model.TaskDetail
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun storeCalendarTask(title: String, description: String): Flow<StoreCalendarTaskResponse>
    fun getCalendarTask(): Flow<List<GetCalendarTask>>
    fun deleteCalendarTask(taskId: Int): Flow<DeleteCalendarTaskResponse>
}