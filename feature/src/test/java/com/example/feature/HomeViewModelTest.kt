package com.example.feature

import com.example.feature.screen.home.HomeViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.core.home.data.HomeRepository
import com.example.core.home.model.DeleteCalendarTaskResponse
import com.example.core.home.model.GetCalendarTask
import com.example.core.home.model.StoreCalendarTaskResponse
import com.example.core.home.model.TaskDetail
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private val testDispatcher = TestCoroutineDispatcher()

    @get:Rule
    val coroutineRule = object : TestWatcher() {
        override fun starting(description: Description?) {
            Dispatchers.setMain(testDispatcher)
        }

        override fun finished(description: Description?) {
            Dispatchers.resetMain()
            testDispatcher.cleanupTestCoroutines()
        }
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var homeRepository: HomeRepository

    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        homeViewModel = HomeViewModel(homeRepository)
    }

    @Test
    fun `storeTask should update uiStoreTaskState to Success when repository call is successful`() = runTest {
        // Given
        val title = "Test Title"
        val description = "Test Description"
        `when`(homeRepository.storeCalendarTask(title, description)).thenReturn(flow { emit(
            StoreCalendarTaskResponse("Task stored successfully")
        ) })

        // When
        homeViewModel.storeTask(title, description)

        // Then
        assertEquals(UIState.Success("Task stored successfully"), homeViewModel.uiStoreTaskState.value)
    }

    @Test
    fun `storeTask should update uiStoreTaskState to Error when repository call fails`() = runTest {
        // Given
        val title = "Test Title"
        val description = "Test Description"
        val errorMessage = "Error storing task"
        `when`(homeRepository.storeCalendarTask(title, description)).thenReturn(flow { throw Exception(errorMessage) })

        // When
        homeViewModel.storeTask(title, description)

        // Then
        assertEquals(UIState.Error(errorMessage), homeViewModel.uiStoreTaskState.value)
    }

    @Test
    fun `getTasks should update uiGetTaskState to Success when repository call is successful`() = runTest {
        // Given
        val tasks = listOf(GetCalendarTask(1, TaskDetail("Task 1", "Description 1")))
        `when`(homeRepository.getCalendarTask()).thenReturn(flow { emit(tasks) })

        // When
        homeViewModel.getTasks()

        // Then
        assertEquals(UIState.Success(tasks), homeViewModel.uiGetTaskState.value)
    }

    @Test
    fun `getTasks should update uiGetTaskState to Error when repository call fails`() = runTest {
        // Given
        val errorMessage = "Error fetching tasks"
        `when`(homeRepository.getCalendarTask()).thenReturn(flow { throw Exception(errorMessage) })

        // When
        homeViewModel.getTasks()

        // Then
        assertEquals(UIState.Error(errorMessage), homeViewModel.uiGetTaskState.value)
    }

    @Test
    fun `deleteTask should update uiDeleteTaskState to Success when repository call is successful`() = runTest {
        // Given
        val taskId = 1
        `when`(homeRepository.deleteCalendarTask(taskId)).thenReturn(flow { emit(
            DeleteCalendarTaskResponse("Task deleted successfully")
        ) })

        // When
        homeViewModel.deleteTask(taskId)

        // Then
        assertEquals(UIState.Success("Task deleted successfully"), homeViewModel.uiDeleteTaskState.value)
    }

    @Test
    fun `deleteTask should update uiDeleteTaskState to Error when repository call fails`() = runTest {
        // Given
        val taskId = 1
        val errorMessage = "Error deleting task"
        `when`(homeRepository.deleteCalendarTask(taskId)).thenReturn(flow { throw Exception(errorMessage) })

        // When
        homeViewModel.deleteTask(taskId)

        // Then
        assertEquals(UIState.Error(errorMessage), homeViewModel.uiDeleteTaskState.value)
    }
}