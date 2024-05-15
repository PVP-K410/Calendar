package com.pvp.app.ui.screen.calendar

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
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.common.DurationUtil.asString
import com.pvp.app.model.CustomMealTask
import com.pvp.app.model.GeneralTask
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import com.pvp.app.ui.common.InfoTooltip
import com.pvp.app.ui.common.darken
import com.pvp.app.ui.common.orInDarkTheme
import java.time.format.DateTimeFormatter

@Composable
fun TaskCard(
    task: Task,
    model: TaskViewModel = hiltViewModel()
) {
    var checked = task.isCompleted
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        TaskEditSheet(
            task,
            onClose = {
                showDialog = false
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 10.dp,
                end = 10.dp,
                bottom = 10.dp
            )
            .clip(MaterialTheme.shapes.medium)
            .background(
                MaterialTheme.colorScheme.surfaceContainerHigh.orInDarkTheme(
                    MaterialTheme.colorScheme.surfaceContainer.darken(0.2f)
                )
            )
            .clickable(enabled = !(task is SportTask && task.isDaily)) {
                showDialog = true
            }
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
                    colors = CheckboxDefaults.colors(checkmarkColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .size(36.dp)
                        .align(CenterVertically),
                    onCheckedChange = {
                        model.update(
                            { task ->
                                Task.copy(
                                    task,
                                    isCompleted = it
                                )
                            },
                            task
                        )

                        checked = it
                    }
                )

                Text(
                    fontSize = 20.sp,
                    modifier = Modifier
                        .align(CenterVertically)
                        .weight(1f),
                    text = task.title,
                    textAlign = TextAlign.Center
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Spacer(modifier = Modifier.height(4.dp))

                    when (task) {
                        is SportTask -> TaskCardContentSport(task)
                        is CustomMealTask -> TaskCardContentMeal(task)
                        else -> TaskCardContent(task)
                    }
                }

                task.time?.let {
                    Text(
                        fontSize = 20.sp,
                        modifier = Modifier.weight(0.3f),
                        text = it.format(DateTimeFormatter.ofPattern("HH:mm"))
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskCardContentMeal(task: CustomMealTask) {
    task.duration?.let { duration ->
        Row(modifier = Modifier.padding(6.dp)) {
            Icon(
                contentDescription = "Duration",
                imageVector = Icons.Outlined.Timelapse
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                text = duration.asString(),
                textAlign = TextAlign.Left
            )
        }
    }

    Text(
        maxLines = 2,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp),
        overflow = TextOverflow.Ellipsis,
        text = "Recipe: " + task.recipe,
        textAlign = TextAlign.Left
    )
}

@Composable
private fun TaskCardContentSport(task: SportTask) {
    if (task.distance != null && task.distance > 0) {
        Row(modifier = Modifier.padding(6.dp)) {
            Icon(
                contentDescription = "Distance",
                imageVector = Icons.Outlined.Straighten
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                text = "${task.distance} km",
                textAlign = TextAlign.Left
            )
        }
    } else if (task.duration != null) {
        Row(modifier = Modifier.padding(6.dp)) {
            Icon(
                contentDescription = "Duration",
                imageVector = Icons.Outlined.Timelapse
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                text = task.duration.asString(),
                textAlign = TextAlign.Left
            )
        }
    }

    Row(
        modifier = Modifier.padding(6.dp),
        verticalAlignment = CenterVertically
    ) {
        Icon(
            contentDescription = "Activity",
            imageVector = task.activity.icon
        )

        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = task.activity.title,
            textAlign = TextAlign.Left
        )

        if (task.activity.supportsDistanceMetrics) {
            InfoTooltip(tooltipText = "This task is likely to be autocompleted")
        }
    }
}

@Composable
private fun TaskCardContent(task: Task) {
    val description = remember(task) {
        when (task) {
            is SportTask -> {
                task.description
            }

            is GeneralTask -> {
                task.description
            }

            else -> {
                null
            }
        }
    }

    task.duration?.let { duration ->
        Row(modifier = Modifier.padding(6.dp)) {
            Icon(
                contentDescription = "Duration",
                imageVector = Icons.Outlined.Timelapse
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                text = duration.asString(),
                textAlign = TextAlign.Left
            )
        }
    }

    if (description != null) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
            text = description,
            textAlign = TextAlign.Left
        )
    }
}