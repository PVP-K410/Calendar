package com.pvp.app.ui.screen.statistics

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.R
import com.pvp.app.model.CustomMealTask
import com.pvp.app.model.GeneralTask
import com.pvp.app.model.Goal
import com.pvp.app.model.GoogleTask
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import com.pvp.app.ui.common.ProgressIndicatorWithinDialog
import com.pvp.app.ui.common.darken
import com.pvp.app.ui.common.orInDarkTheme
import com.pvp.app.ui.screen.goals.GoalCard

@Composable
private fun ActivityRow(model: DashboardViewModel = hiltViewModel()) {
    var calories by remember { mutableDoubleStateOf(0.0) }
    var heartRate by remember { mutableLongStateOf(0) }
    var steps by remember { mutableLongStateOf(0) }

    LaunchedEffect(Unit) {
        calories = model.getCalories()
        heartRate = model.getHeartRate()
        steps = model.getSteps()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.dashboard_activity_tracking),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ActivityRowItem(
                icon = ImageVector.vectorResource(R.drawable.steps_icon),
                title = stringResource(R.string.dashboard_steps),
                value = steps.toString()
            )

            ActivityRowItem(
                icon = Icons.Outlined.LocalFireDepartment,
                title = stringResource(R.string.dashboard_calories),
                value = "%.2f ".format(calories / 1000)
            )

            ActivityRowItem(
                icon = Icons.Outlined.MonitorHeart,
                title = stringResource(R.string.dashboard_BPM),
                value = heartRate.toString()
            )
        }
    }
}

@Composable
private fun ActivityRowItem(
    icon: ImageVector,
    title: String,
    value: String,
) {
    Box(modifier = Modifier.size(110.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize(0.85f)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .align(Alignment.Center)
                .shadow(0.25.dp)
                .padding(8.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = title,
                    fontSize = 14.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize(0.35f)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .align(Alignment.TopEnd)
                .shadow(0.25.dp)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                contentDescription = title,
                imageVector = icon,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun DashboardScreen(
    modifier: Modifier,
    model: DashboardViewModel = hiltViewModel()
) {
    val state by model.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        ProgressIndicatorWithinDialog()
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .then(modifier)
    ) {
        Column(
            Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header(state)

            if (!state.isHealthConnectEnabled) {
                InformationElement(stringResource(R.string.dashboard_healthconnect_not_enabled))

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (!state.isNotificationEnabled) {
                InformationElement(stringResource(R.string.dashboard_notification_not_enabled))

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (state.isHealthConnectEnabled) {
                ActivityRow()

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (state.tasks.isNotEmpty()) {
                Tasks(state.tasks)

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (state.goals.isNotEmpty()) {
                Goals(state.goals)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        StatisticsScreen()
    }
}

@Composable
fun Goals(goals: List<Goal>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.medium
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.dashboard_goals),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(goals) { goal ->
                GoalCard(
                    goal = goal,
                    monthSteps = 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun Header(state: DashboardState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = 15.dp,
                end = 30.dp,
                start = 30.dp,
                top = 15.dp
            ),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest
                        .darken(0.1f)
                        .orInDarkTheme(MaterialTheme.colorScheme.surfaceContainerHighest),
                    shape = CircleShape
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                alignment = Alignment.TopCenter,
                contentDescription = "Profile screen icon",
                modifier = Modifier.size(
                    height = 200.dp,
                    width = 200.dp
                ),
                painter = BitmapPainter(state.avatar)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            style = MaterialTheme.typography.titleLarge,
            text = state.user.username
        )
    }
}

@Composable
private fun InformationElement(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun Tasks(tasks: List<Task>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.medium
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.dashboard_tasks),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TaskItem(
                icon = Icons.Outlined.Event,
                tasks = tasks.filter { it is SportTask && it.isDaily }
            )

            TaskItem(
                icon = Icons.AutoMirrored.Outlined.LibraryBooks,
                tasks = tasks.filter { it is GeneralTask || it is GoogleTask },
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TaskItem(
                icon = Icons.Outlined.Restaurant,
                tasks = tasks.filter { it is MealTask || it is CustomMealTask },
            )

            TaskItem(
                icon = Icons.AutoMirrored.Outlined.DirectionsRun,
                tasks = tasks.filter { it is SportTask && !it.isDaily },
            )
        }
    }
}

@Composable
fun TaskItem(
    icon: ImageVector,
    tasks: List<Task>,
) {
    Box(
        modifier = Modifier
            .width(180.dp)
            .height(150.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(0.9f)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .align(Alignment.Center)
                .shadow(0.25.dp)
                .padding(8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Bottom
            ) {
                if (tasks.isEmpty()) {
                    Text(
                        text = stringResource(R.string.dashboard_tasks_not_found),
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Spacer(modifier = Modifier.height(20.dp))

                    for (task in tasks) {
                        Text(
                            text = task.title,
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .size(38.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .align(Alignment.TopEnd)
                .shadow(0.25.dp)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                contentDescription = "Tasks icon",
                imageVector = icon,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}