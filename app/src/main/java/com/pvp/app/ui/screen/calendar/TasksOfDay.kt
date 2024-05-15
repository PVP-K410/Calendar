@file:OptIn(ExperimentalMaterial3Api::class)

package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pvp.app.model.CustomMealTask
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
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
    val selectedTabIndex = when (filter) {
        TaskFilter.Daily -> 0
        TaskFilter.General -> 1
        TaskFilter.Meal -> 2
        TaskFilter.Sports -> 3
    }

    Row(
        modifier = Modifier.shadow(
            elevation = 6.dp,
            shape = MaterialTheme.shapes.medium
        ),
    ) {

        PrimaryTabRow(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            divider = {},
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium),
            selectedTabIndex = selectedTabIndex,
        ) {
            TaskFilter.entries.forEach { filterNew ->
                Tab(
                    modifier = Modifier.height(32.dp),
                    selected = filter == filterNew,
                    onClick = { onClick(filterNew) }
                ) {
                    Text(
                        text = filterNew.displayName,
                        color = MaterialTheme.colorScheme.inverseSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (filterNew == filter) FontWeight.Bold else FontWeight.Normal
                    )
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
        TaskFilter.Daily -> tasks.filter { it is SportTask && it.isDaily }
        TaskFilter.Sports -> tasks.filter { it is SportTask && !it.isDaily }
        TaskFilter.Meal -> tasks.filterIsInstance<CustomMealTask>()
        TaskFilter.General -> tasks.filter { task -> task !is SportTask && task !is CustomMealTask }
    }
}

enum class TaskFilter(val displayName: String) {
    Daily("Daily"),
    General("General"),
    Meal("Meal"),
    Sports("Sport")
}