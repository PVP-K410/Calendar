@file:OptIn(ExperimentalMaterial3Api::class)

package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.common.ButtonConfirm
import com.pvp.app.ui.common.EditableDateItem
import com.pvp.app.ui.common.EditableInfoItem
import com.pvp.app.ui.common.Picker
import com.pvp.app.ui.common.PickerState.Companion.rememberPickerState
import com.pvp.app.ui.common.PickerTime
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TaskEditSheet(
    task: Task,
    onDialogClose: () -> Unit,
    model: TaskViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var duration by remember { mutableStateOf(task.duration) }
    var date by remember { mutableStateOf(task.date) }
    var activity by remember { mutableStateOf((task as? SportTask)?.activity) }
    var distance by remember { mutableStateOf((task as? SportTask)?.distance) }
    var tempTitle by remember { mutableStateOf(title) }
    var tempDescription by remember { mutableStateOf(description) }
    var tempDuration by remember { mutableStateOf(duration) }
    var time by remember { mutableStateOf(task.time ?: LocalTime.MIN) }
    val tempHour = rememberPickerState(time.hour)
    val tempMinute = rememberPickerState(time.minute)
    var reminderTime by remember { mutableStateOf(task.reminderTime) }
    var editingReminderTime by remember { mutableStateOf(reminderTime) }
    val descriptionLabel = when (task::class) {
        MealTask::class -> "Recipe"
        else -> "Description"
    }

    ModalBottomSheet(
        onDismissRequest = onDialogClose,
        sheetState = rememberModalBottomSheetState(true)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
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
                        label = { Text(descriptionLabel) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        onValueChange = { newText ->
                            tempDescription = newText
                        },
                        value = tempDescription ?: ""
                    )
                },
                dialogTitle = { Text("Editing $descriptionLabel") },
                label = descriptionLabel,
                onConfirm = { description = tempDescription },
                onDismiss = { tempDescription = description },
                value = description ?: ""
            )

            if (task is SportTask) {
                TaskEditFieldsSport(
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
            } else {
                EditableInfoItem(
                    dialogContent = {
                        Column {
                            val initial = remember(tempDuration) {
                                tempDuration
                                    ?.toMinutes()
                                    ?.toFloat()
                                    ?: 0f
                            }

                            Picker(
                                items = (0..300 step 5).toList(),
                                label = { "$it minutes" },
                                onChange = { tempDuration = Duration.ofMinutes(it.toLong()) },
                                startIndex = initial.toInt() / 5,
                                state = rememberPickerState(initialValue = initial)
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
                        val initial = remember(editingReminderTime) {
                            editingReminderTime
                                ?.toMinutes()
                                ?.toFloat()
                                ?: 0f
                        }

                        Picker(
                            items = (0..120 step 5).toList(),
                            label = { "$it minutes" },
                            onChange = { editingReminderTime = Duration.ofMinutes(it.toLong()) },
                            startIndex = initial.toInt() / 5,
                            state = rememberPickerState(initialValue = initial)
                        )
                    }
                },
                dialogTitle = { Text("Editing reminder time") },
                label = "Reminder Time",
                onConfirm = { reminderTime = editingReminderTime },
                onDismiss = { editingReminderTime = reminderTime },
                value = if (reminderTime != null)
                    "${reminderTime?.toMinutes()} ${
                        if (reminderTime
                                ?.toMinutes()
                                ?.toInt() == 1
                        ) "minute" else "minutes"
                    } before task" else ""
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

                        onDialogClose()
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

                                    is MealTask -> {
                                        task.recipe = description ?: ""
                                        task.description = ""
                                    }
                                }
                            },
                            task
                        )

                        onDialogClose()
                    }
                ) {
                    Text("Save")
                }
            }
        }
    }
}
