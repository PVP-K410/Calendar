package com.pvp.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pvp.app.model.SportActivity
import com.pvp.app.ui.common.PickerState.Companion.rememberPickerState
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * @param keyboardOptions (optional) software keyboard options that contains configuration such
 * as KeyboardType and ImeAction.
 * @param label label displayed inside text container
 * @param modifier (optional) the Modifier applies to the text field
 * @param onValueChange (optional) callback triggered when text inside text field is updated
 * @param validationPolicies (optional) how text inside should be validated, should be specified
 * via InputValidator
 * @param value text field input text
 */
@Composable
fun TextField(
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    label: String,
    modifier: Modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .fillMaxWidth(),
    onValueChange: (String, List<String>) -> Unit = { _, _ -> },
    validationPolicies: (String) -> List<String> = { listOf() },
    value: String
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        validationPolicies = validationPolicies,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium
            )
        },
        keyboardOptions = keyboardOptions,
        modifier = modifier
    )
}

/**
 * @param keyboardOptions (optional) software keyboard options that contains configuration such
 * as KeyboardType and ImeAction.
 * @param label label displayed inside text container
 * @param modifier (optional) the Modifier applies to the text field
 * @param onValueChange (optional) callback triggered when text inside text field is updated
 * @param validationPolicies (optional) how text inside should be validated, should be specified
 * via InputValidator
 * @param value text field input text
 */
@Composable
fun TextField(
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .fillMaxWidth(),
    onValueChange: (String, List<String>) -> Unit = { _, _ -> },
    validationPolicies: (String) -> List<String> = { listOf() },
    value: String
) {
    var errors by remember { mutableStateOf(emptyList<String>()) }
    var input by remember { mutableStateOf(value) }

    ErrorFieldWrapper(
        content = {
            androidx.compose.material3.TextField(
                value = input,
                onValueChange = {
                    input = it
                    errors = validationPolicies(it)
                    onValueChange(
                        it,
                        errors
                    )
                },
                label = label,
                modifier = modifier,
                trailingIcon = {
                    if (errors.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Outlined.Error,
                            contentDescription = "Error"
                        )
                    }
                },
                keyboardOptions = keyboardOptions,
            )
        },
        messages = errors
    )
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
    dialogContent: @Composable () -> Unit,
    dialogTitle: @Composable () -> Unit,
    label: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    value: @Composable ColumnScope.() -> Unit
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
        value = value
    )
}

@Composable
fun EditableInfoItem(
    dialogContent: @Composable () -> Unit,
    dialogTitle: @Composable () -> Unit,
    label: @Composable ColumnScope.() -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    value: String
) {
    EditableInfoItem(
        dialogContent = dialogContent,
        dialogTitle = dialogTitle,
        label = label,
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
                    "Save",
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
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    validate: (String) -> Boolean = { true },
    errorMessage: String = "Invalid input"
) {
    var editingText by remember(value) { mutableStateOf(value) }

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

                if (!validate(editingText)) {
                    Spacer(modifier = Modifier.height(8.dp))

                    if (editingText.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        },
        dialogTitle = { Text("Editing $label") },
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
}

@Composable
fun EditablePickerItem(
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
        dialogTitle = { Text("Editing $label") },
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
        dialogTitle = { Text("Editing $label") },
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
        dialogTitle = { Text("Editing $label") },
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
    label: String,
    value: Double?,
    rangeKilometers: List<Int>,
    rangeMeters: List<Int>,
    onValueChange: (Double) -> Unit,
) {
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
        dialogTitle = { Text("Editing $label") },
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
                Text("$value (km)")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableSportActivityItem(
    label: String,
    value: SportActivity,
    onValueChange: (SportActivity) -> Unit,
) {
    var editingActivity by remember(value) { mutableStateOf(value) }

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
                    value = editingActivity.title,
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
                                editingActivity = it
                                isExpanded = false
                            }
                        )
                    }
                }
            }
        },
        dialogTitle = { Text("Editing $label") },
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
                Text(value.title)

                if (value.supportsDistanceMetrics) {
                    InfoTooltip(tooltipText = "This task is likely to be autocompleted")
                }
            }
        }
    )
}