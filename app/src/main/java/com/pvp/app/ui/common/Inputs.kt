@file:OptIn(ExperimentalMaterial3Api::class)

package com.pvp.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pvp.app.R
import com.pvp.app.model.SportActivity
import com.pvp.app.ui.common.PickerState.Companion.rememberPickerState
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun <T> DropdownMenu(
    onSelect: (T) -> Unit,
    optionToLabel: @Composable (T) -> String = { it.toString() },
    options: List<T>,
    value: T
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        androidx.compose.material3.TextField(
            modifier = Modifier.menuAnchor(),
            onValueChange = { },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            value = optionToLabel(value)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { o ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false

                        onSelect(o)
                    },
                    text = { Text(text = optionToLabel(o)) }
                )
            }
        }
    }
}

@Composable
fun EditableInfoItem(
    dialogContent: @Composable () -> Unit,
    dialogTitle: @Composable () -> Unit,
    label: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    value: String
) {
    EditableInfoItem(
        dialogContent = dialogContent,
        dialogTitle = dialogTitle,
        label = {
            Text(
                fontWeight = FontWeight.Bold,
                text = label
            )
        },
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        value = { Text(value) }
    )
}

@Composable
fun EditableInfoItem(
    confirmButtonEnabled: Boolean = true,
    dialogContent: @Composable () -> Unit,
    dialogTitle: @Composable () -> Unit,
    label: @Composable ColumnScope.() -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    value: @Composable ColumnScope.() -> Unit
) {
    val localeSave = stringResource(R.string.action_save)

    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.medium
            )
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            label()

            value()
        }

        IconButtonConfirm(
            confirmationButtonContent = {
                Text(
                    localeSave,
                    fontWeight = FontWeight.Bold
                )
            },
            confirmationButtonEnabled = confirmButtonEnabled,
            confirmationDescription = dialogContent,
            confirmationTitle = dialogTitle,
            icon = Icons.Outlined.Edit,
            iconDescription = "Edit info item icon button",
            iconSize = 30.dp,
            modifier = Modifier.align(Alignment.TopEnd),
            onConfirm = onConfirm,
            onDismiss = onDismiss
        )
    }
}

@Composable
fun EditableDateItem(
    label: String,
    value: LocalDate,
    onValueChange: (LocalDate) -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.medium
            )
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                fontWeight = FontWeight.Bold,
                text = label
            )

            Text(value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd, EEEE")))
        }

        IconButtonWithDatePickerDialog(
            modifier = Modifier.align(Alignment.TopEnd),
            icon = Icons.Outlined.Edit,
            iconDescription = "Edit info item icon button",
            iconSize = 30.dp,
            onDateSelected = { onValueChange(it.toLocalDate()) }
        )
    }
}

@Composable
fun EditableTextItem(
    editLabel: String,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    validate: (String) -> Boolean = { true },
    errorMessage: String = "Invalid input"
) {
    var editingText by remember(value) { mutableStateOf(value) }

    Column {
        EditableInfoItem(
            confirmButtonEnabled = validate(editingText),
            dialogContent = {
                Column {
                    OutlinedTextField(
                        label = { Text(label) },
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = { editingText = it },
                        value = editingText
                    )

                    TextError(
                        enabled = !validate(editingText),
                        text = errorMessage
                    )
                }
            },
            dialogTitle = { Text(editLabel) },
            label = {
                Text(
                    fontWeight = FontWeight.Bold,
                    text = label
                )
            },
            onConfirm = {
                if (validate(editingText)) {
                    onValueChange(editingText)
                }
            },
            onDismiss = {
                editingText = value
            },
            value = {
                if (value.isNotEmpty()) {
                    Text(value)
                }
            }
        )

        TextError(
            enabled = !validate(value),
            text = errorMessage
        )
    }
}

@Composable
fun EditablePickerItem(
    editLabel: String,
    label: String,
    value: Duration?,
    valueLabel: String,
    items: List<Int>,
    itemsLabels: String,
    onValueChange: (Duration) -> Unit,
) {
    var editingDuration by remember(value) { mutableStateOf(value ?: Duration.ZERO) }

    EditableInfoItem(
        dialogContent = {
            Column {
                val initial = remember(editingDuration) {
                    editingDuration
                        ?.toMinutes()
                        ?.toFloat()
                        ?: 0f
                }

                Picker(
                    label = { "$it $itemsLabels" },
                    items = items,
                    state = rememberPickerState(initialValue = initial),
                    startIndex = initial.toInt() / 5,
                    onChange = { editingDuration = Duration.ofMinutes(it.toLong()) }
                )
            }
        },
        dialogTitle = { Text(editLabel) },
        label = {
            Text(
                fontWeight = FontWeight.Bold,
                text = label
            )
        },
        onConfirm = {
            onValueChange(editingDuration)
        },
        onDismiss = { },
        value = {
            if (value != null) {
                Text("${value.toMinutes()} $valueLabel")
            }
        }
    )
}

