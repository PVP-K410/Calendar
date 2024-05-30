package com.pvp.app.ui.screen.goals

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.R
import com.pvp.app.model.Goal
import com.pvp.app.ui.common.TabSelector
import kotlin.math.max

@Composable
fun GoalScreen(
    model: GoalViewModel = hiltViewModel(),
    modifier: Modifier
) {
    val localeButtonContent = stringResource(R.string.goals_icon_create_goal_content_description)
    val localeNotSet = stringResource(R.string.goals_text_nothing_set)
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
                    val (completed, goals) = state.currentGoals.partition { it.completed }

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
                                        text = localeNotSet
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

                        if (completed.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.padding(24.dp))

                                Row {
                                    Icon(
                                        imageVector = Icons.Outlined.Check,
                                        contentDescription = "Completed goals",
                                        tint = MaterialTheme.colorScheme.primary,
                                    )

                                    Text(
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Left,
                                        color = MaterialTheme.colorScheme.primary,
                                        text = " Completed"
                                    )
                                }
                            }

                            items(completed) { goal ->
                                GoalCompletedCard(
                                    goal = goal
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
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = toggleDialog,
                shape = CircleShape
            ) {
                Icon(
                    contentDescription = localeButtonContent,
                    imageVector = Icons.Outlined.Add,
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    )
}

@Composable
fun GoalCard(
    goal: Goal,
    monthSteps: Long
) {
    val localeGoal = stringResource(R.string.goals_card_goal)
    val localeSteps = stringResource(R.string.goals_card_steps)
    val localeStepsMonthly = stringResource(R.string.goals_card_average_monthly_steps)
    val localeStepsWeekly = stringResource(R.string.goals_card_average_weekly_steps)
    val localeKm = stringResource(R.string.goals_card_km)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
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
                text = goal.activity.title()
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

            Row(modifier = Modifier.padding(6.dp)) {
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(end = 8.dp),
                    textAlign = TextAlign.Left,
                    text = "$localeGoal ${
                        when (goal.steps) {
                            true -> localeSteps.format(goal.target.toInt())
                            false -> localeKm.format(goal.target)
                        }
                    }"
                )

                Icon(
                    imageVector = goal.activity.icon,
                    contentDescription = null,
                )
            }

            if (goal.steps) {
                Spacer(modifier = Modifier.padding(2.dp))

                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 6.dp),
                    textAlign = TextAlign.Left,
                    text = if (goal.monthly) {
                        localeStepsMonthly.format(monthSteps)
                    } else {
                        localeStepsWeekly.format(monthSteps / 30 * 7)
                    }
                )
            }
        }
    }
}

@Composable
fun GoalCompletedCard(
    goal: Goal
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                2.dp,
                MaterialTheme.colorScheme.secondary,
                RoundedCornerShape(10.dp)
            )
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
                text = goal.activity.title.invoke() + " goal is completed!"
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

            Row(modifier = Modifier.padding(6.dp)) {
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(end = 8.dp),
                    textAlign = TextAlign.Left,
                    text = "Your goal was: ${
                        when (goal.steps) {
                            true -> "${goal.target.toInt()} steps"
                            false -> "${goal.target} km"
                        }
                    }"
                )

                Icon(
                    imageVector = goal.activity.icon,
                    contentDescription = "Activity icon",
                )
            }
        }
    }
}

@Composable
fun GoalTypeFilter(
    filter: GoalFilter,
    isForm: Boolean = false,
    onClick: (GoalFilter) -> Unit
) {
    TabSelector(
        onSelect = { onClick(GoalFilter.entries[it]) },
        tab = filter.ordinal,
        tabs = GoalFilter.entries.map { it.displayName() },
        withShadow = !isForm
    )
}

@Composable
private fun DateChanger(model: GoalViewModel = hiltViewModel()) {
    val localeBack = stringResource(R.string.goals_icon_back_content_description)
    val localeForward = stringResource(R.string.goals_icon_forward_content_description)
    val state by model.state.collectAsStateWithLifecycle()

    Row {
        IconButton(onClick = {
            model.previous()
        }) {
            Icon(
                contentDescription = localeBack,
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
                contentDescription = localeForward,
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight
            )
        }
    }
}

@Composable
fun ProgressBar(goal: Goal) {
    val localeStepsProgress = stringResource(R.string.goals_card_steps_progress)
    val localeKmProgress = stringResource(R.string.goals_card_km_progress)

    val progress by animateFloatAsState(
        animationSpec = tween(durationMillis = 1000),
        label = "ExperienceProgressAnimation",
        targetValue = goal.progress.toFloat() / max(
            1f,
            goal.target.toFloat()
        )
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
            text = if (goal.steps) {
                localeStepsProgress
            } else {
                localeKmProgress
            }
                .format(
                    goal.progress,
                    goal.target
                ),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

enum class GoalFilter(val displayName: @Composable () -> String) {
    Weekly({ stringResource(R.string.goals_weekly) }),
    Monthly({ stringResource(R.string.goals_monthly) })
}