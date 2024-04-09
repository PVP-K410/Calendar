package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.SwapHoriz
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
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.common.DateUtil
import com.pvp.app.common.DateUtil.getDisplayName
import com.pvp.app.model.Task
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth

@Composable
private fun AnalysisOfDayBox(
    dateTasks: List<Task>,
    selectedDate: LocalDate
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.dp)
    ) {
        AnalysisOfDay(
            date = selectedDate,
            tasks = dateTasks
        )
    }
}

/**
 * Shown below the monthly calendar
 */
@Composable
private fun Content(
    tasks: List<Task> = emptyList(),
    date: LocalDate,
    showAnalyses: Boolean,
    showDialog: Boolean,
    onChangeExpand: (Boolean) -> Unit,
    onChangeShowAnalyses: (Boolean) -> Unit,
    onChangeShowDialog: (Boolean) -> Unit
) {
    when {
        tasks.isEmpty() && date == LocalDate.now() -> {
            AnalysisOfDayBox(
                tasks,
                date
            )

            if (showDialog) {
                TaskCreateDialog(
                    date,
                    onChangeShowDialog
                )
            }
        }

        tasks.isEmpty() && !date.isBefore(LocalDate.now()) -> {
            TaskCreateDialog(
                date,
                onChangeExpand
            )
        }

        tasks.isEmpty() && !date.isAfter(LocalDate.now()) -> {
            AnalysisOfDayBox(
                tasks,
                date
            )
        }

        else -> {
            ContentSwitch(
                date,
                onChangeShowAnalyses,
                showAnalyses,
                tasks
            )
        }
    }
}

/**
 * Switches content that is shown below the monthly calendar and displays it accordingly
 */
@Composable
private fun ContentSwitch(
    date: LocalDate,
    onShowAnalysesChange: (Boolean) -> Unit,
    showAnalyses: Boolean,
    tasks: List<Task>
) {
    Box {
        Row(modifier = Modifier.fillMaxSize()) {
            if (showAnalyses) {
                AnalysisOfDayBox(
                    tasks,
                    date
                )
            } else {
                TasksOfDay(tasks = tasks)
            }
        }

        if (!date.isAfter(LocalDate.now()) && tasks.isNotEmpty()) {
            Icon(
                contentDescription = "Swap",
                imageVector = Icons.Outlined.SwapHoriz,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(30.dp)
                    .zIndex(1f)
                    .clickable { onShowAnalysesChange(!showAnalyses) },
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CalendarMonthly(
    date: LocalDate,
    dates: List<CalendarUiState.DateEntry>,
    days: Array<String>,
    expand: Boolean,
    month: YearMonth,
    onChangeExpand: (Boolean) -> Unit,
    onChangeShowAnalyses: (Boolean) -> Unit,
    onChangeShowDialog: (Boolean) -> Unit,
    onClick: (CalendarUiState.DateEntry) -> Unit,
    showAnalyses: Boolean,
    showDialog: Boolean,
    tasks: List<Task>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row {
            repeat(days.size) {
                Day(
                    day = days[it],
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Header(month = month)

        CalendarMonthlyContent(
            dates = dates,
            onClickListener = onClick,
            selectedDate = date
        )

        if (!expand) {
            return
        }

        Content(
            tasks = tasks,
            date = date,
            showAnalyses = showAnalyses,
            showDialog = showDialog,
            onChangeExpand = onChangeExpand,
            onChangeShowAnalyses = onChangeShowAnalyses,
            onChangeShowDialog = onChangeShowDialog
        )
    }
}

@Composable
private fun CalendarMonthlyContent(
    dates: List<CalendarUiState.DateEntry>,
    selectedDate: LocalDate,
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

                    CalendarMonthlyContentItem(
                        entry = item,
                        selectedDate = selectedDate,
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
private fun CalendarMonthlyContentItem(
    entry: CalendarUiState.DateEntry,
    selectedDate: LocalDate,
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
        clickable = true
    }

    Box(
        modifier = modifier
            .background(
                color = if (entry.date.isEqual(LocalDate.now())) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    Color.Transparent
                },
                MaterialTheme.shapes.medium
            )
            .border(
                width = 2.dp,
                color = if (entry.date.isEqual(selectedDate)) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Transparent
                },
                shape = MaterialTheme.shapes.medium
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

@Composable
fun CalendarMonthlyScreen(model: CalendarMonthlyViewModel = hiltViewModel()) {
    var date by remember { mutableStateOf(LocalDate.now()) }
    var expand by remember { mutableStateOf(false) }
    var showAnalyses by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val state by model.state.collectAsState()
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }

    CalendarMonthly(
        date = date,
        dates = state.dates,
        days = DateUtil.daysOfWeek,
        expand = expand,
        month = state.yearMonth,
        onChangeExpand = { expand = it },
        onChangeShowAnalyses = { showAnalyses = it },
        onChangeShowDialog = { showDialog = it },
        onClick = { entry ->
            date = entry.date
            expand = true
            showAnalyses = false
            showDialog = true
            tasks = entry.tasks
        },
        showAnalyses = showAnalyses,
        showDialog = showDialog,
        tasks = tasks
    )
}

@Composable
private fun Day(
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
private fun Header(
    model: CalendarMonthlyViewModel = hiltViewModel(),
    month: YearMonth
) {
    Row {
        IconButton(onClick = {
            model.changeMonth(month.minusMonths(1))
        }) {
            Icon(
                contentDescription = "Back",
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft
            )
        }

        Text(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            style = MaterialTheme.typography.bodyLarge,
            text = month.getDisplayName(),
            textAlign = TextAlign.Center
        )

        IconButton(onClick = {
            model.changeMonth(month.plusMonths(1))
        }) {
            Icon(
                contentDescription = "Forward",
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight
            )
        }
    }
}

@Composable
private fun TaskCreateDialog(
    selectedDate: LocalDate,
    onClose: (Boolean) -> Unit
) {
    TaskCreateDialog(
        date = LocalDateTime.of(
            selectedDate,
            LocalTime.now()
        ),
        onClose = { onClose(false) },
        isOpen = true,
        shouldCloseOnSubmit = true
    )
}