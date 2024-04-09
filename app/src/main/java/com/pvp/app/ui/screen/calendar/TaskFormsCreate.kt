package com.pvp.app.ui.screen.calendar

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.common.InputValidator
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.common.DateTimePicker
import com.pvp.app.ui.common.LabelFieldWrapper
import com.pvp.app.ui.common.PickerPair
import com.pvp.app.ui.common.PickerState.Companion.rememberPickerState
import com.pvp.app.ui.common.TextField
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
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

@SuppressLint("UnrememberedMutableState")
@Composable
fun TaskCreate(
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
            label = { Text("Title") }
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
            label = { Text("Description") }
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
                    date = selectedDateTime.toLocalDate(),
                    description = description,
                    duration = Duration.ofMinutes(duration.toLong()),
                    time = selectedDateTime.toLocalTime(),
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

            val modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp)
                .verticalScroll(rememberScrollState())

            when (target) {
                MealTask::class -> TaskCreateMeal(
                    date = date,
                    modifier = modifier,
                    onCreate = ::closeIfShould
                )

                SportTask::class -> TaskCreateSport(
                    date = date,
                    modifier = modifier,
                    onCreate = ::closeIfShould
                )

                Task::class -> TaskCreate(
                    date = date,
                    modifier = modifier,
                    onCreate = ::closeIfShould
                )
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun TaskCreateMeal(
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
            label = { Text("Meal Title") }
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
            label = { Text("Description") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = ingredients,
            onValueChange = { newText, _ -> ingredients = newText },
            label = { Text("Ingredients") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = preparation,
            onValueChange = { newText, _ -> preparation = newText },
            label = { Text("Preparation") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = "Duration (minutes): $duration"
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
            onDateTimeChanged = { newDateTime -> selectedDateTime = newDateTime }
        )

        Button(
            onClick = {
                model.create(
                    date = selectedDateTime.toLocalDate(),
                    description = description,
                    duration = Duration.ofMinutes(duration.toLong()),
                    ingredients = ingredients,
                    preparation = preparation,
                    time = selectedDateTime
                        .toLocalTime()
                        .let { if (it == LocalTime.MIN) null else it },
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
                text = "Submit"
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreateSport(
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
    val stateKilometers = rememberPickerState(model.rangeKilometers.first())
    val stateMeters = rememberPickerState(0)

    val distance by remember {
        derivedStateOf {
            stateKilometers.value + (stateMeters.value / 1000.0)
        }
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
            onValueChange = { newText, _ -> description = newText },
            label = { Text("Description") },
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (activity.supportsDistanceMetrics) {
            LabelFieldWrapper(
                content = {
                    PickerPair(
                        itemsFirst = model.rangeKilometers,
                        itemsSecond = (0..900 step 100).toList(),
                        labelFirst = { "$it (km)" },
                        labelSecond = { "$it (m)" },
                        onChange = { stateFirst, stateSecond ->
                            stateKilometers.value = stateFirst
                            stateMeters.value = stateSecond
                        },
                        stateFirst = stateKilometers,
                        stateSecond = stateMeters
                    )
                },
                putBelow = true,
                text = "$distance (km) distance",
                textAlign = TextAlign.End
            )
        } else {
            Text(
                text = "Duration: $duration minutes",
                style = TextStyle(fontSize = 15.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )

            Slider(
                value = duration.toFloat(),
                onValueChange = { newValue -> duration = newValue.toInt() },
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
                    date = selectedDateTime.toLocalDate(),
                    activity = activity,
                    description = description,
                    distance = distance,
                    duration = Duration.ofMinutes(duration.toLong()),
                    time = selectedDateTime
                        .toLocalTime()
                        .let { if (it == LocalTime.MIN) null else it },
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