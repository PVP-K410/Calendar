package com.pvp.app.ui.screen.calendar

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.model.CustomMealTask
import com.pvp.app.model.GeneralTask
import com.pvp.app.model.GoogleTask
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import java.time.LocalDate
import kotlin.reflect.KClass

@Composable
private fun AnalysesContainer(
    modifier: Modifier = Modifier,
    columnModifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        Column(
            modifier = columnModifier,
            verticalArrangement = verticalArrangement,
            content = content
        )
    }
}

@Composable
fun AnalysisOfDay(
    date: LocalDate,
    model: CalendarWeeklyViewModel = hiltViewModel(),
    tasks: List<Task>
) {
    val launcher = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) {}

    LaunchedEffect(Unit) {
        if (!model.permissionsGranted()) {
            launcher.launch(PERMISSIONS)
        }
    }

    Spacer(modifier = Modifier.padding(16.dp))

    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .height(250.dp)
            .clip(MaterialTheme.shapes.medium)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AnalysesContainer(
                columnModifier = Modifier.padding(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = if (date == LocalDate.now()) "Today's tasks" else "Day's tasks"
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(
                            top = 2.dp,
                            bottom = 12.dp
                        )
                )

                TasksOfDayCounterContainer(tasks)
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AnalysesContainer(
                    columnModifier = Modifier.fillMaxSize(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Steps of the day",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )

                    StepCounter(date = date)
                }

                AnalysesContainer(
                    columnModifier = Modifier.fillMaxSize(),
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    AnalysisRow { CalorieCounter(date = date) }

                    AnalysisRow { HeartRateCounterAverage(date = date) }

                    AnalysisRow { SleepDurationCounter(date = date) }
                }
            }
        }
    }
}

@Composable
private fun AnalysisRow(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
private fun TasksOfDayCounterContainer(tasks: List<Task>) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        TasksOfDayCounter(
            Icons.Outlined.Event,
            tasks,
            listOf(SportTask::class),
            true
        )

        TasksOfDayCounter(
            Icons.AutoMirrored.Outlined.LibraryBooks,
            tasks,
            listOf(
                GeneralTask::class,
                GoogleTask::class
            )
        )

        TasksOfDayCounter(
            Icons.Outlined.Restaurant,
            tasks,
            listOf(
                CustomMealTask::class,
                MealTask::class
            )
        )

        TasksOfDayCounter(
            Icons.AutoMirrored.Outlined.DirectionsRun,
            tasks,
            listOf(SportTask::class)
        )
    }
}

@Composable
private fun TasksOfDayCounter(
    icon: ImageVector,
    tasks: List<Task>,
    taskCategories: List<KClass<out Task>>,
    daily: Boolean = false
) {
    val (completed, uncompleted) = tasks
        .filter { it::class in taskCategories }
        .let { tasksFiltered ->
            if (SportTask::class in taskCategories) {
                tasksFiltered.filter { (it as? SportTask)?.isDaily == daily }
            } else {
                tasksFiltered
            }
        }
        .partition { it.isCompleted }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            contentDescription = "Task ${taskCategories.last().simpleName} group icon",
            imageVector = icon
        )

        Text(
            if (completed.isEmpty() && uncompleted.isEmpty()) {
                "-"
            } else {
                "${completed.size}/${completed.size + uncompleted.size}"
            }
        )
    }
}