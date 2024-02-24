package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pvp.app.model.Task
import com.pvp.app.ui.screen.task.TaskBox
import java.time.DayOfWeek

@Composable
fun CalendarScreen() {
    Week()
}

@Composable
fun Day(
    name: String,
    tasks: List<Task> = listOf()
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
fun Week() {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        (1..7).forEach {
            Day(DayOfWeek.of(it).name)
        }
    }
}