@file:OptIn(ExperimentalMaterial3Api::class)

package com.pvp.app.ui.screen.goals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.R
import com.pvp.app.model.SportActivity
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.common.EditableInfoItem
import com.pvp.app.ui.common.EditableSportActivityItem
import com.pvp.app.ui.common.LabelFieldWrapper
import com.pvp.app.ui.common.Picker
import com.pvp.app.ui.common.PickerPair
import com.pvp.app.ui.common.PickerState.Companion.rememberPickerState
import com.pvp.app.ui.common.TabSelector
import java.time.LocalDate
import kotlin.math.roundToInt

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
    val localeEditLabel = stringResource(R.string.input_field_kilometers_edit_label)
    val localeLabel = stringResource(R.string.input_field_kilometers_label)
    val localeDistance = stringResource(R.string.input_field_distance_value)
    val localeMeasurementKilometers = stringResource(R.string.measurement_km)
    val localeMeasurementMeters = stringResource(R.string.measurement_m)
    val localeTotalDistance = stringResource(R.string.input_field_distance_total)

    val stateKilometers = rememberPickerState(
        distance.toInt()
    )

    val stateMeters = rememberPickerState(
        distance.let { (((it - it.toInt()) * 1000) / 100).roundToInt() * 100 }
    )

    EditableInfoItem(
        dialogContent = {
            LabelFieldWrapper(
                content = {
                    PickerPair(
                        itemsFirst = model.rangeKilometers,
                        itemsSecond = model.rangeMeters,
                        labelFirst = { "$it $localeMeasurementKilometers" },
                        labelSecond = { "$it $localeMeasurementMeters" },
                        onChange = { stateFirst, stateSecond ->
                            stateKilometers.value = stateFirst
                            stateMeters.value = stateSecond
                        },
                        stateFirst = stateKilometers,
                        stateSecond = stateMeters
                    )
                },
                putBelow = true,
                text = localeTotalDistance.format(
                    stateKilometers.value + (stateMeters.value / 1000.0)
                ),
                textAlign = TextAlign.End
            )
        },
        dialogTitle = { Text(localeEditLabel) },
        label = localeLabel,
        onConfirm = { onDistanceChange(stateKilometers.value + (stateMeters.value / 1000.0)) },
        onDismiss = { },
        value = localeDistance.format(distance)
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
                .padding(8.dp)
        ) {
            GoalCreateForm(onCreate = onClose)
        }
    }
}

@Composable
fun GoalCreateForm(
    model: GoalViewModel = hiltViewModel(),
    onCreate: () -> Unit
) {
    var activity by remember { mutableStateOf(SportActivity.Walking) }
    var goal by remember { mutableDoubleStateOf(0.0) }
    val localeCreate = stringResource(R.string.action_create)
    val localeEditLabel = stringResource(R.string.input_field_activity_edit_label)
    val localeLabel = stringResource(R.string.input_field_activity_label)
    val state by model.state.collectAsStateWithLifecycle()
    var steps by remember { mutableStateOf(activity == SportActivity.Walking) }
    var stepCount by remember { mutableDoubleStateOf(0.0) }
    var monthly by remember { mutableStateOf(state.monthly) }
    var selectedDistanceType by remember { mutableStateOf(DistanceType.Steps) }

    val isFormValid by remember(goal) {
        derivedStateOf {
            goal > 0 || (stepCount > 0 && steps)
        }
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHigh
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

            GoalTypeFilter(
                filter = value,
                isForm = true
            ) {
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

            EditableSportActivityItem(
                activities = goalActivities,
                editLabel = localeEditLabel,
                label = localeLabel,
                value = activity,
            ) {
                activity = it
                steps = activity == SportActivity.Walking && goal == 0.0

                if (activity != SportActivity.Walking) {
                    selectedDistanceType = DistanceType.Kilometers
                }
            }

            Spacer(modifier = Modifier.padding(4.dp))

            if (activity == SportActivity.Walking) {
                DistanceSelector(
                    selectedDistanceType = selectedDistanceType
                ) { newDistanceType ->
                    if (selectedDistanceType != newDistanceType) {
                        selectedDistanceType = newDistanceType
                    }
                }

                Spacer(modifier = Modifier.padding(4.dp))
            }

            when (selectedDistanceType) {
                DistanceType.Steps -> {
                    StepPicker(steps = stepCount) {
                        stepCount = it
                        goal = 0.0
                    }
                }

                DistanceType.Kilometers -> {
                    DistancePicker(
                        distance = goal,
                        onDistanceChange = {
                            goal = it
                            stepCount = 0.0
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.padding(4.dp))

            Button(
                colors = ButtonDefaults.buttonColors(
                    contentColor = if (isFormValid) MaterialTheme.colorScheme.surface else Color.Gray
                ),
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
                modifier = Modifier.align(Alignment.CenterHorizontally),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text(localeCreate)
            }
        }
    }
}

@Composable
fun StepPicker(
    steps: Double,
    onStepChange: (Double) -> Unit
) {
    val localeEditLabel = stringResource(R.string.input_field_steps_edit_label)
    val localeLabel = stringResource(R.string.input_field_steps_label)
    val localeSteps = stringResource(R.string.measurement_steps).lowercase()
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
                        state = rememberPickerState(
                            steps.toInt()
                        )
                    )
                },
                putBelow = true,
                text = "${stepCount.toInt()} $localeSteps",
                textAlign = TextAlign.End
            )
        },
        dialogTitle = { Text(localeEditLabel) },
        label = localeLabel,
        onConfirm = { onStepChange(stepCount) },
        onDismiss = { },
        value = "${steps.toInt()} $localeSteps"
    )
}

@Composable
fun DistanceSelector(
    selectedDistanceType: DistanceType,
    onDistanceTypeChange: (DistanceType) -> Unit
) {
    TabSelector(
        onSelect = { onDistanceTypeChange(DistanceType.entries[it]) },
        tab = selectedDistanceType.ordinal,
        tabs = DistanceType.entries.map { it.displayName() },
        withShadow = false
    )
}

enum class DistanceType(val displayName: @Composable () -> String) {
    Steps({ stringResource(R.string.goals_label_steps) }),
    Kilometers({ stringResource(R.string.goals_label_kilometers) })
}