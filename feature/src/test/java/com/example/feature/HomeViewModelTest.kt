package com.example.feature

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.feature.screen.home.HomeViewModel

import com.example.core.home.data.HomeRepository
import com.example.core.home.model.GetCalendarTask
import com.example.core.home.model.SimpleStatusResponse
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

    //Purpose: It sets the main dispatcher to a TestCoroutineDispatcher
    // for unit testing coroutines.
    //Usage: Even though you don't see it explicitly used
    // in the test methods, it ensures that all coroutines
    // launched in the viewModelScope use the TestCoroutineDispatcher.
    // This allows you to control the execution of coroutines and advance time in tests.
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

    //Purpose: It ensures that LiveData updates happen instantly and synchronously during tests.
    //Usage: This rule is necessary for testing LiveData because
    // it forces all background operations to run on the same thread,
    // making it easier to test LiveData changes.
    //you don't need this rule because StatFlow or live data are thread safe it
    // means you can update them from any thread
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
            SimpleStatusResponse("Task stored successfully")
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
        `when`(homeRepository.getCalendarTask()).thenReturn(flow { emit(Result.success(tasks)) })

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
            SimpleStatusResponse("Task deleted successfully")
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