package com.pvp.app.ui.screen.task

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.common.InputValidator
import com.pvp.app.common.getDurationString
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.common.DatePickerDialog
import com.pvp.app.ui.common.DateTimePicker
import com.pvp.app.ui.common.EditableInfoItem
import com.pvp.app.ui.common.LabelFieldWrapper
import com.pvp.app.ui.common.Picker
import com.pvp.app.ui.common.PickerState.Companion.rememberPickerState
import com.pvp.app.ui.common.TextField
import com.pvp.app.ui.common.TimePicker
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.round

private val RANGE_KILOMETERS = List(1001) {
    round(it * 0.1 * 10) / 10
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun CreateTaskMealForm(
    date: LocalDateTime? = null,
    model: TaskViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onCreate: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(true) }
    var description by remember { mutableStateOf("") }
    var descriptionError by remember { mutableStateOf(true) }
    var ingredients by remember { mutableStateOf("") }
    var preparation by remember { mutableStateOf("") }
    var duration by remember { mutableIntStateOf(0) }
    var selectedDateTime by remember { mutableStateOf(date ?: LocalDateTime.now()) }

    val isFormValid by derivedStateOf {
        !titleError && !descriptionError && duration > 0
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = title,
            onValueChange = { newText, errors ->
                title = newText
                titleError = errors.isNotEmpty()
            },
            validationPolicies = { input ->
                InputValidator.validateBlank(
                    input,
                    "Title"
                )
            },
            label = { Text("Meal Title") },
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = description,
            onValueChange = { newText, errors ->
                description = newText
                descriptionError = errors.isNotEmpty()
            },
            validationPolicies = { input ->
                InputValidator.validateBlank(
                    input,
                    "Description"
                )
            },
            label = { Text("Description") },
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = ingredients,
            onValueChange = { newText, _ ->
                ingredients = newText
            },
            label = { Text("Ingredients") },
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = preparation,
            onValueChange = { newText, _ ->
                preparation = newText
            },
            label = { Text("Preparation") },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = "Duration (minutes): $duration",
        )

        Slider(
            value = duration.toFloat(),
            onValueChange = { duration = it.toInt() },
            valueRange = 1f..180f,
            steps = 179,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        DateTimePicker(
            dateTime = selectedDateTime,
            onDateTimeChanged = { newDateTime ->
                selectedDateTime = newDateTime
            }
        )

        Button(
            onClick = {
                model.create(
                    description = description,
                    duration = Duration.ofMinutes(duration.toLong()),
                    ingredients = ingredients,
                    preparation = preparation,
                    scheduledAt = selectedDateTime,
                    title = title
                )

                onCreate()
            },
            enabled = isFormValid,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp)
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Submit",
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskSportForm(
    date: LocalDateTime? = null,
    model: TaskViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onCreate: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var titleValid by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var activity by remember { mutableStateOf(SportActivity.Walking) }
    var duration by remember { mutableIntStateOf(0) }
    var selectedDateTime by remember { mutableStateOf(date ?: LocalDateTime.now()) }
    var isExpanded by remember { mutableStateOf(false) }
    val statePickerDistance = rememberPickerState(0.0)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = title,
            onValueChange = { newText, errors ->
                title = newText
                titleValid = errors.isEmpty()
            },
            validationPolicies = { input ->
                InputValidator.validateBlank(
                    input,
                    "Title"
                )
            },
            label = { Text("Title") },
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded },
        ) {
            androidx.compose.material3.TextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = activity.title,
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
                            activity = it
                            isExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = description,
            onValueChange = { newText, _ ->
                description = newText
            },
            label = { Text("Description") },
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (activity.supportsDistanceMetrics) {
            LabelFieldWrapper(
                content = {
                    Picker(
                        items = RANGE_KILOMETERS,
                        label = { "$it (km)" },
                        state = statePickerDistance
                    )
                },
                putBelow = true,
                text = "${statePickerDistance.value} (km) distance",
                textAlign = TextAlign.End
            )
        } else {
            Text(
                text = "Duration: $duration minutes",
                style = TextStyle(
                    fontSize = 15.sp,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )

            Slider(
                value = duration.toFloat(),
                onValueChange = { newValue ->
                    duration = newValue.toInt()
                },
                valueRange = 1f..180f,
                steps = 180,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            )
        }

        Spacer(modifier = Modifier.height((32.dp)))

        DateTimePicker(
            dateTime = selectedDateTime,
            onDateTimeChanged = { newDateTime ->
                selectedDateTime = newDateTime
            }
        )

        Button(
            onClick = {
                model.create(
                    activity = activity,
                    description = description,
                    distance = statePickerDistance.value,
                    duration = Duration.ofMinutes(duration.toLong()),
                    scheduledAt = selectedDateTime,
                    title = title
                )

                onCreate()
            },
            enabled = titleValid,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp)
        ) {
            Text("Submit")
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun CreateTaskGeneralForm(
    date: LocalDateTime? = null,
    model: TaskViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onCreate: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(true) }
    var description by remember { mutableStateOf("") }
    var descriptionError by remember { mutableStateOf(true) }
    var duration by remember { mutableFloatStateOf(0.0f) }
    var selectedDateTime by remember { mutableStateOf(date ?: LocalDateTime.now()) }

    val isFormValid by derivedStateOf {
        !titleError && !descriptionError
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = title,
            onValueChange = { newText, errors ->
                title = newText
                titleError = errors.isNotEmpty()
            },
            validationPolicies = { input ->
                InputValidator.validateBlank(
                    input,
                    "Title"
                )
            },
            label = { Text("Title") },
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = description,
            onValueChange = { newText, errors ->
                description = newText
                descriptionError = errors.isNotEmpty()
            },
            validationPolicies = { input ->
                InputValidator.validateBlank(
                    input,
                    "Description"
                )
            },
            label = { Text("Description") },
        )

        Spacer(modifier = Modifier.height(16.dp))

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

        DateTimePicker(
            dateTime = selectedDateTime,
            onDateTimeChanged = { newDateTime ->
                selectedDateTime = newDateTime
            }
        )

        Button(
            onClick = {
                model.create(
                    description = description,
                    duration = Duration.ofMinutes(duration.toLong()),
                    scheduledAt = selectedDateTime,
                    title = title
                )

                onCreate()
            },
            enabled = isFormValid,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp)
        ) {
            Text("Submit")
        }
    }
}

