package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import com.pvp.app.common.util.DateUtil
import com.pvp.app.common.util.DateUtil.getDisplayName
import com.pvp.app.model.Task
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth

@Composable
fun CalendarMonthlyScreen(
    model: CalendarMonthlyViewModel = hiltViewModel(),
) {
    val state by model.state.collectAsState()
    var expand by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var dateTasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var showActivitiesBox by remember { mutableStateOf(false) }

    MonthlyCalendar(
        days = DateUtil.daysOfWeek,
        month = state.yearMonth,
        dates = state.dates,
        expand = expand,
        onExpandChange = { expand = it },
        showDialog = showDialog,
        onShowDialogChange = { showDialog = it },
        selectedDate = selectedDate,
        dateTasks = dateTasks,
        showActivitiesBox = showActivitiesBox,
        onShowActivitiesBoxChange = { showActivitiesBox = it },
        onClickListener = { date ->
            expand = true
            showDialog = true
            selectedDate = date.date
            dateTasks = date.tasks
            showActivitiesBox = false
        }
    )
}

@Composable
fun MonthlyCalendar(
    days: Array<String>,
    month: YearMonth,
    dates: List<CalendarUiState.DateEntry>,
    expand: Boolean,
    onExpandChange: (Boolean) -> Unit,
    showDialog: Boolean,
    onShowDialogChange: (Boolean) -> Unit,
    selectedDate: LocalDate,
    dateTasks: List<Task>,
    showActivitiesBox: Boolean,
    onShowActivitiesBoxChange: (Boolean) -> Unit,
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
            selectedDate = selectedDate,
            onClickListener = onClickListener
        )

        if (expand) {
            when {
                dateTasks.isEmpty() && selectedDate == LocalDate.now() -> {
                    showActivitiesBox(
                        dateTasks,
                        selectedDate
                    )

                    if (showDialog) {
                        createTaskDialog(
                            selectedDate,
                            showDialog,
                            onShowDialogChange
                        )
                    }
                }

                dateTasks.isEmpty() && !selectedDate.isBefore(LocalDate.now()) -> {
                    createTaskDialog(
                        selectedDate,
                        expand,
                        onExpandChange
                    )
                }

                dateTasks.isEmpty() && !selectedDate.isAfter(LocalDate.now()) -> {
                    showActivitiesBox(
                        dateTasks,
                        selectedDate
                    )
                }

                else -> {
                    showDayContentOrActivitiesBox(
                        dateTasks,
                        selectedDate,
                        showActivitiesBox,
                        onShowActivitiesBoxChange
                    )
                }
            }
        }
    }
}

@Composable
fun showActivitiesBox(
    dateTasks: List<Task>,
    selectedDate: LocalDate
) {
    Box {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp)
        ) {
            ActivitiesBox(
                date = selectedDate,
                tasks = dateTasks
            )
        }
    }
}

@Composable
fun createTaskDialog(
    selectedDate: LocalDate,
    isOpen: Boolean,
    onClose: (Boolean) -> Unit
) {
    CreateTaskDialog(
        date = LocalDateTime.of(
            selectedDate,
            LocalTime.now()
        ),
        onClose = { onClose(false) },
        isOpen = isOpen,
        shouldCloseOnSubmit = true
    )
}

@Composable
fun showDayContentOrActivitiesBox(
    dateTasks: List<Task>,
    selectedDate: LocalDate,
    showActivitiesBox: Boolean,
    onShowActivitiesBoxChange: (Boolean) -> Unit
) {
    Box {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            if (showActivitiesBox) {
                showActivitiesBox(
                    dateTasks,
                    selectedDate
                )
            } else {
                DayContent(tasks = dateTasks)
            }
        }

        if (!selectedDate.isAfter(LocalDate.now()) && dateTasks.isNotEmpty()) {
            Icon(
                imageVector = Icons.Outlined.SwapHoriz,
                contentDescription = "Swap",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(30.dp)
                    .zIndex(1f)
                    .clickable { onShowActivitiesBoxChange(!showActivitiesBox) },
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun Header(
    month: YearMonth,
    model: CalendarMonthlyViewModel = hiltViewModel()
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

                    ContentItem(
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
fun ContentItem(
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
