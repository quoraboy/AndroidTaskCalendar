package com.example.feature.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.home.data.HomeRepository
import com.example.core.home.model.GetCalendarTask
import com.example.feature.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {


    private val _uiStoreTaskState = MutableStateFlow<UIState<String>>(UIState.Idle)
    val uiStoreTaskState: StateFlow<UIState<String>> = _uiStoreTaskState.asStateFlow()

    private val _uiGetTaskState = MutableStateFlow<UIState<List<GetCalendarTask>>>(UIState.Idle)
    val uiGetTaskState: StateFlow<UIState<List<GetCalendarTask>>> = _uiGetTaskState.asStateFlow()

    private val _uiDeleteTaskState = MutableStateFlow<UIState<String>>(UIState.Idle)
    val uiDeleteTaskState: StateFlow<UIState<String>> = _uiDeleteTaskState.asStateFlow()

    init {
        getTasks()
    }

    fun storeTask(title: String, description: String) {
        _uiStoreTaskState.value = UIState.Loading
        viewModelScope.launch {
            homeRepository.storeCalendarTask(title, description)
                .catch { e ->
                    _uiStoreTaskState.value = UIState.Error(e.message.toString())
                }.collect {
                    _uiStoreTaskState.value = UIState.Success(data = "Task stored successfully")
                    getTasks()
                }
        }
    }

    private fun getTasks() {
        _uiGetTaskState.value = UIState.Loading
        viewModelScope.launch {
            homeRepository.getCalendarTask()
                .catch { e ->
                    _uiGetTaskState.value =
                        UIState.Error(e.message.toString())
                }
                .collect { result ->
                    _uiGetTaskState.value =
                        UIState.Success(result)
                }
        }
    }

    fun deleteTask(it: Int) {
        _uiDeleteTaskState.value = UIState.Loading
        viewModelScope.launch {
            homeRepository.deleteCalendarTask(it)
                .catch { e ->
                    _uiDeleteTaskState.value = UIState.Error(e.message.toString())
                }
                .collect {
                    _uiDeleteTaskState.value = UIState.Success(data = "Task deleted successfully")
                    getTasks()
                }
        }
    }

}