package com.pvp.app.ui.screen.goals

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.model.Goal
import com.pvp.app.ui.screen.layout.FloatingActionButton

@Composable
fun GoalScreen(
    model: GoalViewModel = hiltViewModel(),
    modifier: Modifier
) {
    val state by model.state.collectAsStateWithLifecycle()
    var isDialogOpen by remember { mutableStateOf(false) }
    val toggleDialog = remember { { isDialogOpen = !isDialogOpen } }

    Scaffold(
        content = { paddingValues ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Column {
                    val goals = state.currentGoals

                    val filter by remember {
                        derivedStateOf {
                            if (state.monthly) GoalFilter.Monthly else GoalFilter.Weekly
                        }
                    }

                    GoalTypeFilter(filter = filter) {
                        if (it != filter) model.changeMonthly()
                    }

                    Spacer(modifier = Modifier.padding(8.dp))

                    DateChanger()

                    Spacer(modifier = Modifier.padding(8.dp))

                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        if (!goals.any()) {
                            item {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        fontStyle = FontStyle.Italic,
                                        modifier = Modifier.padding(32.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        text = "No goals have been set up yet."
                                    )
                                }
                            }
                        } else {
                            items(goals) { goal ->
                                GoalCard(
                                    goal = goal,
                                    monthSteps = state.monthSteps
                                )
                            }
                        }
                    }
                }
            }

            GoalCreateDialog(
                onClose = toggleDialog,
                isOpen = isDialogOpen,
            )
        },
        floatingActionButton = {
            FloatingActionButton(toggleDialog)
        },
        floatingActionButtonPosition = FabPosition.End,
    )
}

@Composable
fun GoalCard(
    goal: Goal,
    monthSteps: Long
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = goal.activity.title + if (goal.monthly) {
                    " monthly"
                } else {
                    " weekly"
                } + " goal"
            )

            Spacer(modifier = Modifier.padding(2.dp))

            Text(
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = goal.startDate.toString() + " - " + goal.endDate.toString()
            )

            Spacer(modifier = Modifier.padding(8.dp))

            ProgressBar(goal = goal)

            Spacer(modifier = Modifier.padding(4.dp))

            Row(
                modifier = Modifier.padding(6.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(
                        end = 8.dp
                    ),
                    textAlign = TextAlign.Left,
                    text = "Set goal: ${
                        when (goal.steps) {
                            true -> "${goal.goal.toInt()} steps"
                            false -> "${goal.goal} km"
                        }
                    }"
                )

                Icon(
                    imageVector = goal.activity.icon,
                    contentDescription = "Activity icon",
                )
            }

            if (goal.steps) {
                Spacer(modifier = Modifier.padding(4.dp))

                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 6.dp),
                    textAlign = TextAlign.Left,
                    text = "Your average " + when (goal.monthly) {
                        true -> "monthly steps: $monthSteps"
                        false -> "weekly steps: ${monthSteps / 30 * 7}"
                    }
                )
            }
        }
    }
}

@Composable
fun GoalTypeFilter(
    filter: GoalFilter,
    onClick: (GoalFilter) -> Unit
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
        GoalFilter.entries.forEach { filterNew ->
            GoalTypeSelector(
                filter = filterNew,
                isSelected = filter == filterNew,
                onClick = { onClick(filterNew) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .height(40.dp)
            )
        }
    }
}

@Composable
private fun GoalTypeSelector(
    filter: GoalFilter,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(contentAlignment = Alignment.Center,
        modifier = modifier
            .clickable { onClick() }
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    Color.Transparent
                },
                MaterialTheme.shapes.medium
            )) {
        Text(text = filter.displayName)
    }
}

@Composable
private fun DateChanger(
    model: GoalViewModel = hiltViewModel(),
) {
    val state by model.state.collectAsStateWithLifecycle()

    Row {
        IconButton(onClick = {
            model.previous()
        }) {
            Icon(
                contentDescription = "Back",
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft
            )
        }

        Text(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            style = MaterialTheme.typography.bodyLarge,
            text = when (state.monthly) {
                true -> {
                    "${state.monthStartDate} - ${state.monthEndDate}"
                }

                false -> {
                    "${state.weekStartDate} - ${state.weekEndDate}"
                }
            },
            textAlign = TextAlign.Center
        )

        IconButton(onClick = {
            model.next()
        }) {
            Icon(
                contentDescription = "Forward",
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight
            )
        }
    }
}

@Composable
fun ProgressBar(goal: Goal) {
    val progress by animateFloatAsState(
        animationSpec = tween(durationMillis = 1000),
        label = "ExperienceProgressAnimation",
        targetValue = goal.progress.toFloat() / goal.goal.toFloat(),
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
    ) {
        LinearProgressIndicator(
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp),
            progress = { progress },
            strokeCap = StrokeCap.Round,
            trackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f)
        )

        Text(
            text = when (goal.steps) {
                true -> "${goal.progress.toInt()} / ${goal.goal.toInt()} steps"
                false -> "${goal.progress} / ${goal.goal} km"
            },
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

enum class GoalFilter(val displayName: String) {
    Weekly("Weekly"),
    Monthly("Monthly")
}