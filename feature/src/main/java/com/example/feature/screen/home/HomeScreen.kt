package com.example.feature.screen.home

import android.util.Log
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
import com.example.feature.component.DropdownSelector
import com.example.feature.component.InputDialog
import com.example.feature.component.ShowToast
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.time.YearMonth

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
            CalendarApp() {
                showDialog = true
            }
        }

        when (getTask) {
            is HomeViewModel.UiState.Loading -> {
                item { LoadingIndicator() }
            }

            is HomeViewModel.UiState.Success -> {
                val date = (getTask as HomeViewModel.UiState.Success<List<GetCalendarTask>>).data
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
                items(date.size) { index ->
                    TaskItem(date[index], selectedTaskId, { selectedTaskId = it }, {
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
                Log.d("sachin", "$title $description")
            }
        )
    }
}


@Composable
fun CalendarApp(openDialog: () -> Unit) {
    val currentDate = remember { LocalDate.now() }
    var selectedYear by remember { mutableStateOf(currentDate.year) }
    var selectedMonth by remember { mutableStateOf(currentDate.monthValue) }


    Column(modifier = Modifier.padding(top = Dimens.dp16)) {
        // Year and Month Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Year: ", style = MaterialTheme.typography.titleLarge)
            YearSelector(
                selectedYear = selectedYear,
                onYearSelected = { selectedYear = it }
            )

            Spacer(modifier = Modifier.width(Dimens.dp16))

            Text("Month: ", style = MaterialTheme.typography.titleLarge)
            MonthSelector(selectedMonth = selectedMonth,
                onMonthSelected = { selectedMonth = it }
            )
        }
        Spacer(modifier = Modifier.height(Dimens.dp32))

        MonthView(year = selectedYear, month = selectedMonth, openDialog = openDialog)
    }
}

@Composable
fun YearSelector(selectedYear: Int, onYearSelected: (Int) -> Unit) {
    DropdownSelector(
        options = (2000..2030).toList(),
        selectedOption = selectedYear,
        onOptionSelected = onYearSelected
    )
}

@Composable
fun MonthSelector(selectedMonth: Int, onMonthSelected: (Int) -> Unit) {
    val monthNames = remember {
        DateFormatSymbols().months.toList() // Get month names for current locale
    }

    DropdownSelector(
        options = monthNames,
        selectedOption = monthNames[selectedMonth - 1], // Adjust index for month names
        onOptionSelected = { selectedMonthName ->
            val newMonthIndex = monthNames.indexOf(selectedMonthName) + 1
            onMonthSelected(newMonthIndex)
        }
    )
}


@Composable
fun MonthView(year: Int, month: Int, openDialog: () -> Unit) {
    val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
    val firstDayOfMonth = LocalDate.of(year, month, 1).dayOfWeek.value % 7
    val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Day of the week headers
        Row(modifier = Modifier.fillMaxWidth()) {
            dayNames.forEach { day ->
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    text = day, style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Days grid
        var dayCounter = 1
        for (week in 0..5) {
            key(week) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    for (day in 0..6) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (week == 0 && day < firstDayOfMonth || dayCounter > daysInMonth) {
                                Text("")
                            } else {
                                Text(modifier = Modifier.clickable {
                                    openDialog()
                                }, text = dayCounter.toString())
                                dayCounter++
                            }
                        }
                    }
                }
            }
        }
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
    CalendarApp() {}
}
