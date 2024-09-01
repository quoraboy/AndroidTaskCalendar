package com.example.feature

sealed class UIState<out T : Any?> {

    object Loading : UIState<Nothing>()

    //idle is the default value passed into stateflow
    object Idle : UIState<Nothing>()

    data class Success<out T : Any>(val data: T) : UIState<T>()
    data class Error(
        val errorMessage: String
    ) : UIState<Nothing>()
}