package com.example.feature.screen.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.home.model.GetCalendarTask
import com.example.feature.Dimens
import com.example.feature.UIState
import com.example.feature.component.InputDialog
import com.example.feature.component.ShowToast

@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = hiltViewModel()
    val storeTask by viewModel.uiStoreTaskState.collectAsStateWithLifecycle()
    val getTask by viewModel.uiGetTaskState.collectAsStateWithLifecycle()
    val deleteTask by viewModel.uiDeleteTaskState.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var selectedTaskId by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        //To increase the readability of the code, I have extracted the content of the screen into separate functions
        storeTaskContent(this, storeTask)
        item {
            CalendarView {
                showDialog = true
            }
        }

        //To increase the readability of the code, I have extracted the content of the screen into separate functions
        getTaskContent(
            this,
            getTask,
            selectedTaskId,
            { selectedTaskId = it },
            { selectedTaskId?.let { viewModel.deleteTask(it) } })

        //To increase the readability of the code, I have extracted the content of the screen into separate functions
        deleteTaskContent(this, deleteTask)
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
}

fun storeTaskContent(scope: LazyListScope, storeTask: UIState<*>) {
    when (storeTask) {
        is UIState.Loading -> {
            scope.item {
                LoadingIndicator()
            }
        }
        is UIState.Success -> {
            scope.item { ShowToast((storeTask as UIState.Success<String>).data) }
        }
        is UIState.Error -> {
            scope.item { ShowToast(storeTask.errorMessage) }
        }
        UIState.Idle -> { /* No UI for Idle state */ }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun getTaskContent(
    scope: LazyListScope,
    getTask: UIState<*>,
    selectedTaskId: Int?,
    onTaskSelected: (Int?) -> Unit,
    deleteTask: () -> Unit
) {
    when (getTask) {
        is UIState.Loading -> {
            scope.item { LoadingIndicator() }
        }
        is UIState.Success -> {
            val tasks = (getTask as UIState.Success<List<GetCalendarTask>>).data
            scope.stickyHeader {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    Text(
                        modifier = Modifier.padding(Dimens.dp8),
                        text = "Tasks",
                        style = MaterialTheme.typography.titleLarge
                    )
                    HorizontalDivider()
                }
            }
            scope.items(tasks.size) { index ->
                TaskItem(tasks[index], selectedTaskId, onTaskSelected, deleteTask)
            }
        }
        is UIState.Error -> {
            scope.item { ShowToast((getTask as UIState.Error).errorMessage) }
        }
        UIState.Idle -> { /* No UI for Idle state */ }
    }
}

fun deleteTaskContent(scope: LazyListScope, deleteTask: UIState<*>) {
    when (deleteTask) {
        is UIState.Loading -> { /* No UI for Loading state */ }
        is UIState.Success -> {
            scope.item { ShowToast((deleteTask as UIState.Success<String>).data) }
        }
        is UIState.Error -> {
            scope.item { ShowToast((deleteTask as UIState.Error).errorMessage) }
        }
        UIState.Idle -> { /* No UI for Idle state */ }
    }
}

@Composable
fun TaskItem(task: GetCalendarTask, selectedTaskId: Int?, onTaskSelected: (Int?) -> Unit, deleteTask: () -> Unit) {
    val isSelected = task.taskId == selectedTaskId
    ElevatedCard(
        modifier = Modifier
            .padding(Dimens.dp8)
            .border(
                width = Dimens.dp1,
                color = if (isSelected) Color.Blue else Color.Red,
                shape = RoundedCornerShape(size = Dimens.dp8)
            )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = isSelected,
                onClick = { onTaskSelected(task.taskId) }
            )
            Column(modifier = Modifier.padding(start = Dimens.dp8, end = Dimens.dp8)) {
                Text(
                    text = "Title: ${task.taskDetail.title}"
                )
                Text(
                    text = "Description: ${task.taskDetail.description}"
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
            .height(8.dp),
        color = Color.Red
    )
}

