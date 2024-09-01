package com.example.core.home.data

import com.example.core.ApiService
import com.example.core.home.model.DeleteCalendarTaskRequest
import com.example.core.home.model.GetCalendarTask
import com.example.core.home.model.GetCalendarTaskRequest
import com.example.core.home.model.StoreCalendarTaskRequest
import com.example.core.home.model.SimpleStatusResponse
import com.example.core.home.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(private val apiService: ApiService) : HomeRepository {
    override fun storeCalendarTask(title: String, description: String): Flow<SimpleStatusResponse> {
        return flow {
                val requestBody = prepareRequestBody(title, description)
                emit(apiService.storeCalendarTask(requestBody))
            }
        }


    override fun getCalendarTask(): Flow<List<GetCalendarTask>> {
        return flow {
            val response = apiService.getCalendarTaskList(requestBody = GetCalendarTaskRequest())
            emit(response.tasks)
        }
    }

    override fun deleteCalendarTask(taskId: Int): Flow<SimpleStatusResponse> {
        return flow {
            emit(apiService.deleteCalendarTask(DeleteCalendarTaskRequest(taskId = taskId)))
        }
    }

    private fun prepareRequestBody(title: String, description: String): StoreCalendarTaskRequest {
        return StoreCalendarTaskRequest(task = Task(title = title, description = description))
    }
}