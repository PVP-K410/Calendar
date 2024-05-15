package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.model.CustomMealTask
import com.pvp.app.model.GeneralTask
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.common.ButtonConfirm
import com.pvp.app.ui.common.EditableDateItem
import com.pvp.app.ui.common.EditableDistanceItem
import com.pvp.app.ui.common.EditablePickerItem
import com.pvp.app.ui.common.EditableSportActivityItem
import com.pvp.app.ui.common.EditableTextItem
import com.pvp.app.ui.common.EditableTimeItem
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

@Composable
fun TaskCommonForm(
    model: TaskViewModel = hiltViewModel(),
    date: LocalDateTime? = null,
    task: Task? = null,
    targetClass: KClass<out Task>? = null,
    onClose: () -> Unit,
) {
    if (task == null && targetClass == null) {
        return
    }

    val taskClass = targetClass ?: task!!::class
    val isCreateForm by remember { mutableStateOf(task == null) }
    var title by remember { mutableStateOf(task?.title ?: "") }
    var duration by remember { mutableStateOf(task?.duration) }
    var distance by remember { mutableStateOf((task as? SportTask)?.distance) }
    var reminderTime by remember { mutableStateOf(task?.reminderTime) }

    val isDescriptionSupported = remember(task) {
        taskClass in listOf(
            CustomMealTask::class,
            GeneralTask::class,
            SportTask::class
        )
    }

    var description by remember(isDescriptionSupported) {
        mutableStateOf(
            when (task) {
                is CustomMealTask -> task.recipe
                is GeneralTask -> task.description
                is SportTask -> task.description
                else -> ""
            } ?: ""
        )
    }

    var activity by remember {
        mutableStateOf(
            (task as? SportTask)?.activity ?: SportActivity.Walking
        )
    }

    var dateTime by remember {
        mutableStateOf(
            if (task?.date != null && task.time != null) {
                LocalDateTime.of(
                    task.date,
                    task.time
                )
            } else date ?: LocalDateTime.now()
        )
    }

    val isFormValid by remember {
        derivedStateOf {
            title.isNotEmpty()
        }
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clip(MaterialTheme.shapes.medium)
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EditableTextItem(
            label = "Title",
            value = title,
            onValueChange = { title = it },
            validate = { it.isNotEmpty() },
            errorMessage = "Title cannot be empty"
        )

        if (isDescriptionSupported) {
            EditableTextItem(
                label = if (taskClass == CustomMealTask::class) "Recipe" else "Description",
                value = description,
                onValueChange = { description = it }
            )
        }

        if (targetClass == SportTask::class) {
            EditableSportActivityItem(
                label = "Activity",
                value = activity,
                onValueChange = { activity = it }
            )
        }

        if (taskClass == SportTask::class && activity.supportsDistanceMetrics) {
            EditableDistanceItem(
                label = "Distance",
                value = distance,
                rangeKilometers = model.rangeKilometers,
                rangeMeters = model.rangeMeters,
                onValueChange = { distance = it }
            )
        }

        if (taskClass != SportTask::class || !activity.supportsDistanceMetrics) {
            EditablePickerItem(
                label = "Duration",
                value = duration,
                valueLabel = "minutes",
                items = model.rangeDuration,
                itemsLabels = "minutes",
                onValueChange = { duration = it }
            )
        }

        EditableTimeItem(
            label = "Scheduled at",
            value = dateTime.toLocalTime(),
            valueDisplay = if (
                (taskClass == SportTask::class &&
                        activity.supportsDistanceMetrics) ||
                duration == null
            ) {
                dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            } else {
                "${dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - " +
                        (dateTime.plus(duration)).format(DateTimeFormatter.ofPattern("HH:mm"))
            },
            onValueChange = {
                dateTime = dateTime
                    .withHour(it.hour)
                    .withMinute(it.minute)
            }
        )

        EditableDateItem(
            label = "Date",
            value = dateTime,
            onValueChange = { dateTime = it }
        )

        EditablePickerItem(
            label = "Reminder Time",
            value = reminderTime,
            valueLabel = "minutes before task",
            items = model.rangeReminderTime,
            itemsLabels = "minutes",
            onValueChange = { reminderTime = it }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 24.dp,
                    vertical = 15.dp
                ),
            horizontalArrangement = if (isCreateForm) Arrangement.Center else Arrangement.SpaceBetween
        ) {
            if (!isCreateForm) {
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
                        model.remove(task!!)

                        onClose()
                    },
                    shape = MaterialTheme.shapes.extraLarge
                )
            }

            Button(
                onClick = {
                    if (isCreateForm) {
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

                            CustomMealTask::class -> model.create(
                                date = dateTime.toLocalDate(),
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
                    } else {
                        model.update(
                            { task ->
                                updateTask(
                                    dateTime = dateTime,
                                    description = description,
                                    duration = duration,
                                    reminderTime = reminderTime,
                                    task = task,
                                    title = title
                                )
                            },
                            task!!
                        )
                    }

                    onClose()
                },
                enabled = isFormValid
            ) {
                Text(if (isCreateForm) "Create" else "Update")
            }
        }
    }
}

private fun updateTask(
    dateTime: LocalDateTime,
    description: String,
    duration: Duration?,
    reminderTime: Duration?,
    task: Task,
    title: String
): Task {
    return when (task) {
        is SportTask -> {
            SportTask.copy(
                task,
                date = dateTime.toLocalDate(),
                description = description,
                duration = duration,
                reminderTime = reminderTime,
                time = dateTime.toLocalTime(),
                title = title
            )
        }

        is CustomMealTask -> {
            CustomMealTask.copy(
                task,
                date = dateTime.toLocalDate(),
                duration = duration,
                recipe = description,
                reminderTime = reminderTime,
                time = dateTime.toLocalTime(),
                title = title
            )
        }

        is GeneralTask -> {
            GeneralTask.copy(
                task,
                date = dateTime.toLocalDate(),
                description = description,
                duration = duration,
                reminderTime = reminderTime,
                time = dateTime.toLocalTime(),
                title = title
            )
        }

        else -> Task.copy(
            task,
            date = dateTime.toLocalDate(),
            duration = duration,
            reminderTime = reminderTime,
            time = dateTime.toLocalTime(),
            title = title
        )
    }
}