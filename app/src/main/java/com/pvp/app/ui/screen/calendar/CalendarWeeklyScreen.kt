package com.pvp.app.ui.screen.calendar

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Nightlight
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
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
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import kotlin.math.min
import kotlin.math.roundToInt
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

@Composable
fun CalorieCounter(
    model: CalendarWeeklyViewModel = hiltViewModel(),
    date: LocalDate
) {
    var calories by remember { mutableDoubleStateOf(0.0) }
    var launcherTriggered by remember { mutableStateOf(false) }
    val permissionContract = PermissionController.createRequestPermissionResultContract()

    val launcher =
        rememberLauncherForActivityResult(permissionContract) {
            launcherTriggered = !launcherTriggered
        }

    LaunchedEffect(date, launcherTriggered) {
        if (model.permissionsGranted()) {
            calories = model.getDaysCaloriesTotal(date)
        } else {
            launcher.launch(PERMISSIONS)
        }
    }

    Icon(
        imageVector = Icons.Outlined.LocalFireDepartment,
        contentDescription = "Calories",
        modifier = Modifier.size(26.dp)
    )

    Text(
        text = "${(calories / 1000).roundToInt()} kCal",
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(start = 8.dp)
    )
}

@Composable
fun CalendarWeeklyScreen(
    viewModel: CalendarWeeklyViewModel = hiltViewModel()
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
    date: LocalDateTime? = null,
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
                    date = date,
                    modifier = modifier,
                    onCreate = closeIfShould
                )

                SportTask::class -> CreateTaskSportForm(
                    date = date,
                    modifier = modifier,
                    onCreate = closeIfShould
                )

                Task::class -> CreateTaskGeneralForm(
                    date = date,
                    modifier = modifier,
                    onCreate = closeIfShould
                )
            }
        }
    }
}

@Composable
fun Day(
    clickEnabled: Boolean = false,
    date: LocalDate = LocalDate.MIN,
    name: String = "Day",
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .size(
                    height = 180.dp,
                    width = 200.dp
                )
                .border(
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .align(Alignment.CenterHorizontally)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(
                        enabled = clickEnabled,
                        onClick = onClick
                    )
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .height(60.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        style = MaterialTheme.typography.titleLarge,
                        text = name,
                        textAlign = TextAlign.Center
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        fontSize = 50.sp,
                        text = date.dayOfMonth.toString()
                    )
                }
            }
        }
    }
}

