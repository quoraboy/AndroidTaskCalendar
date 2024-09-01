package com.example.feature.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.home.data.HomeRepository
import com.example.core.home.model.GetCalendarTask
import com.example.feature.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {


    private val _uiStoreTaskState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val uiStoreTaskState: StateFlow<UiState<String>> = _uiStoreTaskState.asStateFlow()

    private val _uiGetTaskState = MutableStateFlow<UiState<List<GetCalendarTask>>>(UiState.Idle)
    val uiGetTaskState: StateFlow<UiState<List<GetCalendarTask>>> = _uiGetTaskState.asStateFlow()

    private val _uiDeleteTaskState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val uiDeleteTaskState: StateFlow<UiState<String>> = _uiDeleteTaskState.asStateFlow()

    init {
        getTasks()
    }

    fun storeTask(title: String, description: String) {
        _uiStoreTaskState.value = UiState.Loading
        viewModelScope.launch {
            homeRepository.storeCalendarTask(title, description)
                .catch { e ->
                    _uiStoreTaskState.value = UiState.Error(e.message.toString())
                }.collect {
                    _uiStoreTaskState.value = UiState.Success(data = "Task stored successfully")
                    getTasks()
                }
        }
    }

    private fun getTasks() {
        _uiGetTaskState.value = UiState.Loading
        viewModelScope.launch {
            homeRepository.getCalendarTask()
                .catch { e ->
                    _uiGetTaskState.value =
                        UiState.Error(e.message.toString())
                }
                .collect { result ->
                    _uiGetTaskState.value =
                        UiState.Success(result)
                }
        }
    }

    fun deleteTask(it: Int) {
        _uiDeleteTaskState.value = UiState.Loading
        viewModelScope.launch {
            homeRepository.deleteCalendarTask(it)
                .catch { e ->
                    _uiDeleteTaskState.value = UiState.Error(e.message.toString())
                }
                .collect {
                    _uiDeleteTaskState.value = UiState.Success(data = "Task deleted successfully")
                    getTasks()
                }
        }
    }

}