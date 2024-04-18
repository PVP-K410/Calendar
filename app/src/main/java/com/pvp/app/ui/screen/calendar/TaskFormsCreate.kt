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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.window.Dialog
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
import java.time.LocalTime
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
fun TaskCreateDialog(
    date: LocalDateTime? = null,
    onClose: () -> Unit,
    isOpen: Boolean,
    shouldCloseOnSubmit: Boolean
) {
    if (!isOpen) {
        return
    }

    Dialog(onDismissRequest = onClose) {
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

            fun closeIfShould() {
                if (shouldCloseOnSubmit) {
                    onClose()
                }
            }

            TaskCreateNew(
                targetClass = target,
                date = date,
                onCreate = ::closeIfShould
            )
        }
    }
}

@Composable
fun TaskCreateNew(
    model: TaskViewModel = hiltViewModel(),
    date: LocalDateTime? = null,
    targetClass: KClass<out Task>,
    onCreate: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf(Duration.ofMinutes(0)) }
    var activity by remember { mutableStateOf(SportActivity.Walking) }
    var distance by remember { mutableDoubleStateOf(0.0) }
    var recipe by remember { mutableStateOf("") }
    var tempTitle by remember { mutableStateOf("") }
    var tempDescription by remember { mutableStateOf("") }
    var tempDuration by remember { mutableStateOf(Duration.ofMinutes(0)) }
    var selectedDateTime by remember { mutableStateOf(date ?: LocalDateTime.now()) }
    var time by remember { mutableStateOf(selectedDateTime.toLocalTime()) }
    val tempHour = rememberPickerState(time.hour)
    val tempMinute = rememberPickerState(time.minute)

    val isFormValid by remember(targetClass) {
        derivedStateOf {
            when (targetClass) {
                MealTask::class -> title.isNotEmpty() && description.isNotEmpty() && !duration.isZero
                SportTask::class -> title.isNotEmpty()
                else -> title.isNotEmpty() && description.isNotEmpty()
            }
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
                        value = tempDescription
                    )
                },
                dialogTitle = { Text("Editing description") },
                label = "Description",
                onConfirm = { description = tempDescription },
                onDismiss = { tempDescription = description },
                value = description
            )

            when (targetClass) {
                SportTask::class -> TaskEditFieldsSport(
                    onActivityChange = { newActivity ->
                        if (newActivity != null) {
                            activity = newActivity
                        }
                    },
                    onDistanceChange = { newDistance ->
                        distance = newDistance
                    },
                    onDurationChange = { newDuration ->
                        duration = newDuration
                    }
                )

                MealTask::class -> TaskEditFieldsMeal() { newRecipe ->
                    recipe = newRecipe
                }

                else -> {}
            }

            if (targetClass != MealTask::class) {
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
                value = if (activity.supportsDistanceMetrics) {
                    time.format(DateTimeFormatter.ofPattern("HH:mm"))
                } else {
                    "${time.format(DateTimeFormatter.ofPattern("HH:mm"))} - " +
                            (time.plus(duration)).format(DateTimeFormatter.ofPattern("HH:mm"))
                }
            )

            EditableDateItem(
                label = "Date",
                value = selectedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd, EEEE")),
                onDateSelected = { selectedDate ->
                    selectedDateTime = selectedDateTime
                        .withYear(selectedDate.year)
                        .withMonth(selectedDate.monthValue)
                        .withDayOfMonth(selectedDate.dayOfMonth)
                }
            )

            Button(
                onClick = {
                    when (targetClass) {
                        SportTask::class -> model.create(
                            date = selectedDateTime.toLocalDate(),
                            activity = activity,
                            description = description,
                            distance = distance,
                            duration = duration,
                            time = selectedDateTime
                                .toLocalTime()
                                .let { if (it == LocalTime.MIN) null else it },
                            title = title
                        )

                        MealTask::class -> model.create(
                            date = selectedDateTime.toLocalDate(),
                            description = description,
                            duration = duration,
                            recipe = recipe,
                            time = selectedDateTime
                                .toLocalTime()
                                .let { if (it == LocalTime.MIN) null else it },
                            title = title
                        )

                        else -> model.create(
                            date = selectedDateTime.toLocalDate(),
                            description = description,
                            duration = duration,
                            time = selectedDateTime.toLocalTime(),
                            title = title
                        )

                    }

                    onCreate()
                },
                enabled = isFormValid,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Create")
            }
        }
    }
}
