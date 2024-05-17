package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.pvp.app.model.CustomMealTask
import com.pvp.app.model.GeneralTask
import com.pvp.app.model.GoogleTask
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import com.pvp.app.ui.common.TabSelector
import java.util.Locale

@Composable
fun TasksOfDay(tasks: List<Task>) {
    Box(contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.fillMaxWidth()) {
            var filter by remember { mutableStateOf(TaskFilter.Daily) }

            Spacer(modifier = Modifier.padding(12.dp))

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer,
                        MaterialTheme.shapes.medium
                    )
            ) {
                TaskTypeFilter(filter) { filter = it }

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
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
                                        filter
                                            .toString()
                                            .lowercase(Locale.ROOT)
                                    } tasks have been setup for this day"
                                )
                            }
                        }
                    } else {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        items(filteredTasks) {
                            TaskCard(task = it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskTypeFilter(
    filter: TaskFilter,
    onClick: (TaskFilter) -> Unit
) {
    TabSelector(
        onSelect = { onClick(TaskFilter.entries[it]) },
        tab = filter.ordinal,
        tabs = TaskFilter.entries.map { it.displayName }
    )
}

private fun filterTasks(
    tasks: List<Task>,
    filter: TaskFilter
): List<Task> {
    return when (filter) {
        TaskFilter.Daily -> tasks.filter { it is SportTask && it.isDaily }
        TaskFilter.Sports -> tasks.filter { it is SportTask && !it.isDaily }
        TaskFilter.Meal -> tasks.filter { it is CustomMealTask || it is MealTask }
        TaskFilter.General -> tasks.filter { it is GeneralTask || it is GoogleTask }
    }
}

enum class TaskFilter(val displayName: String) {
    Daily("Daily"),
    General("General"),
    Meal("Meal"),
    Sports("Sport")
}