package com.example.feature.screen.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.home.model.GetCalendarTask
import com.example.feature.Dimens
import com.example.feature.component.InputDialog
import com.example.feature.component.ShowToast

@OptIn(ExperimentalFoundationApi::class)
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
        when (storeTask) {
            is HomeViewModel.UiState.Loading -> {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color.Red
                    )
                }
            }

            is HomeViewModel.UiState.Success -> {
                item { ShowToast((storeTask as HomeViewModel.UiState.Success<String>).data) }
            }

            is HomeViewModel.UiState.Error ->
                item { ShowToast((storeTask as HomeViewModel.UiState.Error).errorMessage) }

            HomeViewModel.UiState.Idle -> {

            }
        }

        item {
            CalendarView() {
                showDialog = true
            }
        }

        when (getTask) {
            is HomeViewModel.UiState.Loading -> {
                item { LoadingIndicator() }
            }

            is HomeViewModel.UiState.Success -> {
                val tasks = (getTask as HomeViewModel.UiState.Success<List<GetCalendarTask>>).data
                stickyHeader {
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
                items(tasks.size) { index ->
                    TaskItem(tasks[index], selectedTaskId, { selectedTaskId = it }, {
                        selectedTaskId?.let { viewModel.deleteTask(it) }
                    })
                }
            }

            is HomeViewModel.UiState.Error -> {
                item { ShowToast((getTask as HomeViewModel.UiState.Error).errorMessage) }
            }

            HomeViewModel.UiState.Idle -> {

            }
        }

        when (deleteTask) {
            is HomeViewModel.UiState.Loading -> {

            }

            is HomeViewModel.UiState.Success -> {
                item { ShowToast((deleteTask as HomeViewModel.UiState.Success<String>).data) }
            }

            is HomeViewModel.UiState.Error -> {
                item { ShowToast((deleteTask as HomeViewModel.UiState.Error).errorMessage) }
            }

            HomeViewModel.UiState.Idle -> {

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
                    Text(text = "Delete Task", color = Color.Magenta, modifier = Modifier.clickable {
                        deleteTask()
                    })

            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    // Show loading indicator
}

@Preview(showBackground = true)
@Composable
fun CalendarAppPreview() {
    CalendarView() {}
}
