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
        val response = StoreCalendarTaskResponse("Task stored successfully")
        `when`(apiService.storeCalendarTask(requestBody)).thenReturn(response)

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
        val response = GetCalendarTaskResponse(tasks)
        `when`(apiService.getCalendarTask(GetCalendarTaskRequest())).thenReturn(response)

        // When
        val result = homeRepository.getCalendarTask().single()

        // Then
        assertEquals(tasks, result)
        verify(apiService).getCalendarTask(GetCalendarTaskRequest())
    }

    @Test
    fun `deleteCalendarTask should return success response`() = runTest {
        // Given
        val taskId = 1
        val requestBody = DeleteCalenderTaskRequest(taskId = taskId)
        val response = DeleteCalendarTaskResponse("Task deleted successfully")
        `when`(apiService.deleteCalendarTask(requestBody)).thenReturn(response)

        // When
        val result = homeRepository.deleteCalendarTask(taskId).single()

        // Then
        assertEquals(response, result)
        verify(apiService).deleteCalendarTask(requestBody)
    }
}