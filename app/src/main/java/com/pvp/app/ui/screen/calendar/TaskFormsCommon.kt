package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.BasicTooltipBox
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberBasicTooltipState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TooltipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import com.pvp.app.ui.common.EditableInfoItem
import com.pvp.app.ui.common.LabelFieldWrapper
import com.pvp.app.ui.common.PickerPair
import com.pvp.app.ui.common.PickerState
import kotlinx.coroutines.launch
import java.time.Duration

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TaskEditFieldsSport(
    task: SportTask? = null,
    model: TaskViewModel = hiltViewModel(),
    onActivityChange: (SportActivity?) -> Unit,
    onDistanceChange: (Double) -> Unit,
    onDurationChange: (Duration) -> Unit
) {
    var activity by remember { mutableStateOf(task?.activity) }
    var distance by remember { mutableDoubleStateOf(task?.distance ?: 0.0) }
    var duration by remember { mutableStateOf(task?.duration ?: Duration.ZERO) }
    var tempActivity by remember { mutableStateOf(activity) }
    var tempDuration by remember { mutableStateOf(duration) }

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
        value = {
            Row (
                Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tooltipState = rememberBasicTooltipState()
                val scope = rememberCoroutineScope()

                Text(activity?.title ?: "")

                if (activity?.supportsDistanceMetrics == true) {
                    BasicTooltipBox(
                        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                        tooltip = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "This task is likely to be autocompleted",
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                        .size(
                                            height = 40.dp,
                                            width = 300.dp
                                        )
                                        .border(
                                            border = BorderStroke(
                                                1.dp,
                                                MaterialTheme.colorScheme.outline
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .wrapContentSize(Alignment.Center),
                                    color = Color.White
                                )
                            }
                        },
                        state = tooltipState
                    ) {
                        IconButton(
                            onClick = { scope.launch { tooltipState.show() } },
                            modifier = Modifier.padding(0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        }
    )

    if (activity != null && activity!!.supportsDistanceMetrics) {
        val stateKilometers = PickerState.rememberPickerState(
            task?.distance
                ?.toInt()
                ?: model.rangeKilometers.first()
        )

        val stateMeters = PickerState.rememberPickerState(
            task?.distance
                ?.let { ((it - stateKilometers.value) * 900).toInt() }
                ?: 0
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
            onConfirm = { distance = stateKilometers.value.toDouble() },
            onDismiss = { distance = task?.distance ?: 0.0 },
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

    onDistanceChange(distance)

    duration?.let { onDurationChange(it) }
}