@Composable
private fun MealTaskBoxBody(
    task: MealTask
) {
    task.duration?.let { duration ->
        Row(
            modifier = Modifier.padding(6.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Timelapse,
                contentDescription = "Duration"
            )

            Text(
                text = duration.asString(),
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            )
        }
    }

    Text(
        "Main ingredient: " + task.recipe,
        textAlign = TextAlign.Left,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
    )
}

@Composable
private fun SportTaskBoxBody(task: SportTask) {
    if (task.distance != null && task.distance!! > 0) {
        Row(modifier = Modifier.padding(6.dp)) {
            Icon(
                imageVector = Icons.Outlined.Straighten,
                contentDescription = "Distance"
            )

            Text(
                text = "${task.distance} km",
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            )
        }
    } else if (task.duration != null) {
        Row(modifier = Modifier.padding(6.dp)) {
            Icon(
                imageVector = Icons.Outlined.Timelapse,
                contentDescription = "Duration"
            )

            Text(
                text = task.duration!!.asString(),
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            )
        }
    }

    Row(modifier = Modifier.padding(6.dp)) {
        Icon(
            imageVector = task.activity.icon,
            contentDescription = "Activity"
        )

        Text(
            text = task.activity.title,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        )
    }
}

@Composable
private fun TaskBoxBody(
    task: Task
) {
    task.duration?.let { duration ->
        Row(
            modifier = Modifier.padding(6.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Timelapse,
                contentDescription = "Duration"
            )

            Text(
                text = duration.asString(),
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            )
        }
    }

    task.description?.let { description ->
        Text(
            text = description,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        )
    }
}

@Composable
fun TaskBox(
    task: Task,
    model: TaskViewModel = hiltViewModel()
) {
    var checked = task.isCompleted
    var showDialog by remember { mutableStateOf(false) }

    if (task !is SportTask || !task.isDaily) {
        if (showDialog) {
            TaskPreviewDialog(
                task,
                onDismissRequest = { showDialog = false },
                showDialog
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant
                ),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { showDialog = true }
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 16.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = {
                        model.update(
                            { task -> task.isCompleted = it },
                            task
                        )

                        checked = it
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .align(CenterVertically)
                )

                Text(
                    text = task.title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(CenterVertically)
                        .weight(1f),
                    fontSize = 20.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Spacer(modifier = Modifier.height(4.dp))

                    when (task) {
                        is SportTask -> SportTaskBoxBody(task)
                        is MealTask -> MealTaskBoxBody(task)
                        else -> TaskBoxBody(task)
                    }
                }

                Text(
                    text = task.scheduledAt
                        .toLocalTime()
                        .format(DateTimeFormatter.ofPattern("HH:mm")),
                    modifier = Modifier.weight(0.3f),
                    fontSize = 22.sp
                )
            }
        }
    }
}

@Composable
fun TaskPreviewDialog(
    task: Task,
    onDismissRequest: () -> Unit,
    showDialog: Boolean,
    model: TaskViewModel = hiltViewModel()
) {
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
    var tempHour = rememberPickerState(scheduledAt.hour)
    var tempMinute = rememberPickerState(scheduledAt.minute)

    if (showDialog) {
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
                        is SportTask -> SportTaskFields(
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

                        is MealTask -> MealTaskFields(task) { newRecipe ->
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
                            "${scheduledAt.format(DateTimeFormatter.ofPattern("hh:mm a"))}"
                        } else {
                            "${scheduledAt.format(DateTimeFormatter.ofPattern("hh:mm a"))} - " +
                                    "${(scheduledAt.plus(duration)).format(DateTimeFormatter.ofPattern("hh:mm a"))}"
                        }
                    )

                    CustomEditableInfoItem(
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
                        value = "${scheduledAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd, EEEE"))}"
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
                                                task.distance = distance!!
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportTaskFields(
    task: SportTask,
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
        value = activity?.title ?: ""
    )

    if (activity!!.supportsDistanceMetrics) {
        val statePickerDistance = rememberPickerState(0.0)

        EditableInfoItem(
            dialogContent = {
                LabelFieldWrapper(
                    content = {
                        Picker(
                            items = RANGE_KILOMETERS,
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
            onConfirm = { distance = statePickerDistance.value },
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
fun MealTaskFields(
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
fun CustomEditableInfoItem(
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

@Composable
fun TaskScreen(
    task: Task
) {
    // TODO: Implement TaskScreen (opened task details)
}
