package com.example.core

import com.example.core.home.data.HomeRepositoryImpl
import com.example.core.home.model.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Response

@ExperimentalCoroutinesApi
class HomeRepositoryImplTest {

    @Mock
    private lateinit var apiService: ApiService

    private lateinit var homeRepository: HomeRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        homeRepository = HomeRepositoryImpl(apiService)
    }

    @Test
    fun `storeCalendarTask should return success response`() = runTest {
        // Given
        val title = "Test Title"
        val description = "Test Description"
        val requestBody = StoreCalendarTaskRequest(task = Task(title, description))
        val response = SimpleStatusResponse("Task stored successfully")
        `when`(apiService.storeCalendarTask(requestBody)).thenReturn(Response.success(response))

        // When
        val result = homeRepository.storeCalendarTask(title, description).single()

        // Then
        assertEquals(response, result)
        verify(apiService).storeCalendarTask(requestBody)
    }

    @Test
    fun `getCalendarTask should return list of tasks`() = runTest {
        // Given
        val tasks = listOf(GetCalendarTask(1, TaskDetail("Task 1", "Description 1")))
        val response = Response.success(GetCalendarTaskResponse(tasks))
        `when`(apiService.getCalendarTaskList(GetCalendarTaskRequest())).thenReturn(response)

        // When
        val result = homeRepository.getCalendarTask().single().getOrNull()

        // Then
        assertEquals(tasks, result)
        verify(apiService).getCalendarTaskList(GetCalendarTaskRequest())
    }

    @Test
    fun `deleteCalendarTask should return success response`() = runTest {
        // Given
        val taskId = 1
        val requestBody = DeleteCalendarTaskRequest(taskId = taskId)
        val response = SimpleStatusResponse("Task deleted successfully")
        `when`(apiService.deleteCalendarTask(requestBody)).thenReturn(Response.success(response))

        // When
        val result = homeRepository.deleteCalendarTask(taskId).single()

        // Then
        assertEquals(response, result)
        verify(apiService).deleteCalendarTask(requestBody)
    }
}