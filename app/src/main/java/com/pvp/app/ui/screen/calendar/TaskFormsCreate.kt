@file:OptIn(ExperimentalMaterial3Api::class)

package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.common.EditableDateItem
import com.pvp.app.ui.common.EditableInfoItem
import com.pvp.app.ui.common.PickerState.Companion.rememberPickerState
import com.pvp.app.ui.common.PickerTime
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

@Composable
private fun TaskTypeSelector(
    isSelected: Boolean,
    onSelect: () -> Unit,
    text: String
) {
    Button(onClick = onSelect) {
        if (isSelected) {
            Icon(
                contentDescription = "Currently selected form",
                imageVector = Icons.Outlined.Place
            )
        }

        Text(
            style = MaterialTheme.typography.labelLarge,
            text = text
        )
    }
}

@Composable
fun TaskCreateSheetContent(
    date: LocalDateTime? = null,
    onClose: () -> Unit,
    isOpen: Boolean,
    shouldCloseOnSubmit: Boolean
) {
    if (!isOpen) {
        return
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(8.dp)
        ) {
            var target by remember { mutableStateOf(Task::class as KClass<out Task>) }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TaskTypeSelector(
                    isSelected = target == Task::class,
                    onSelect = { target = Task::class },
                    text = "General"
                )

                TaskTypeSelector(
                    isSelected = target == MealTask::class,
                    onSelect = { target = MealTask::class },
                    text = "Meal"
                )

                TaskTypeSelector(
                    isSelected = target == SportTask::class,
                    onSelect = { target = SportTask::class },
                    text = "Sport"
                )
            }

            Spacer(modifier = Modifier.padding(4.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.padding(4.dp))

            TaskCreateForm(
                targetClass = target,
                date = date,
                onCreate = {
                    if (shouldCloseOnSubmit) {
                        onClose()
                    }
                }
            )
        }
    }
}

@Composable
fun TaskCreateForm(
    model: TaskViewModel = hiltViewModel(),
    date: LocalDateTime? = null,
    targetClass: KClass<out Task>,
    onCreate: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf(Duration.ofMinutes(0)) }
    var reminderTime by remember { mutableStateOf<Duration?>(null) }
    var activity by remember { mutableStateOf(SportActivity.Walking) }
    var distance by remember { mutableDoubleStateOf(0.0) }
    var dateTime by remember { mutableStateOf(date ?: LocalDateTime.now()) }
    var editingTitle by remember { mutableStateOf("") }
    var editingDescription by remember { mutableStateOf("") }
    var editingDuration by remember { mutableStateOf(Duration.ofMinutes(0)) }
    var editingReminderTime by remember { mutableStateOf(reminderTime) }
    val editingHour = rememberPickerState(dateTime.hour)
    val editingMinute = rememberPickerState(dateTime.minute)

    val descriptionLabel = when (targetClass) {
        MealTask::class -> "Recipe"
        else -> "Description"
    }

    val isFormValid by remember(targetClass) {
        derivedStateOf {
            title.isNotEmpty()
        }
    }

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
                            editingTitle = newText
                        },
                        value = editingTitle
                    )
                },
                dialogTitle = { Text("Editing title") },
                label = "Title",
                onConfirm = { title = editingTitle },
                onDismiss = { editingTitle = title },
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
                            editingDescription = newText
                        },
                        value = editingDescription
                    )
                },
                dialogTitle = { Text("Editing $descriptionLabel") },
                label = descriptionLabel,
                onConfirm = { description = editingDescription },
                onDismiss = { editingDescription = description },
                value = description
            )

            when (targetClass) {
                SportTask::class -> TaskEditFieldsSport(
                    onActivityChange = { newActivity ->
                        if (newActivity != null) {
                            activity = newActivity
                            //supportsDistanceMetrics = activity.supportsDistanceMetrics
                        }
                    },
                    onDistanceChange = { newDistance ->
                        distance = newDistance
                    },
                    onDurationChange = { newDuration ->
                        duration = newDuration
                    }
                )

                else -> {}
            }

            if (targetClass != SportTask::class) {
                EditableInfoItem(
                    dialogContent = {
                        Column {
                            Text(
                                text = "Duration: ${editingDuration?.toMinutes()} minutes",
                                style = TextStyle(fontSize = 16.sp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 4.dp)
                            )

                            Slider(
                                value = editingDuration
                                    ?.toMinutes()
                                    ?.toFloat() ?: 0f,
                                onValueChange = { newValue ->
                                    editingDuration = Duration.ofMinutes(newValue.toLong())
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
                    onConfirm = { duration = editingDuration },
                    onDismiss = { editingDuration = duration },
                    value = "${duration?.toMinutes()} minutes"
                )
            }

            EditableInfoItem(
                dialogContent = {
                    PickerTime(
                        selectedHour = editingHour,
                        selectedMinute = editingMinute,
                        onChange = { hour, minute ->
                            editingHour.value = hour
                            editingMinute.value = minute
                        }
                    )
                },
                dialogTitle = { Text("Editing scheduled at") },
                label = "Scheduled at",
                onConfirm = {
                    dateTime = dateTime
                        .withHour(editingHour.value)
                        .withMinute(editingMinute.value)
                },
                onDismiss = {
                    editingHour.value = dateTime.hour
                    editingMinute.value = dateTime.minute
                },
                value = if (activity.supportsDistanceMetrics) {
                    dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                } else {
                    "${dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - " +
                            (dateTime.plus(duration)).format(DateTimeFormatter.ofPattern("HH:mm"))
                }
            )

            EditableDateItem(
                label = "Date",
                value = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd, EEEE")),
                onDateSelected = { selectedDate ->
                    dateTime = dateTime
                        .withYear(selectedDate.year)
                        .withMonth(selectedDate.monthValue)
                        .withDayOfMonth(selectedDate.dayOfMonth)
                }
            )

            EditableInfoItem(
                dialogContent = {
                    Column {
                        Text(
                            text = "Reminder Time: ${editingReminderTime?.toMinutes() ?: 0} minutes",
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
                value = if (reminderTime != null)
                    "${reminderTime?.toMinutes()} ${
                        if (reminderTime
                                ?.toMinutes()
                                ?.toInt() == 1
                        ) "minute" else "minutes"
                    } before task" else ""
            )

            Button(
                onClick = {
                    when (targetClass) {
                        SportTask::class -> model.create(
                            date = dateTime.toLocalDate(),
                            activity = activity,
                            description = description,
                            distance = distance,
                            duration = duration,
                            reminderTime = reminderTime,
                            time = dateTime.toLocalTime(),
                            title = title
                        )

                        MealTask::class -> model.create(
                            date = dateTime.toLocalDate(),
                            description = "",
                            duration = duration,
                            reminderTime = reminderTime,
                            recipe = description,
                            time = dateTime.toLocalTime(),
                            title = title
                        )

                        else -> model.create(
                            date = dateTime.toLocalDate(),
                            description = description,
                            duration = duration,
                            reminderTime = reminderTime,
                            time = dateTime.toLocalTime(),
                            title = title
                        )
                    }

                    onCreate()
                },
                enabled = isFormValid,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Create")
            }
        }
    }
}
