package com.example.core.home.data

import com.example.core.ApiService
import com.example.core.home.model.DeleteCalendarTaskRequest
import com.example.core.home.model.GetCalendarTask
import com.example.core.home.model.GetCalendarTaskRequest
import com.example.core.home.model.GetCalendarTaskResponse
import com.example.core.home.model.StoreCalendarTaskRequest
import com.example.core.home.model.SimpleStatusResponse
import com.example.core.home.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(private val apiService: ApiService) : HomeRepository {
    override fun storeCalendarTask(title: String, description: String): Flow<SimpleStatusResponse> {
        return flow {
            val requestBody = prepareRequestBody(title, description)
            val response = apiService.storeCalendarTask(requestBody)
            if (response.isSuccessful){
                emit(response.body()?: throw Exception("Empty response body"))
            } else {
                throw Exception(response.message())
            }
        }
    }

    override fun getCalendarTask(): Flow<Result<List<GetCalendarTask>>> {
        return flow {
            val response = apiService.getCalendarTaskList(GetCalendarTaskRequest())
            if (response.isSuccessful) {
                emit(Result.success(response.body()?.tasks ?: emptyList()))
            } else {
                emit(Result.failure(Exception(response.message())))
            }
        }
    }


    override fun deleteCalendarTask(taskId: Int): Flow<SimpleStatusResponse> {
        return flow {
            val response = apiService.deleteCalendarTask(DeleteCalendarTaskRequest(taskId = taskId))
            if (response.isSuccessful) {
                emit(response.body()?: throw Exception("Empty response body"))
            } else {
                throw Exception(response.message())
            }
        }
    }

    private fun prepareRequestBody(title: String, description: String): StoreCalendarTaskRequest {
        return StoreCalendarTaskRequest(task = Task(title = title, description = description))
    }
}