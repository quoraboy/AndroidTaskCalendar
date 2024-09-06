package com.example.feature.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.home.model.HeaderViewState
import com.example.core.home.model.TaskViewState
import com.example.feature.Dimens
import com.example.feature.UIState
import com.example.feature.component.InputDialog
import com.example.feature.component.ListItemCustom
import com.example.feature.component.ShowToast


@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = hiltViewModel()
    val storeTask by viewModel.uiStoreTaskState.collectAsStateWithLifecycle()
    val getTask by viewModel.uiGetTaskState.collectAsStateWithLifecycle()
    val deleteTask by viewModel.uiDeleteTaskState.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var selectedTaskId by remember { mutableStateOf<Int?>(null) }

    val items = mutableListOf<ListItemCustom>().apply {
        add(ListItemCustom.CalendarViewItemCustom)
        add(ListItemCustom.HeaderItemCustom(HeaderViewState("Tasks")))
        when (getTask) {
            is UIState.Loading -> add(ListItemCustom.FooterItemCustom)
            is UIState.Success -> {
                val tasks = (getTask as UIState.Success<*>).data as List<TaskViewState>
                tasks.forEach {
                    add(ListItemCustom.TaskItemCustom(it))
                }
            }

            is UIState.Error -> ShowToast(message = (storeTask as UIState.Error).errorMessage)
            is UIState.Idle -> { /* No UI for Idle state */
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        items(items, contentType = { item ->
            when (item) {
                is ListItemCustom.HeaderItemCustom -> "Header"
                is ListItemCustom.TaskItemCustom -> "Task"
                is ListItemCustom.FooterItemCustom -> "Footer"
                is ListItemCustom.CalendarViewItemCustom -> "CalendarView"
            }
        }) { item ->
            when (item) {
                is ListItemCustom.HeaderItemCustom -> HeaderView(item.headerViewState)
                is ListItemCustom.TaskItemCustom -> TaskView(
                    item.taskViewState,
                    selectedTaskId,
                    { selectedTaskId = it },
                    { selectedTaskId?.let { viewModel.deleteTask(it) } })

                is ListItemCustom.FooterItemCustom -> LoadingIndicator()
                is ListItemCustom.CalendarViewItemCustom -> CalendarView { showDialog = true }
            }
        }
    }

    if (showDialog) {
        InputDialog(
            onDismissRequest = {
                showDialog = false
            },
            onSubmit = { title, description ->
                viewModel.storeTask(title, description)
                showDialog = false
            }
        )
    }

    // Show toast messages based on storeTask state
    when (storeTask) {
        is UIState.Success -> ShowToast(message = "Task stored successfully")
        is UIState.Error -> ShowToast(message = (storeTask as UIState.Error).errorMessage)
        else -> { /* No toast for other states */
        }
    }

    // Show toast messages based on deleteTask state
    when (deleteTask) {
        is UIState.Success -> ShowToast(message = "Task deleted successfully")
        is UIState.Error -> ShowToast(message = (deleteTask as UIState.Error).errorMessage)
        else -> { /* No toast for other states */
        }
    }
}

@Composable
fun HeaderView(headerViewState: HeaderViewState) {
    Text(text = headerViewState.title)
}

@Composable
fun TaskView(
    taskViewState: TaskViewState,
    selectedTaskId: Int?,
    onTaskSelected: (Int?) -> Unit,
    deleteTask: () -> Unit
) {
    val isSelected = taskViewState.id == selectedTaskId
    ElevatedCard(
        modifier = Modifier
            .padding(Dimens.dp8)
            .border(
                width = Dimens.dp1,
                color = if (isSelected) Color.Red else Color.Blue,
                shape = RoundedCornerShape(size = Dimens.dp8)
            )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = isSelected,
                onClick = { onTaskSelected(taskViewState.id) }
            )
            Column(modifier = Modifier.padding(start = Dimens.dp8, end = Dimens.dp8)) {
                Text(
                    text = "Title: ${taskViewState.title}"
                )
                Text(
                    text = "Description: ${taskViewState.description}"
                )
                if (isSelected)
                    Text(text = "Delete", color = Color.Magenta, modifier = Modifier.clickable {
                        deleteTask()
                    })
            }
        }
    }
}


@Composable
fun LoadingIndicator() {
    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.dp8),
        color = Color.Red
    )
}