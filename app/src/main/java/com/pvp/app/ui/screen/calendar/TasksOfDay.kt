package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import java.util.Locale

@Composable
private fun TaskTypeSelector(
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
fun TasksOfDay(tasks: List<Task>) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxWidth(0.9f)) {
            var filter by remember { mutableStateOf(TaskFilter.Daily) }

            Spacer(modifier = Modifier.padding(16.dp))

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
                    items(filteredTasks) {
                        Spacer(modifier = Modifier.padding(8.dp))

                        TaskCard(task = it)
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
        TaskFilter.entries.forEach { filterNew ->
            TaskTypeSelector(
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

private fun filterTasks(
    tasks: List<Task>,
    filter: TaskFilter
): List<Task> {
    return when (filter) {
        TaskFilter.Daily -> tasks.filter { it is SportTask && it.isDaily }
        TaskFilter.Sports -> tasks.filter { it is SportTask && !it.isDaily }
        TaskFilter.Meal -> tasks.filterIsInstance<MealTask>()
        TaskFilter.General -> tasks.filter { task -> task !is SportTask && task !is MealTask }
    }
}

enum class TaskFilter(val displayName: String) {
    Daily("Daily"),
    General("General"),
    Meal("Meal"),
    Sports("Sport")
}