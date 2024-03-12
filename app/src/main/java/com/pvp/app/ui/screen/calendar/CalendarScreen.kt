package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Card
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
import kotlin.reflect.KClass

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel()
) {
    var isOpen by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { isOpen = !isOpen },
                shape = CircleShape
            ) {
                Icon(
                    contentDescription = "Add task",
                    imageVector = Icons.Outlined.Add
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            val state by viewModel.state.collectAsStateWithLifecycle()

            Week(tasks = state.tasksWeek)

            if (!isOpen) {
                return@Column
            }

            Dialog(onDismissRequest = { isOpen = false }) {
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
                        CreateTaskFormSelector(
                            isSelected = target == Task::class,
                            onSelect = { target = Task::class },
                            text = "General"
                        )

                        CreateTaskFormSelector(
                            isSelected = target == MealTask::class,
                            onSelect = { target = MealTask::class },
                            text = "Meal"
                        )

                        CreateTaskFormSelector(
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

                    when (target) {
                        MealTask::class -> CreateTaskMealForm(
                            modifier = modifier,
                            onCreate = { isOpen = false }
                        )

                        SportTask::class -> CreateTaskSportForm(
                            modifier = modifier,
                            onCreate = { isOpen = false }
                        )

                        Task::class -> CreateTaskGeneralForm(
                            modifier = modifier,
                            onCreate = { isOpen = false }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateTaskFormSelector(
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
fun Day(
    name: String,
    tasks: List<Task>
) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .padding(8.dp)
    ) {
        Card(
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline))
        ) {
            Text(
                name,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline))
        ) {
            items(tasks) {
                TaskBox(task = it)
            }
        }
    }
}

@Composable
fun Week(
    modifier: Modifier = Modifier,
    tasks: List<Task>
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState())
    ) {
        (1..7).forEach {
            Day(
                name = DayOfWeek.of(it).name,
                tasks = tasks.filter { task -> task.scheduledAt.dayOfWeek.value == it }
            )
        }
    }
}