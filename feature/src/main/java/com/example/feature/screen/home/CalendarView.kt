package com.example.feature.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.feature.Dimens
import com.example.feature.UtilConstant.EMPTY_STRING
import com.example.feature.UtilConstant.INT_FIVE
import com.example.feature.UtilConstant.INT_ONE
import com.example.feature.UtilConstant.INT_SIX
import com.example.feature.UtilConstant.INT_ZERO
import com.example.feature.component.DropdownSelector
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.time.YearMonth

const val START_YEAR = 2000
const val END_YEAR = 2030
const val WEIGHT_ONE = 1f
@Composable
fun CalendarView(openDialog: () -> Unit) {
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
        options = (START_YEAR..END_YEAR).toList(),
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
        selectedOption = monthNames[selectedMonth - INT_ONE], // Adjust index for month names
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
                        .weight(WEIGHT_ONE)
                        .padding(Dimens.dp8),
                    text = day, style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Days grid
        var dayCounter = INT_ONE
        for (week in INT_ZERO..INT_FIVE) {
            key(week) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    for (day in INT_ZERO..INT_SIX) {
                        Box(
                            modifier = Modifier
                                .weight(WEIGHT_ONE)
                                .height(Dimens.dp48)
                                .padding(Dimens.dp4),
                            contentAlignment = Alignment.Center
                        ) {
                            if (week == INT_ZERO && day < firstDayOfMonth || dayCounter > daysInMonth) {
                                Text(EMPTY_STRING)
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

