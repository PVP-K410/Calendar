package com.pvp.app.ui.screen.task

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
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
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import com.yourapp.ui.components.TextFieldWithErrors

@SuppressLint("UnrememberedMutableState")
@Composable
fun CreateMealTaskForm(
    model: TaskViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(true) }
    var description by remember { mutableStateOf("") }
    var descriptionError by remember { mutableStateOf(true) }
    var ingredients by remember { mutableStateOf("") }
    var preparation by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf(0) }
    var selectedDateTime by remember { mutableStateOf(LocalDateTime.now()) }

    val isFormValid by derivedStateOf {
        !titleError && !descriptionError && duration > 0
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        TextFieldWithErrors(
            value = title,
            onValueChange = { newText, errors ->
                title = newText
                titleError = errors.isNotEmpty()
            },
            validationPolicies = { input -> InputValidator.validateBlank(input, "Title") },
            label = { Text("Meal Title") },
        )

        Spacer(modifier = Modifier.height((16.dp)))

        TextFieldWithErrors(
            value = description,
            onValueChange = { newText, errors ->
                description = newText
                descriptionError = errors.isNotEmpty()
            },
            validationPolicies = { input -> InputValidator.validateBlank(input, "Description") },
            label = { Text("Description") },
        )

        Spacer(modifier = Modifier.height((16.dp)))

        TextFieldWithErrors(
            value = ingredients,
            onValueChange = { newText, _ ->
                ingredients = newText
            },
            label = { Text("Ingredients") },
        )

        Spacer(modifier = Modifier.height((16.dp)))

        TextFieldWithErrors(
            value = preparation,
            onValueChange = { newText, _ ->
                preparation = newText
            },
            label = { Text("Preparation") },
        )

        Spacer(modifier = Modifier.height((16.dp)))

        Text("Duration (minutes): $duration", modifier = Modifier.padding(vertical = 8.dp))

        Slider(
            value = duration.toFloat(),
            onValueChange = { duration = it.toInt() },
            valueRange = 1f..180f,
            steps = 179,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height((16.dp)))

        DateAndTimePicker(
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
                    title = title,
                    userEmail = "fake@email@gmail@com"
                )
            },
            enabled = isFormValid,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp)
        ) {
            Text(
                "Submit",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSportTaskForm(
    model: TaskViewModel = hiltViewModel(),
) {
    val sportActivities = listOf(
        SportActivity.Cycling,
        SportActivity.Gym,
        SportActivity.Running,
        SportActivity.Swimming,
        SportActivity.Walking,
        SportActivity.Yoga
    )

    var title by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(true) }
    var description by remember { mutableStateOf("") }
    var descriptionError by remember { mutableStateOf(true) }
    var activity by remember { mutableStateOf<SportActivity>(sportActivities[0]) }
    var distance by remember { mutableStateOf("") }
    var distanceError by remember { mutableStateOf(true) }
    var duration by remember { mutableStateOf(0) }
    var durationError by remember { mutableStateOf(true) }
    var selectedDateTime by remember { mutableStateOf(LocalDateTime.now()) }
    var isExpanded by remember { mutableStateOf(false) }

    val isFormValid by derivedStateOf {
        !titleError && !descriptionError && (!durationError || !distanceError)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TextFieldWithErrors(
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
                TextField(
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
                    sportActivities.forEach { activityItem ->
                        DropdownMenuItem(
                            text = { Text(text = activityItem.title) },
                            onClick = {
                                activity = activityItem
                                isExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height((16.dp)))

            TextFieldWithErrors(
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
                TextFieldWithErrors(
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

            DateAndTimePicker(
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
                        userEmail = "fake@email@gmail@com",
                        title = title
                    )
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
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun CreateGeneralTaskForm(
    model: TaskViewModel = hiltViewModel()
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
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextFieldWithErrors(
            value = title,
            onValueChange = { newText, errors ->
                title = newText
                titleError = errors.isNotEmpty()
            },
            validationPolicies = { input -> InputValidator.validateBlank(input, "Title") },
            label = { Text("Title") },
        )

        Spacer(modifier = Modifier.height((16.dp)))

        TextFieldWithErrors(
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

        DateAndTimePicker(
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
                    title = title,
                    userEmail = "fake@email@gmail@com"
                )
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
fun MealTaskBoxBody(
    task: MealTask
) {
    Text(
        "Main ingredient: " + task.recipe,
        textAlign = TextAlign.Left,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
    )

    if (task.duration != null) {
        Text(
            "Duration: " + getDurationString(task.duration!!),
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        )
    }
}

@Composable
fun SportTaskBoxBody(
    task: SportTask
) {
    task.activity?.let { activity ->
        val activityText = buildString {
            append(activity.title)

            when {
                task.distance != null ->
                    append(" for ${task.distance} km")

                task.duration != null ->
                    append(" for ${getDurationString(task.duration!!)}")
            }
        }

        Text(
            text = activityText,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        )
    }
}

@Composable
fun TaskBoxBody(
    task: Task
) {
    task.description?.let { description ->
        Text(
            text = description,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        )
    }

    task.duration?.let { duration ->
        Text(
            text = "Duration: ${getDurationString(duration)}",
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

    Card(
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline
                )
            )
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    task.title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(CenterVertically)
                        .weight(1f),
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            when (task) {
                is SportTask -> SportTaskBoxBody(task)
                is MealTask -> MealTaskBoxBody(task)
                else -> TaskBoxBody(task)
            }

            val timeString = "Scheduled at ${
                task.scheduledAt
                    .toLocalTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm"))
            }"

            Text(
                timeString,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun TaskScreen(
    task: Task
) {
    // Only for testing purposes, delete later
    // To add preview, comment out task: Task from constructor.
    val mealTask = MealTask(
        description = "Prepare dinner",
        duration = Duration.ofMinutes(48),
        id = "1",
        isCompleted = false,
        recipe = "Chicken breast",
        scheduledAt = LocalDateTime.now(),
        title = "Cook Dinner",
        userEmail = "example@example.com"
    )

    val sportTask = SportTask(
        activity = SportActivity.Running,
        description = "Run in the park",
        distance = 5.0,
        duration = Duration.ofMinutes(65),
        id = "2",
        isCompleted = false,
        scheduledAt = LocalDateTime.now(),
        title = "Morning Run",
        userEmail = "example@example.com"
    )

    val task = Task(
        description = "Complete project tasks",
        duration = Duration.ofHours(2),
        id = "3",
        isCompleted = false,
        scheduledAt = LocalDateTime.now(),
        title = "Project Tasks",
        userEmail = "example@example.com"
    )

    TaskBox(task)
}
