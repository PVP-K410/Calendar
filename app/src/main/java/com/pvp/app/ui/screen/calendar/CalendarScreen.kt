package com.pvp.app.ui.screen.calendar

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.screen.task.CreateTaskGeneralForm
import com.pvp.app.ui.screen.task.CreateTaskMealForm
import com.pvp.app.ui.screen.task.CreateTaskSportForm
import com.pvp.app.ui.screen.task.TaskBox
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.reflect.KClass

@Composable
private fun ButtonTaskSelector(
    isSelected: Boolean,
    onSelect: () -> Unit,
    text: String
) {
    Button(onClick = onSelect) {
        if (isSelected) {
            Icon(
                contentDescription = "Currently selected form",
                imageVector = Icons.Outlined.Place
            )
        }

        Text(
            style = MaterialTheme.typography.labelLarge,
            text = text
        )
    }
}

@Preview
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val state by viewModel.state.collectAsStateWithLifecycle()

        Week(tasks = state.tasksWeek)
    }
}

@Composable
fun CreateTaskDialog(
    onClose: () -> Unit,
    isOpen: Boolean,
    shouldCloseOnSubmit: Boolean
) {
    if (!isOpen) {
        return
    }

    Dialog(onDismissRequest = onClose) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(8.dp)
        ) {
            var target by remember { mutableStateOf(Task::class as KClass<out Task>) }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ButtonTaskSelector(
                    isSelected = target == Task::class,
                    onSelect = { target = Task::class },
                    text = "General"
                )

                ButtonTaskSelector(
                    isSelected = target == MealTask::class,
                    onSelect = { target = MealTask::class },
                    text = "Meal"
                )

                ButtonTaskSelector(
                    isSelected = target == SportTask::class,
                    onSelect = { target = SportTask::class },
                    text = "Sport"
                )
            }

            Spacer(modifier = Modifier.padding(4.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.padding(4.dp))

            val modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp)
                .verticalScroll(rememberScrollState())

            val closeIfShould = {
                if (shouldCloseOnSubmit) {
                    onClose()
                }
            }

            when (target) {
                MealTask::class -> CreateTaskMealForm(
                    modifier = modifier,
                    onCreate = closeIfShould
                )

                SportTask::class -> CreateTaskSportForm(
                    modifier = modifier,
                    onCreate = closeIfShould
                )

                Task::class -> CreateTaskGeneralForm(
                    modifier = modifier,
                    onCreate = closeIfShould
                )
            }
        }
    }
}

@Composable
fun Day(
    name: String = "Day",
    date: LocalDate = LocalDate.MIN,
    tasks: List<Task> = emptyList(),
    expandedUponCreation: Boolean = false
) {
    var expand by remember { mutableStateOf(expandedUponCreation) }
    var selectedFilter by remember { mutableStateOf(TaskFilter.Daily) }
    val filteredTasks = filterTasks(tasks, selectedFilter)

    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .size(
                    height = 250.dp,
                    width = 300.dp
                )
                .border(
                    BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { expand = !expand }
                .align(Alignment.CenterHorizontally)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .height(60.dp)
                        .fillMaxWidth()
                        .clickable { expand = !expand }
                ) {
                    Text(
                        text = name,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }

                if (!date.isEqual(LocalDate.MIN) && !date.isAfter(LocalDate.now())) {
                    Text(
                        text = "Steps of the day",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )

                    StepCounter(date = date)
                }
            }
        }

        if (expand) {
            Spacer(modifier = Modifier.padding(8.dp))

            TaskFilterBar(selectedFilter) { filter ->
                selectedFilter = filter
            }

            // Fixed to take up the whole screen for now as it bugs out in Weekly view,
            // replace Modifier.width with Modifier.fillMaxWidth() later
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp

            LazyColumn(
                modifier = Modifier.width(screenWidth)
            ) {
                items(filteredTasks) {
                    Spacer(modifier = Modifier.padding(8.dp))

                    TaskBox(task = it)
                }
            }
        }
    }
}

private fun filterTasks(
    tasks: List<Task>,
    filter: TaskFilter
): List<Task> {
    return when (filter) {
        TaskFilter.Daily -> emptyList()
        TaskFilter.Sports -> tasks.filterIsInstance<SportTask>()
        TaskFilter.Meal -> tasks.filterIsInstance<MealTask>()
        TaskFilter.General -> tasks.filter { task -> task !is SportTask && task !is MealTask }
    }
}

enum class TaskFilter(val displayName: String) {
    Daily("Daily"),
    General("General"),
    Sports("Sports"),
    Meal("Meal")
}

@Composable
fun StepCounter(
    model: CalendarViewModel = hiltViewModel(),
    date: LocalDate
) {
    // Required for checking whether user has permissions before entering the window,
    // as users can revoke permissions at any time
    val permissionContract = PermissionController.createRequestPermissionResultContract()

    val launcher =
        rememberLauncherForActivityResult(permissionContract) {
            model.getDaysSteps(date)
        }

    LaunchedEffect(Unit) {
        if (model.permissionsGranted()) {
            model.getDaysSteps(date)
        } else {
            launcher.launch(PERMISSIONS)
        }
    }

    val steps = model.stepsCount.collectAsStateWithLifecycle()
    val goal = 10000f // TODO create way for user to set a step goal?
    val progress = steps.value / goal

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        val backgroundArcColor = MaterialTheme.colorScheme.primaryContainer
        val progressArcColor = MaterialTheme.colorScheme.primary

        Canvas(modifier = Modifier.size(100.dp)) {
            val strokeWidth = 6.dp.toPx()
            val radius = 300f
            val topLeft = Offset(
                (size.width / 2) - (radius / 2),
                (size.height / 2) - (radius / 2)
            )
            val size = Size(radius, radius)

            drawArc(
                color = backgroundArcColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = size,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )

            drawArc(
                color = progressArcColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = topLeft,
                size = size,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }

        Text(
            text = "${steps.value}",
            style = MaterialTheme.typography.titleSmall,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TaskFilterBar(
    selectedFilter: TaskFilter,
    onClick: (TaskFilter) -> Unit
) {
    // Fixed to take up the whole screen for now as it bugs out in Weekly view,
    // replace Modifier.width with Modifier.weigh(1f)
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val chipWidth = screenWidth / TaskFilter.values().size

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
        TaskFilter.entries.forEach { filter ->
            FilterBox(
                filter = filter,
                isSelected = selectedFilter == filter,
                onClick = { onClick(filter) },
                modifier = Modifier
                    .width(chipWidth)
                    .height(40.dp)
            )
        }
    }
}

@Composable
fun FilterBox(
    filter: TaskFilter,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clickable { onClick() }
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    Color.Transparent
                },
                MaterialTheme.shapes.medium
            )
    ) {
        Text(text = filter.displayName)
    }
}

@Composable
fun Week(
    modifier: Modifier = Modifier,
    tasks: List<Task>
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .fillMaxWidth()
    ) {
        (1..7).forEach {
            Day(
                name = DayOfWeek.of(it).name,
                tasks = tasks.filter { task -> task.scheduledAt.dayOfWeek.value == it }
            )
        }
    }
}