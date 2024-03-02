package com.pvp.app.ui.screen.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pvp.app.model.Task
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

class GeneralTasksManager {

    val tasks = mutableStateListOf<Task>()

    fun addTask(
        title: String,
        description: String,
        startDate: LocalDateTime,
        duration: Int
    ) {
        if (title.isEmpty() || description.isEmpty()) {
            return
        }

        val task = Task(
            description = description,
            duration = Duration.ofMinutes(duration.toLong()),
            isCompleted = false,
            scheduledAt = startDate,
            title = title,
            userEmail = "TODO"
        )

        tasks.add(task)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TaskForm(
    manager: GeneralTasksManager
) {
    var description by remember { mutableStateOf("") }
    var duration by remember { mutableFloatStateOf(0.0f) }
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    var showPickerDate by remember { mutableStateOf(false) }
    var showPickerTime by remember { mutableStateOf(false) }
    val stateDate = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)
    val stateTime = rememberTimePickerState(
        initialHour = now.hour,
        initialMinute = now.minute,
        is24Hour = true
    )
    var title by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create a General Task",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.15.sp
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = "Start Date:\n${now.format(dateFormatter)}",
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            Button(
                onClick = {
                    showPickerDate = true
                },
                modifier = Modifier.wrapContentWidth()
            ) {
                Text("Set Date")
            }

            Button(
                onClick = {
                    showPickerTime = true
                },
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(2.dp)
            ) {
                Text("Set Time")
            }
        }

        Text(
            text = "Duration: ${duration.toInt()} minutes",
            style = TextStyle(fontSize = 16.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    top = 8.dp
                )
        )

        Slider(
            value = duration,
            onValueChange = { duration = it },
            valueRange = 1f..180f,
            steps = 180,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    bottom = 8.dp
                )
        )

        if (showPickerDate) {
            DatePickerDialog(
                onDismissRequest = {
                    showPickerDate = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showPickerDate = false

                            val instant = stateDate.selectedDateMillis?.let {
                                Instant.ofEpochMilli(it)
                            }

                            now = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showPickerDate = false
                        }
                    ) {
                        Text("CANCEL")
                    }
                }
            ) {
                DatePicker(
                    state = stateDate
                )
            }
        }

        if (showPickerTime) {
            TimePickerDialog(
                onCancel = { showPickerTime = false },
                onConfirm = {
                    showPickerTime = false

                    now = now
                        .withHour(stateTime.hour)
                        .withMinute(stateTime.minute)
                },
            ) {
                TimeInput(state = stateTime)
            }
        }

        Button(
            onClick = {
                manager.addTask(
                    title = title,
                    description = description,
                    startDate = now,
                    duration = duration.toInt()
                )

                description = ""
                duration = 0.0f
                now = LocalDateTime.now()
                stateDate.selectedDateMillis = now
                    .atZone(ZoneOffset.systemDefault())
                    ?.toInstant()
                    ?.toEpochMilli()
                title = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Add Task")
        }
    }
}

@Composable
fun TaskList(tasks: List<Task>) {
    LazyColumn {
        item {
            Text(
                text = "Your General Tasks",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        if (tasks.isEmpty()) {
            item {
                Text(
                    text = "No tasks available",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        } else {
            items(tasks) { task ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = task.title,
                                fontWeight = FontWeight.Bold
                            )

                            task.description?.let { Text(text = it) }
                        }

                        Text(
                            text = task.scheduledAt.format(dateFormatter),
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )

                content()

                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()

                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(
                        onClick = onCancel
                    ) {
                        Text("Cancel")
                    }

                    TextButton(
                        onClick = onConfirm
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Composable
fun TaskFormAndList() {
    val manager = GeneralTasksManager()

    Column {
        TaskForm(manager)

        TaskList(manager.tasks)
    }
}
