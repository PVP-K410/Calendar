package com.pvp.app.ui.screen.calendar.task

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.common.DatePickerDialog
import com.pvp.app.ui.common.EditableInfoItem
import com.pvp.app.ui.common.LabelFieldWrapper
import com.pvp.app.ui.common.Picker
import com.pvp.app.ui.common.PickerState.Companion.rememberPickerState
import com.pvp.app.ui.common.TimePicker
import java.time.Duration
import java.time.LocalDateTime
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
    var scheduledAt by remember { mutableStateOf(task.scheduledAt) }
    var activity by remember { mutableStateOf((task as? SportTask)?.activity) }
    var distance by remember { mutableStateOf((task as? SportTask)?.distance) }
    var recipe by remember { mutableStateOf((task as? MealTask)?.recipe) }
    var tempTitle by remember { mutableStateOf(title) }
    var tempDescription by remember { mutableStateOf(description) }
    var tempDuration by remember { mutableStateOf(duration) }
    val tempHour = rememberPickerState(scheduledAt.hour)
    val tempMinute = rememberPickerState(scheduledAt.minute)

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
                        TimePicker(
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
                        scheduledAt = scheduledAt
                            .withHour(tempHour.value)
                            .withMinute(tempMinute.value)
                    },
                    onDismiss = {
                        tempHour.value = scheduledAt.hour
                        tempMinute.value = scheduledAt.minute
                    },
                    value = if (task is SportTask && activity!!.supportsDistanceMetrics) {
                        scheduledAt.format(DateTimeFormatter.ofPattern("hh:mm a"))
                    } else {
                        "${scheduledAt.format(DateTimeFormatter.ofPattern("hh:mm a"))} - " +
                                (scheduledAt.plus(duration)).format(DateTimeFormatter.ofPattern("hh:mm a"))
                    }
                )

                FieldBox(
                    dialogContent = { showDialog, onDismiss, onDateSelected ->
                        DatePickerDialog(
                            showPicker = showDialog,
                            onDismiss = onDismiss,
                            onDateSelected = { selectedDate ->
                                scheduledAt = scheduledAt
                                    .withYear(selectedDate.year)
                                    .withMonth(selectedDate.monthValue)
                                    .withDayOfMonth(selectedDate.dayOfMonth)
                            }
                        )
                    },
                    label = "Date",
                    onConfirm = { },
                    value = scheduledAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd, EEEE"))
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
                    var showConfirmationDialog by remember { mutableStateOf(false) }

                    OutlinedButton(
                        onClick = { showConfirmationDialog = true },
                        shape = MaterialTheme.shapes.extraLarge,
                        border = BorderStroke(
                            1.dp,
                            Color.Red
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.Delete,
                                contentDescription = "Remove task"
                            )

                            Text(
                                "Delete",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }

                    if (showConfirmationDialog) {
                        AlertDialog(
                            onDismissRequest = { showConfirmationDialog = false },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        model.remove(task)

                                        showConfirmationDialog = false
                                        onDismissRequest()
                                    }
                                ) {
                                    Text("Yes")
                                }
                            },
                            dismissButton = {
                                Button(
                                    onClick = { showConfirmationDialog = false }
                                ) {
                                    Text("No")
                                }
                            },
                            title = { Text("Confirmation") },
                            text = { Text("Are you sure you want to delete?") }
                        )
                    }

                    Button(
                        onClick = {
                            model.update(
                                { task ->
                                    task.title = title
                                    task.description = description
                                    task.duration = duration
                                    task.scheduledAt = scheduledAt

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditFieldsSport(
    task: SportTask,
    model: TaskViewModel = hiltViewModel(),
    onActivityChange: (SportActivity?) -> Unit,
    onDistanceChange: (Double) -> Unit,
    onDurationChange: (Duration) -> Unit
) {
    var activity by remember { mutableStateOf(task.activity) }
    var distance by remember { mutableStateOf(task.distance) }
    var duration by remember { mutableStateOf(task.duration) }

    var tempActivity by remember { mutableStateOf(activity) }
    var tempDuration by remember { mutableStateOf(duration) }

    EditableInfoItem(
        dialogContent = {
            var isExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = { isExpanded = it },
            ) {
                androidx.compose.material3.TextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    value = tempActivity?.title ?: "",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }
                ) {
                    SportActivity.entries.forEach {
                        DropdownMenuItem(
                            text = { Text(text = it.title) },
                            onClick = {
                                tempActivity = it
                                isExpanded = false
                            }
                        )
                    }
                }
            }
        },
        dialogTitle = { Text("Editing activity") },
        label = "Activity",
        onConfirm = { activity = tempActivity },
        onDismiss = { tempActivity = activity },
        value = activity.title
    )

    if (activity.supportsDistanceMetrics) {
        val statePickerDistance = rememberPickerState(
            task.distance
                ?.toInt()
                ?: model.rangeKilometers.first()
        )

        EditableInfoItem(
            dialogContent = {
                LabelFieldWrapper(
                    content = {
                        Picker(
                            items = model.rangeKilometers,
                            label = { "$it (km)" },
                            state = statePickerDistance
                        )
                    },
                    putBelow = true,
                    text = "${statePickerDistance.value} (km) distance",
                    textAlign = TextAlign.End
                )
            },
            dialogTitle = { Text("Editing distance") },
            label = "Distance",
            onConfirm = { distance = statePickerDistance.value.toDouble() },
            onDismiss = { distance = (task as? SportTask)?.distance },
            value = "$distance (km)"
        )
    } else {
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

    onActivityChange(activity)

    distance?.let { onDistanceChange(it) }

    duration?.let { onDurationChange(it) }
}

@Composable
fun TaskEditFieldsMeal(
    task: MealTask,
    onRecipeChange: (String?) -> Unit
) {
    var recipe by remember { mutableStateOf(task.recipe) }
    var tempRecipe by remember { mutableStateOf(recipe) }

    EditableInfoItem(
        dialogContent = {
            OutlinedTextField(
                label = { Text("Recipe") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                onValueChange = { newText ->
                    tempRecipe = newText
                },
                value = tempRecipe
            )
        },
        dialogTitle = { Text("Editing recipe") },
        label = "Recipe",
        onConfirm = { recipe = tempRecipe },
        onDismiss = { tempRecipe = recipe },
        value = recipe
    )

    onRecipeChange(recipe)
}

@Composable
private fun FieldBox(
    dialogContent: @Composable (Boolean, () -> Unit, (LocalDateTime) -> Unit) -> Unit,
    label: String,
    onConfirm: (String) -> Unit,
    value: String,
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    fontWeight = FontWeight.Bold,
                    text = label
                )

                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit Icon Button",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { showDialog = true }
                )
            }

            Text(text = value)
        }

        dialogContent(
            showDialog,
            { showDialog = false },
            { selectedDate ->
                onConfirm(selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                showDialog = false
            }
        )
    }
}