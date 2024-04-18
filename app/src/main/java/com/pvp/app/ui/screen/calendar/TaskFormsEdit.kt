package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.common.ButtonConfirm
import com.pvp.app.ui.common.EditableDateItem
import com.pvp.app.ui.common.EditableInfoItem
import com.pvp.app.ui.common.PickerState.Companion.rememberPickerState
import com.pvp.app.ui.common.PickerTime
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TaskEdit(
    task: Task,
    onDismissRequest: () -> Unit,
    showDialog: Boolean,
    model: TaskViewModel = hiltViewModel()
) {
    if (!showDialog) {
        return
    }

    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var duration by remember { mutableStateOf(task.duration) }
    var date by remember { mutableStateOf(task.date) }
    var activity by remember { mutableStateOf((task as? SportTask)?.activity) }
    var distance by remember { mutableStateOf((task as? SportTask)?.distance) }
    var recipe by remember { mutableStateOf((task as? MealTask)?.recipe) }
    var tempTitle by remember { mutableStateOf(title) }
    var tempDescription by remember { mutableStateOf(description) }
    var tempDuration by remember { mutableStateOf(duration) }
    var time by remember { mutableStateOf(task.time ?: LocalTime.MIN) }
    val tempHour = rememberPickerState(time.hour)
    val tempMinute = rememberPickerState(time.minute)
    var reminderTime by remember { mutableStateOf(task.reminderTime ?: Duration.ZERO) }
    var editingReminderTime by remember { mutableStateOf(reminderTime) }


    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        end = 15.dp,
                        start = 15.dp,
                        top = 15.dp
                    )
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EditableInfoItem(
                    dialogContent = {
                        OutlinedTextField(
                            label = { Text("Title") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            onValueChange = { newText ->
                                tempTitle = newText
                            },
                            value = tempTitle
                        )
                    },
                    dialogTitle = { Text("Editing title") },
                    label = "Title",
                    onConfirm = { title = tempTitle },
                    onDismiss = { tempTitle = title },
                    value = title
                )

                EditableInfoItem(
                    dialogContent = {
                        OutlinedTextField(
                            label = { Text("Description") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            onValueChange = { newText ->
                                tempDescription = newText
                            },
                            value = tempDescription ?: ""
                        )
                    },
                    dialogTitle = { Text("Editing description") },
                    label = "Description",
                    onConfirm = { description = tempDescription },
                    onDismiss = { tempDescription = description },
                    value = description ?: ""
                )

                when (task) {
                    is SportTask -> TaskEditFieldsSport(
                        task = task,
                        onActivityChange = { newActivity ->
                            activity = newActivity
                        },
                        onDistanceChange = { newDistance ->
                            distance = newDistance
                        },
                        onDurationChange = { newDuration ->
                            duration = newDuration
                        }
                    )

                    is MealTask -> TaskEditFieldsMeal(task) { newRecipe ->
                        recipe = newRecipe
                    }

                    else -> {}
                }

                if (task !is SportTask) {
                    EditableInfoItem(
                        dialogContent = {
                            Column {
                                Text(
                                    text = "Duration: ${tempDuration?.toMinutes()} minutes",
                                    style = TextStyle(fontSize = 16.sp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 4.dp)
                                )

                                Slider(
                                    value = tempDuration
                                        ?.toMinutes()
                                        ?.toFloat() ?: 0f,
                                    onValueChange = { newValue ->
                                        tempDuration = Duration.ofMinutes(newValue.toLong())
                                    },
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
                            }
                        },
                        dialogTitle = { Text("Editing duration") },
                        label = "Duration",
                        onConfirm = { duration = tempDuration },
                        onDismiss = { tempDuration = duration },
                        value = "${duration?.toMinutes()} minutes"
                    )
                }

                EditableInfoItem(
                    dialogContent = {
                        PickerTime(
                            selectedHour = tempHour,
                            selectedMinute = tempMinute,
                            onChange = { hour, minute ->
                                tempHour.value = hour
                                tempMinute.value = minute
                            }
                        )
                    },
                    dialogTitle = { Text("Editing scheduled at") },
                    label = "Scheduled at",
                    onConfirm = {
                        time = time
                            .withHour(tempHour.value)
                            .withMinute(tempMinute.value)
                    },
                    onDismiss = {
                        tempHour.value = time.hour
                        tempMinute.value = time.minute
                    },
                    value = if (task is SportTask && activity!!.supportsDistanceMetrics) {
                        time.format(DateTimeFormatter.ofPattern("HH:mm"))
                    } else {
                        "${time.format(DateTimeFormatter.ofPattern("HH:mm"))} - " +
                                (time.plus(duration)).format(DateTimeFormatter.ofPattern("HH:mm"))
                    }
                )

                EditableDateItem(
                    label = "Date",
                    value = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd, EEEE")),
                    onDateSelected = { selectedDate ->
                        date = date
                            .withYear(selectedDate.year)
                            .withMonth(selectedDate.monthValue)
                            .withDayOfMonth(selectedDate.dayOfMonth)
                    }
                )

                EditableInfoItem(
                    dialogContent = {
                        Column {
                            Text(
                                text = "Reminder Time: ${editingReminderTime?.toMinutes()} minutes",
                                style = TextStyle(fontSize = 16.sp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 4.dp)
                            )

                            Slider(
                                value = editingReminderTime
                                    ?.toMinutes()
                                    ?.toFloat() ?: 0f,
                                onValueChange = { newValue ->
                                    editingReminderTime = Duration.ofMinutes(newValue.toLong())
                                },
                                valueRange = 1f..120f,
                                steps = 120,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 8.dp,
                                        end = 8.dp,
                                        bottom = 8.dp
                                    )
                            )
                        }
                    },
                    dialogTitle = { Text("Editing reminder time") },
                    label = "Reminder Time",
                    onConfirm = { reminderTime = editingReminderTime },
                    onDismiss = { editingReminderTime = reminderTime },
                    value = "${reminderTime?.toMinutes()} minutes before task"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 24.dp,
                            vertical = 15.dp
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ButtonConfirm(
                        border = BorderStroke(
                            1.dp,
                            Color.Red
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        content = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    contentDescription = "Delete task icon",
                                    imageVector = Icons.Outlined.Delete
                                )

                                Text(
                                    style = MaterialTheme.typography.titleSmall,
                                    text = "Delete"
                                )
                            }
                        },
                        confirmationButtonContent = { Text("Delete") },
                        confirmationDescription = { Text("If the task is deleted, it cannot be recovered") },
                        confirmationTitle = { Text("Are you sure you want to delete this task?") },
                        onConfirm = {
                            model.remove(task)

                            onDismissRequest()
                        },
                        shape = MaterialTheme.shapes.extraLarge
                    )

                    Button(
                        onClick = {
                            model.update(
                                { task ->
                                    task.title = title
                                    task.description = description
                                    task.duration = duration
                                    task.reminderTime = reminderTime
                                    task.date = date
                                    task.time = time

                                    when (task) {
                                        is SportTask -> {
                                            task.activity = activity!!
                                            task.distance = distance
                                        }

                                        is MealTask -> task.recipe = recipe.toString()
                                    }
                                },
                                task
                            )

                            onDismissRequest()
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
