package com.pvp.app.ui.screen.task

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.common.InputValidator
import com.pvp.app.common.getDurationString
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.common.DateTimePicker
import com.pvp.app.ui.common.TextField
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("UnrememberedMutableState")
@Composable
fun CreateTaskMealForm(
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
    var selectedDateTime by remember { mutableStateOf(LocalDateTime.now()) }

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
            validationPolicies = { input -> InputValidator.validateBlank(input, "Title") },
            label = { Text("Meal Title") },
        )

        Spacer(modifier = Modifier.height((16.dp)))

        TextField(
            value = description,
            onValueChange = { newText, errors ->
                description = newText
                descriptionError = errors.isNotEmpty()
            },
            validationPolicies = { input -> InputValidator.validateBlank(input, "Description") },
            label = { Text("Description") },
        )

        Spacer(modifier = Modifier.height((16.dp)))

        TextField(
            value = ingredients,
            onValueChange = { newText, _ ->
                ingredients = newText
            },
            label = { Text("Ingredients") },
        )

        Spacer(modifier = Modifier.height((16.dp)))

        TextField(
            value = preparation,
            onValueChange = { newText, _ ->
                preparation = newText
            },
            label = { Text("Preparation") },
        )

        Spacer(modifier = Modifier.height((16.dp)))

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

        Spacer(modifier = Modifier.height((16.dp)))

        DateTimePicker(
            dateTime = selectedDateTime,
            onDateTimeChanged = { newDateTime ->
                selectedDateTime = newDateTime
            }
        )

        Button(
            onClick = {
                val recipe = if (
                    ingredients.isNotEmpty() &&
                    preparation.isNotEmpty()
                ) {
                    "$ingredients\n$preparation"
                } else if (ingredients.isNotEmpty() && preparation.isEmpty()) {
                    ingredients
                } else {
                    preparation
                }

                model.createTaskMeal(
                    description = description,
                    duration = Duration.ofMinutes(duration.toLong()),
                    recipe = recipe,
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
    model: TaskViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onCreate: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(true) }
    var description by remember { mutableStateOf("") }
    var descriptionError by remember { mutableStateOf(true) }
    var activity by remember { mutableStateOf(SportActivity.Walking) }
    var distance by remember { mutableStateOf("") }
    var distanceError by remember { mutableStateOf(true) }
    var duration by remember { mutableIntStateOf(0) }
    var durationError by remember { mutableStateOf(true) }
    var selectedDateTime by remember { mutableStateOf(LocalDateTime.now()) }
    var isExpanded by remember { mutableStateOf(false) }

    val isFormValid by derivedStateOf {
        !titleError && !descriptionError && (!durationError || !distanceError)
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
            validationPolicies = { input -> InputValidator.validateBlank(input, "Title") },
            label = { Text("Title") },
        )

        Spacer(modifier = Modifier.height((16.dp)))

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

        Spacer(modifier = Modifier.height((16.dp)))

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

        Spacer(modifier = Modifier.height((16.dp)))

        if (activity.supportsDistanceMetrics) {
            duration = 0

            TextField(
                value = distance,
                onValueChange = { newText, errors ->
                    distance = newText
                    distanceError = errors.isNotEmpty()
                },
                validationPolicies = { input ->
                    InputValidator.validateBlank(input, "Distance") +
                            InputValidator.validateFloat(input, "Distance")
                },
                label = { Text("Distance (m)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .width(200.dp)
            )
        } else {
            distance = ""

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
                    durationError = false
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
                val distanceValue = if (distance != "") {
                    distance.toDouble()
                } else {
                    0.0f.toDouble()
                }

                model.createTaskSport(
                    activity = activity,
                    description = description,
                    distance = distanceValue,
                    duration = Duration.ofMinutes(duration.toLong()),
                    isCompleted = false,
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

@SuppressLint("UnrememberedMutableState")
@Composable
fun CreateTaskGeneralForm(
    model: TaskViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onCreate: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(true) }
    var description by remember { mutableStateOf("") }
    var descriptionError by remember { mutableStateOf(true) }
    var duration by remember { mutableFloatStateOf(0.0f) }
    var selectedDateTime by remember { mutableStateOf(LocalDateTime.now()) }

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
            validationPolicies = { input -> InputValidator.validateBlank(input, "Title") },
            label = { Text("Title") },
        )

        Spacer(modifier = Modifier.height((16.dp)))

        TextField(
            value = description,
            onValueChange = { newText, errors ->
                description = newText
                descriptionError = errors.isNotEmpty()
            },
            validationPolicies = { input -> InputValidator.validateBlank(input, "Description") },
            label = { Text("Description") },
        )

        Spacer(modifier = Modifier.height((16.dp)))

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
                model.createTask(
                    description = description,
                    duration = Duration.ofMinutes(duration.toLong()),
                    isCompleted = false,
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
                text = getDurationString(duration),
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
private fun SportTaskBoxBody(
    task: SportTask
) {
    if (task.distance != null && task.distance!! > 0) {
        Row(
            modifier = Modifier.padding(6.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Straighten,
                contentDescription = "Distance"
            )

            Text(
                text = "${task.distance!! / 1000} km",
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            )
        }
    } else if (task.duration != null) {
        Row(
            modifier = Modifier.padding(6.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Timelapse,
                contentDescription = "Duration"
            )

            Text(
                text = getDurationString(task.duration!!),
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            )
        }
    }

    task.activity?.let { activity ->
        Text(
            text = activity.title,
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
                text = getDurationString(duration),
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
    var checked by remember {
        mutableStateOf(task.isCompleted)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)

    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = {
                        checked = it
                        task.isCompleted = checked
                        model.updateTask(task)
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
fun TaskScreen(
    task: Task
) {
    // TODO: Implement TaskScreen (opened task details)
}
