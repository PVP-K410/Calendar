package com.pvp.app.ui.screen.task

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.Slider
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.pvp.app.model.Task
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskForm(generalTasksManager: GeneralTasksManager) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(LocalDateTime.now()) }
    var duration by remember { mutableStateOf(0) }
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)
    val isDatePickerDialogOpen = remember { mutableStateOf(false) }

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
                text = "Start Date: ${startDate.toLocalDate()}",
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            Button(
                onClick = {
                    isDatePickerDialogOpen.value = true
                },
                modifier = Modifier.wrapContentWidth()
            ) {
                Text("Pick Date")
            }
        }

        Slider(
            value = duration.toFloat(),
            onValueChange = { newValue -> duration = newValue.toInt() },
            valueRange = 1f..180f,
            steps = 180,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        )

        Text(
            text = "Duration: $duration minutes",
            style = TextStyle(
                fontSize = 15.sp,
                color = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )

        if (isDatePickerDialogOpen.value) {
            DatePickerDialog(
                onDismissRequest = {
                    isDatePickerDialogOpen.value = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            isDatePickerDialogOpen.value = false

                            val instant = datePickerState.selectedDateMillis?.let {
                                Instant.ofEpochMilli(it)
                            }

                            startDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            isDatePickerDialogOpen.value = false
                        }
                    ) {
                        Text("CANCEL")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState
                )
            }
        }

        Button(
            onClick = {
                generalTasksManager.addTask(
                    title = title,
                    description = description,
                    startDate = startDate,
                    duration = duration
                )

                title = ""
                description = ""
                startDate = LocalDateTime.now()
                duration = 0
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
                            text = "${task.scheduledAt.toLocalDate()}",
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskFormAndList() {
    val generalTasksManager = GeneralTasksManager()

    Column {
        TaskForm(generalTasksManager)

        TaskList(generalTasksManager.tasks)
    }
}