@Composable
fun EditablePickerItem(
    editLabel: String,
    label: String,
    value: Int,
    valueLabel: String,
    items: List<Int>,
    itemsLabels: String,
    onValueChange: (Int) -> Unit,
) {
    var editingValue by remember(value) { mutableIntStateOf(value) }

    EditableInfoItem(
        dialogContent = {
            Column {
                Picker(
                    label = { "$it $itemsLabels" },
                    items = items,
                    state = rememberPickerState(initialValue = editingValue),
                    startIndex = editingValue - items.first(),
                    onChange = { editingValue = it }
                )
            }
        },
        dialogTitle = { Text(editLabel) },
        label = {
            Text(
                fontWeight = FontWeight.Bold,
                text = label
            )
        },
        onConfirm = {
            onValueChange(editingValue)
        },
        onDismiss = { },
        value = { Text("$value $valueLabel") }
    )
}

@Composable
fun EditableTimeItem(
    editLabel: String,
    label: String,
    value: LocalTime,
    valueDisplay: String? = null,
    onValueChange: (LocalTime) -> Unit,
) {
    val editingHour = rememberPickerState(value.hour)
    val editingMinute = rememberPickerState(value.minute)

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
        dialogTitle = { Text(editLabel) },
        label = {
            Text(
                fontWeight = FontWeight.Bold,
                text = label
            )
        },
        onConfirm = {
            onValueChange(
                LocalTime.of(
                    editingHour.value,
                    editingMinute.value
                )
            )
        },
        onDismiss = { },
        value = {
            if (valueDisplay != null) {
                Text(valueDisplay)
            }
        }
    )
}

@Composable
fun EditableDistanceItem(
    editLabel: String,
    label: String,
    value: Double?,
    rangeKilometers: List<Int>,
    rangeMeters: List<Int>,
    onValueChange: (Double) -> Unit,
) {
    val localeDistance = stringResource(R.string.input_field_distance_value)
    val localeMeasurementKilometers = stringResource(R.string.measurement_km)
    val localeMeasurementMeters = stringResource(R.string.measurement_m)
    val localeTotalDistance = stringResource(R.string.input_field_distance_total)

    val stateKilometers = rememberPickerState(
        value
            ?.toInt()
            ?: rangeKilometers.first()
    )

    val stateMeters = rememberPickerState(
        value
            ?.let { ((it - stateKilometers.value) * 1000).toInt() }
            ?: 0
    )

    EditableInfoItem(
        dialogContent = {
            LabelFieldWrapper(
                content = {
                    PickerPair(
                        itemsFirst = rangeKilometers,
                        itemsSecond = rangeMeters,
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
        dialogTitle = { Text(editLabel) },
        label = {
            Text(
                fontWeight = FontWeight.Bold,
                text = label
            )
        },
        onConfirm = {
            onValueChange(
                stateKilometers.value + (stateMeters.value / 1000.0)
            )
        },
        onDismiss = { },
        value = {
            if (value != null) {
                Text(localeDistance.format(value))
            }
        }
    )
}

@Composable
fun EditableSportActivityItem(
    editLabel: String,
    label: String,
    activities: List<SportActivity> = SportActivity.entries,
    value: SportActivity,
    onValueChange: (SportActivity) -> Unit,
) {
    val localeTooltip = stringResource(R.string.activity_tooltip_autocomplete)
    var editingActivity by remember(value) { mutableStateOf(value) }

    EditableInfoItem(
        dialogContent = {
            DropdownMenu(
                onSelect = { editingActivity = it },
                optionToLabel = { it.title() },
                options = activities,
                value = editingActivity
            )
        },
        dialogTitle = { Text(editLabel) },
        label = {
            Text(
                fontWeight = FontWeight.Bold,
                text = label
            )
        },
        onConfirm = {
            onValueChange(editingActivity)
        },
        onDismiss = { },
        value = {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(value.title())

                if (value.supportsDistanceMetrics) {
                    InfoTooltip(tooltipText = localeTooltip)
                }
            }
        }
    )
}