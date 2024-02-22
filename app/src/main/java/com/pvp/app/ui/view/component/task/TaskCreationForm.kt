package com.pvp.app.ui.view.component.task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel

data class PersonalTask(val id: Int, val title: String, val description: String)

class TaskViewModel : ViewModel() {
    private val _tasks = mutableStateListOf<PersonalTask>()
    val tasks: List<PersonalTask> get() = _tasks

    fun addTask(task: PersonalTask) {
        _tasks.add(task)
    }
}
@Composable
fun TaskForm(viewModel: TaskViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Button(
            onClick = {
                viewModel.addTask(PersonalTask(viewModel.tasks.size + 1, title, description))
                title = ""
                description = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Add Task")
        }
    }
}

@Composable
fun TaskList(tasks: List<PersonalTask>) {
    LazyColumn {
        items(tasks) { task ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(text = task.title, fontWeight = FontWeight.Bold)
                    Text(text = task.description)
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TaskFormPreview() {
    val viewModel = TaskViewModel()
    TaskForm(viewModel)
}

@Preview(showSystemUi = true)
@Composable
fun TaskListPreview() {
    val dummyTasks = listOf(
        PersonalTask(1, "Task 1", "Description 1"),
        PersonalTask(2, "Task 2", "Description 2"),
        PersonalTask(3, "Task 3", "Description 3")
    )
    TaskList(dummyTasks)
}