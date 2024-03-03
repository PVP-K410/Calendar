package com.pvp.app.ui.screen.task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    showPicker: Boolean,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDateTime) -> Unit,
) {
    val stateDate = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

    if (showPicker) {
        androidx.compose.material3.DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        onDismiss()

                        val instant = stateDate.selectedDateMillis?.let {
                            Instant.ofEpochMilli(it)
                        }

                        instant?.let {
                            onDateSelected(LocalDateTime.ofInstant(it, ZoneId.systemDefault()))
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("CANCEL")
                }
            }
        ) {
            androidx.compose.material3.DatePicker(state = stateDate)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    showPicker: Boolean,
    onDismiss: () -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
    initialHour: Int,
    initialMinute: Int
) {
    val stateTime = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    if (showPicker) {
        TimePicker(
            onCancel = onDismiss,
            onConfirm = {
                onDismiss()
                onTimeSelected(stateTime.hour, stateTime.minute)
            },
        ) {
            TimeInput(state = stateTime)
        }
    }
}

@Composable
fun TimePicker(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )

                content()

                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()

                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(
                        onClick = onCancel
                    ) {
                        Text("Cancel")
                    }

                    TextButton(
                        onClick = onConfirm
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Composable
fun ScrollableTimePicker(
    selectedDateTime: LocalDateTime,
    onDateTimeChanged: (LocalDateTime) -> Unit
) {
    var dateTime by remember { mutableStateOf(selectedDateTime) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f)) {
            TimePickerColumn(
                range = 0..23,
                selectedValue = selectedDateTime.hour,
                onValueChange = { hour ->
                    dateTime = dateTime.withHour(hour)
                    onDateTimeChanged(dateTime)
                }
            )
        }

        Text(":", style = MaterialTheme.typography.headlineMedium)

        Box(modifier = Modifier.weight(1f)) {
            TimePickerColumn(
                range = 0..59,
                selectedValue = selectedDateTime.minute,
                onValueChange = { minute ->
                    dateTime = dateTime.withMinute(minute)
                    onDateTimeChanged(dateTime)
                }
            )
        }
    }
}

@Composable
fun TimePickerColumn(
    range: IntRange,
    selectedValue: Int,
    onValueChange: (Int) -> Unit
) {
    val visibleItemCount = 3
    val itemCount = range.count()
    val extendedRange = (range.first - itemCount)..(range.last + itemCount)
    val initialIndex = itemCount + selectedValue - (visibleItemCount / 2)

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    LaunchedEffect(key1 = selectedValue) {
        val scrollToIndex = itemCount + selectedValue - (visibleItemCount / 2)
        listState.scrollToItem(scrollToIndex)
    }

    LaunchedEffect(key1 = listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { firstVisibleItemIndex ->
                val correctedIndex = (firstVisibleItemIndex % itemCount) + range.first
                correctedIndex + (visibleItemCount / 2)
            }
            .distinctUntilChanged()
            .collect { centeredValue ->
                if (centeredValue in range) {
                    onValueChange(centeredValue)
                } else if (centeredValue == range.last + 1) {
                    // After implementing infinite scroll, first value registers as last + 1.
                    // Might be better way to address this, but works ig ¯\_(ツ)_/¯
                    onValueChange(range.first)
                }
            }
    }

    Box(modifier = Modifier.height(50.dp * visibleItemCount)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(extendedRange.toList()) { value ->
                val displayValue = when {
                    value < range.first -> value + itemCount
                    value > range.last -> value - itemCount
                    else -> value
                }
                if (displayValue in range) {
                    TimePickerItem(
                        value = displayValue,
                        isSelected = displayValue == selectedValue
                    ) {}
                }
            }
        }
    }
}

@Composable
fun TimePickerItem(value: Int, isSelected: Boolean, onItemSelected: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
            .clickable { onItemSelected() }
    ) {
        Text(
            text = "%02d".format(value),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
            )
        )
    }
}

@Composable
fun ExpandableTimePicker(
    transitionState: MutableTransitionState<Boolean>,
    selectedDateTime: LocalDateTime,
    onDateTimeChanged: (LocalDateTime) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = selectedDateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
            style = TextStyle(
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.clickable {
                    transitionState.targetState = !transitionState.currentState
                }
        )

        AnimatedVisibility(
            visibleState = transitionState,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            ScrollableTimePicker(
                selectedDateTime = selectedDateTime,
                onDateTimeChanged = onDateTimeChanged
            )
        }
    }
}

@Composable
fun DateAndTimePicker(
    dateTime: LocalDateTime,
    onDateTimeChanged: (LocalDateTime) -> Unit
){
    var showPickerDate by remember { mutableStateOf(false) }
    val showPickerTime = remember { MutableTransitionState(false) }
    var selectedDateTime by remember { mutableStateOf(dateTime) }

    Text(
        text = "Task date",
        style = TextStyle(
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier
            .fillMaxWidth()
    )

    Spacer(modifier = Modifier.height((8.dp)))

    Text(
        text = selectedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
        style = TextStyle(
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showPickerDate = true },
        color = MaterialTheme.colorScheme.onSurface
    )

    ExpandableTimePicker(
        transitionState = showPickerTime,
        selectedDateTime = selectedDateTime,
        onDateTimeChanged = { newDateTime ->
            selectedDateTime = newDateTime
            onDateTimeChanged(selectedDateTime)
        }
    )

    DatePickerDialog(
        showPicker = showPickerDate,
        onDismiss = { showPickerDate = false },
        onDateSelected = { selectedDate ->
            selectedDateTime = selectedDate
                .withHour(selectedDateTime.hour)
                .withMinute(selectedDateTime.minute)
            onDateTimeChanged(selectedDateTime)
        },
    )
}