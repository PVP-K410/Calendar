package com.pvp.app.ui.screen.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.model.SportActivity
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.common.EditableInfoItem
import com.pvp.app.ui.common.LabelFieldWrapper
import com.pvp.app.ui.common.Picker
import com.pvp.app.ui.common.PickerPair
import com.pvp.app.ui.common.PickerState
import java.time.LocalDate

private val goalActivities: List<SportActivity> = listOf(
    SportActivity.Cycling,
    SportActivity.Hiking,
    SportActivity.Rowing,
    SportActivity.Running,
    SportActivity.Skiing,
    SportActivity.Snowboarding,
    SportActivity.Swimming,
    SportActivity.Walking
)

@Composable
fun DistancePicker(
    model: GoalViewModel = hiltViewModel(),
    distance: Double,
    onDistanceChange: (Double) -> Unit
) {
    val stateKilometers = PickerState.rememberPickerState(
        distance.toInt()
    )

    val stateMeters = PickerState.rememberPickerState(
        distance.let { ((it - stateKilometers.value) * 900).toInt() }
    )

    EditableInfoItem(
        dialogContent = {
            LabelFieldWrapper(
                content = {
                    PickerPair(
                        itemsFirst = model.rangeKilometers,
                        itemsSecond = (0..1000 step 100).toList(),
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
                text = "${stateKilometers.value + (stateMeters.value / 1000.0)} (km) distance",
                textAlign = TextAlign.End
            )
        },
        dialogTitle = { Text("Editing distance") },
        label = "Distance",
        onConfirm = { onDistanceChange(stateKilometers.value + (stateMeters.value / 1000.0)) },
        onDismiss = { },
        value = "${stateKilometers.value + (stateMeters.value / 1000.0)} (km)"
    )
}

@Composable
fun GoalCreateDialog(
    onClose: () -> Unit,
    isOpen: Boolean,
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
            GoalCreateForm(onCreate = onClose)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalCreateForm(
    model: GoalViewModel = hiltViewModel(),
    onCreate: () -> Unit
) {
    var activity by remember { mutableStateOf(SportActivity.Walking) }
    var tempActivity by remember { mutableStateOf(activity) }
    var goal by remember { mutableDoubleStateOf(0.0) }
    val state by model.state.collectAsStateWithLifecycle()
    var steps by remember { mutableStateOf(activity == SportActivity.Walking) }
    var stepCount by remember { mutableDoubleStateOf(0.0) }
    var monthly by remember { mutableStateOf(state.monthly) }

    val isFormValid by remember(goal) {
        derivedStateOf {
            goal > 0 || (stepCount > 0 && steps)
        }
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            var value by remember {
                mutableStateOf(
                    if (monthly) {
                        GoalFilter.Monthly
                    } else {
                        GoalFilter.Weekly
                    }
                )
            }

            GoalTypeFilter(filter = value) {
                monthly = it == GoalFilter.Monthly
                value = it
            }

            Spacer(modifier = Modifier.padding(top = 4.dp))

            val now = LocalDate.now()

            val (start, end) = when (monthly) {
                true -> {
                    Pair(
                        now,
                        now.plusMonths(1)
                    )
                }

                false -> {
                    Pair(
                        now,
                        now.plusDays(7)
                    )
                }
            }

            Text(
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "$start - $end"
            )

            Spacer(modifier = Modifier.padding(top = 8.dp))

            EditableInfoItem(
                dialogContent = {
                    var isExpanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = isExpanded,
                        onExpandedChange = { isExpanded = it },
                    ) {
                        TextField(
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            value = tempActivity.title,
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
                            goalActivities.forEach {
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
                onConfirm = {
                    activity = tempActivity
                    steps = activity == SportActivity.Walking && goal == 0.0
                },
                onDismiss = { tempActivity = activity },
                value = activity?.title ?: ""
            )

            Spacer(modifier = Modifier.padding(top = 8.dp))

            if (activity == SportActivity.Walking) {
                StepSelector(
                    isSelected = steps
                ) {
                    steps = !steps
                }

                Spacer(modifier = Modifier.padding(top = 8.dp))
            }

            when (steps) {
                true -> {
                    StepPicker(steps = stepCount) {
                        stepCount = it
                        goal = 0.0
                    }
                }

                false -> {
                    DistancePicker(distance = goal) {
                        goal = it
                        stepCount = 0.0
                        steps = false
                    }
                }
            }

            Spacer(modifier = Modifier.padding(top = 8.dp))

            Button(
                onClick = {
                    model.create(
                        activity = activity,
                        goal = if (steps) stepCount else goal,
                        monthly = monthly,
                        steps = steps,
                    )

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

@Composable
fun StepPicker(
    steps: Double,
    onStepChange: (Double) -> Unit
) {
    var stepCount by remember { mutableDoubleStateOf(steps) }

    EditableInfoItem(
        dialogContent = {
            LabelFieldWrapper(
                content = {
                    Picker(
                        items = (14000..140000 step 1000).toList(),
                        label = { "$it" },
                        onChange = { state ->
                            stepCount = state.toDouble()
                        },
                        state = PickerState.rememberPickerState(
                            steps.toInt()
                        )
                    )
                },
                putBelow = true,
                text = "${steps.toInt()} steps",
                textAlign = TextAlign.End
            )
        },
        dialogTitle = { Text("Editing steps") },
        label = "Steps",
        onConfirm = { onStepChange(stepCount) },
        onDismiss = { },
        value = "${steps.toInt()} steps"
    )
}

@Composable
private fun StepSelector(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                MaterialTheme.shapes.medium
            )
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .height(40.dp)
                .clickable { onClick() }
                .background(
                    if (isSelected) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        Color.Transparent
                    },
                    MaterialTheme.shapes.medium
                )
        ) {
            Text(text = "Steps")
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .height(40.dp)
                .clickable { onClick() }
                .background(
                    if (!isSelected) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        Color.Transparent
                    },
                    MaterialTheme.shapes.medium
                )
        ) {
            Text(text = "Distance")
        }
    }
}