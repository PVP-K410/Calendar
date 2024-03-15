package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.model.Task
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun MonthlyCalendarScreen(
    model: MonthlyCalendarViewModel = hiltViewModel(),
) {
    val state by model.state.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var dateTasks by remember { mutableStateOf<List<Task>>(emptyList()) }

    MonthlyCalendar(
        days = DateUtil.daysOfWeek,
        month = state.yearMonth,
        dates = state.dates,
        onClickListener = { date ->
            showDialog = true
            selectedDate = date.date
            dateTasks = date.tasks
        }
    )

    if (showDialog) {
        DayDialog(
            date = selectedDate,
            tasks = dateTasks,
            onDismissRequest = { showDialog = false }
        )
    }
}

@Composable
fun DayDialog(
    date: LocalDate,
    tasks: List<Task> = listOf(),
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Day(
            "${date.month.toString().toLowerCase().capitalize()} ${date.dayOfMonth}",
            tasks = tasks,
            date = date,
            expandedUponCreation = true
        )
    }
}

@Composable
fun MonthlyCalendar(
    days: Array<String>,
    month: YearMonth,
    dates: List<CalendarUiState.DateEntry>,
    onClickListener: (CalendarUiState.DateEntry) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row {
            repeat(days.size) {
                DayItem(
                    day = days[it],
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Header(
            month = month,
        )

        Content(
            dates = dates,
            onClickListener = onClickListener
        )
    }
}

@Composable
fun Header(
    month: YearMonth,
    model: MonthlyCalendarViewModel = hiltViewModel()
) {
    Row {
        IconButton(onClick = {
            model.changeMonth(month.minusMonths(1))
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back"
            )
        }

        Text(
            text = month.getDisplayName(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )

        IconButton(onClick = {
            model.changeMonth(month.plusMonths(1))
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Forward"
            )
        }
    }
}

@Composable
fun DayItem(
    day: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Text(
            text = day,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(10.dp)
        )
    }
}

@Composable
fun Content(
    dates: List<CalendarUiState.DateEntry>,
    onClickListener: (CalendarUiState.DateEntry) -> Unit,
) {
    Column {
        var index = 0

        repeat(6) {
            if (index >= dates.size) {
                return@repeat
            }

            Row {
                repeat(7) {
                    val item = if (index < dates.size) {
                        dates[index]
                    } else {
                        CalendarUiState.DateEntry.Empty
                    }

                    ContentItem(
                        entry = item,
                        onClickListener = onClickListener,
                        modifier = Modifier.weight(1f)
                    )

                    index++
                }
            }
        }
    }
}

@Composable
fun ContentItem(
    entry: CalendarUiState.DateEntry,
    onClickListener: (CalendarUiState.DateEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    var text = entry.date.dayOfMonth.toString()
    var clickable = true
    var color = MaterialTheme.colorScheme.onPrimaryContainer

    // If date is a default value used to fill up the list so that first month day starts on correct
    // week date, we ensure that it is not clickable and is an empty string
    if (entry.date.isEqual(LocalDate.MIN)) {
        text = ""
        clickable = false
    } else if (entry.tasks.isEmpty()) {
        color = Color.Gray
        clickable = false
    }

    Box(
        modifier = modifier
            .background(
                color = if (entry.isHighlighted) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    Color.Transparent
                },
                MaterialTheme.shapes.medium
            )
            .clickable(enabled = clickable) {
                onClickListener(entry)
            }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(10.dp)
        )
    }
}