@Composable
fun ActivitiesBox(
    date: LocalDate,
    tasks: List<Task>
) {
    Spacer(modifier = Modifier.padding(16.dp))

    Box(
        modifier = Modifier.size(
            height = 270.dp,
            width = 300.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            ActivityBox(
                modifier = Modifier.fillMaxWidth(),
                columnModifier = Modifier.padding(16.dp)
            ) {
                Text(
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Today's tasks"
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    //TodaysTasks(Icons.Outlined.Event, tasks, Daily::class) // TODO uncomment when Daily tasks are implemented
                    TodaysTasks(Icons.AutoMirrored.Outlined.LibraryBooks, tasks, Task::class)
                    TodaysTasks(Icons.AutoMirrored.Outlined.DirectionsRun, tasks, SportTask::class)
                    TodaysTasks(Icons.Outlined.Restaurant, tasks, MealTask::class)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActivityBox(
                    modifier = Modifier.weight(1f),
                    columnModifier = Modifier.fillMaxSize()
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

                Spacer(modifier = Modifier.width(4.dp))

                ActivityBox(
                    modifier = Modifier.weight(1f),
                    columnModifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActivityRow {
                        CalorieCounter(date = date)
                    }

                    ActivityRow {
                        Icon(
                            imageVector = Icons.Outlined.MonitorHeart,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp)
                        )

                        Text(
                            text = "0",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    ActivityRow {
                        Icon(
                            imageVector = Icons.Outlined.Nightlight,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp)
                        )

                        Text(
                            text = "0 hr 0 m",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodaysTasks(
    icon: ImageVector,
    tasks: List<Task>,
    taskCategory: KClass<out Task>
) {
    val tasksOfCategory = tasks.filter { it::class == taskCategory }
    val completedTasks = tasksOfCategory.filter { it.isCompleted }

    val text = if (tasksOfCategory.isEmpty()) {
        "-"
    } else {
        "${completedTasks.size}/${tasksOfCategory.size}"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )

        Text(text = text)
    }
}

@Composable
fun ActivityBox(
    modifier: Modifier = Modifier,
    columnModifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.shapes.small
            )
            .border(
                BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Column(
            modifier = columnModifier,
            verticalArrangement = verticalArrangement,
            content = content
        )
    }
}

@Composable
fun ActivityRow(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
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
    model: CalendarWeeklyViewModel = hiltViewModel(),
    date: LocalDate
) {
    var steps by remember { mutableStateOf<Long>(0L) }
    var launcherTriggered by remember { mutableStateOf<Boolean>(false) }

    // Required for checking whether user has permissions before entering the window,
    // as users can revoke permissions at any time
    val permissionContract = PermissionController.createRequestPermissionResultContract()

    val launcher =
        rememberLauncherForActivityResult(permissionContract) {
            launcherTriggered = !launcherTriggered
        }

    LaunchedEffect(date, launcherTriggered) {
        if (model.permissionsGranted()) {
            steps = model.getDaysSteps(date)

        } else {
            launcher.launch(PERMISSIONS)
        }
    }

    val goal = 10000f // TODO create way for user to set a step goal?
    val progress = steps / goal

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize(fraction = 1f)
            .padding(bottom = 4.dp)
    ) {
        val backgroundArcColor = MaterialTheme.colorScheme.primaryContainer
        val progressArcColor = MaterialTheme.colorScheme.primary

        Canvas(modifier = Modifier.fillMaxSize(fraction = 1f)) {
            val strokeWidth = 6.dp.toPx()
            val radius = min(size.width, size.height) / 2 - strokeWidth

            val topLeft = Offset(
                (size.width / 2) - radius,
                (size.height / 2) - radius
            )

            val size = Size(
                radius * 2,
                radius * 2
            )

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
            text = steps.toString(),
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TaskFilterBar(
    selectedFilter: TaskFilter,
    onClick: (TaskFilter) -> Unit
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
        TaskFilter.entries.forEach { filter ->
            FilterBox(
                filter = filter,
                isSelected = selectedFilter == filter,
                onClick = { onClick(filter) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
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
fun DayCard(
    clickEnabled: Boolean,
    date: LocalDate,
    day: String,
    onClick: () -> Unit,
    page: Int,
    pageIndex: Int
) {
    val scale by animateFloatAsState(
        targetValue = if (pageIndex == page) 1f else 0.8f,
        animationSpec = spring(stiffness = 500f),
        label = "DayCardAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        Day(
            clickEnabled = clickEnabled,
            date = date,
            name = day,
            onClick = onClick
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Week(
    modifier: Modifier = Modifier,
    tasks: List<Task>
) {
    val days = (1..7).map { DayOfWeek.of(it).name }
    val today = LocalDate.now()
    val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val dates = (0..6).map { startOfWeek.plusDays(it.toLong()) }
    var stateDialog by remember { mutableStateOf(false) }

    val statePager = rememberPagerState(
        initialPage = dates.indexOf(today),
        pageCount = { days.size }
    )

    val date = dates[statePager.currentPage]
    var stateShowCards by remember { mutableStateOf(false) }

    val tasksFiltered = remember(tasks.size, date) {
        tasks.filter { task ->
            task.scheduledAt.toLocalDate() == date
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            contentPadding = PaddingValues(
                90.dp,
                0.dp
            ),
            modifier = modifier.height(LocalConfiguration.current.screenHeightDp.dp / 3),
            state = statePager
        ) { page ->
            DayCard(
                clickEnabled = date == dates[page],
                date = dates[page],
                day = days[page],
                onClick = {
                    if (tasksFiltered.isEmpty()) {
                        stateDialog = true
                    } else {
                        stateShowCards = !stateShowCards
                    }
                },
                page = page,
                pageIndex = statePager.currentPage
            )
        }

        if (!stateShowCards || tasksFiltered.isEmpty()) {
            if (!date.isEqual(LocalDate.MIN) && !date.isAfter(LocalDate.now())) {
                ActivitiesBox(
                    date = date,
                    tasks = tasksFiltered
                )
            }
        } else {
            DayContent(tasksFiltered)
        }

        CreateTaskDialog(
            date = date.atTime(0, 0),
            isOpen = stateDialog,
            onClose = { stateDialog = false },
            shouldCloseOnSubmit = true
        )
    }
}

@Composable
private fun DayContent(
    tasks: List<Task>
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            var filter by remember { mutableStateOf(TaskFilter.General) }

            Spacer(modifier = Modifier.padding(16.dp))

            TaskFilterBar(filter) { filter = it }

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                val filteredTasks = filterTasks(
                    tasks,
                    filter
                )

                if (!filteredTasks.any()) {
                    item {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                fontStyle = FontStyle.Italic,
                                modifier = Modifier.padding(32.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                text = "No ${
                                    filter.toString().lowercase(Locale.ROOT)
                                } tasks have been setup for this day"
                            )
                        }
                    }
                } else {
                    items(filteredTasks) {
                        Spacer(modifier = Modifier.padding(8.dp))

                        TaskBox(task = it)
                    }
                }
            }
        }
    }
}