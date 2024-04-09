package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.common.DurationUtil.asString
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import java.time.format.DateTimeFormatter

@Composable
fun TaskCard(
    task: Task,
    model: TaskViewModel = hiltViewModel()
) {
    var checked = task.isCompleted
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        TaskEdit(
            task,
            onDismissRequest = { showDialog = false },
            true
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant
                ),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { showDialog = true }
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
                    onCheckedChange = {
                        model.update(
                            { task -> task.isCompleted = it },
                            task
                        )

                        checked = it
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
                        is SportTask -> TaskCardContentSport(task)
                        is MealTask -> TaskCardContentMeal(task)
                        else -> TaskCardContent(task)
                    }
                }

                Text(
                    text = task.time?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "--:--",
                    modifier = Modifier.weight(0.3f),
                    fontSize = 22.sp
                )
            }
        }
    }
}

@Composable
private fun TaskCardContentMeal(task: MealTask) {
    task.duration?.let { duration ->
        Row(
            modifier = Modifier.padding(6.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Timelapse,
                contentDescription = "Duration"
            )

            Text(
                text = duration.asString(),
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            )
        }
    }

    Text(
        "Recipe: " + task.recipe,
        textAlign = TextAlign.Left,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
    )
}

@Composable
private fun TaskCardContentSport(task: SportTask) {
    if (task.distance != null && task.distance!! > 0) {
        Row(
            modifier = Modifier.padding(6.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Straighten,
                contentDescription = "Distance"
            )

            Text(
                text = "${task.distance} km",
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
                text = task.duration!!.asString(),
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            )
        }
    }

    Text(
        text = task.activity.title,
        textAlign = TextAlign.Left,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
    )
}

@Composable
private fun TaskCardContent(task: Task) {
    task.duration?.let { duration ->
        Row(
            modifier = Modifier.padding(6.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Timelapse,
                contentDescription = "Duration"
            )

            Text(
                text = duration.asString(),
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