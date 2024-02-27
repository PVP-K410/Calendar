package com.pvp.app.ui.view.component.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel

data class GeneralPersonalTask(val id: Int, val title: String, val description: String,
                               val frequency: Int )

class TaskViewModel : ViewModel() {
/*
    private val sharedPreferences =
        application.getSharedPreferences("tasks_prefs", Context.MODE_PRIVATE)
*/

    private val _tasks = mutableStateListOf<GeneralPersonalTask>()
    val tasks: List<GeneralPersonalTask> get() = _tasks

    fun addTask(task: GeneralPersonalTask) {
        if (task.title.isEmpty() || task.description.isEmpty() || task.frequency <= 0) {
            return
        }
        _tasks.add(task)
    }

/*    private fun saveTasks() {
        val tasksJson = Gson().toJson(_tasks)
        sharedPreferences.edit().putString("tasks", tasksJson).apply()
    }

    private fun loadTasks() {
        val tasksJson = sharedPreferences.getString("tasks", null)
        tasksJson?.let {
            val typeToken = object : TypeToken<List<GeneralPersonalTask>>() {}.type
            _tasks.addAll(Gson().fromJson(it, typeToken))
        }
    }*/
}

@Composable
fun TaskForm(viewModel: TaskViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var frequency by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally, // Center the content horizontally
            verticalArrangement = Arrangement.Center // Center the content vertically
    ) {
        Text(
            text = "Create a General Task",
            style = TextStyle(
                fontSize = 24.sp, // Change the font size as needed
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.15.sp
            ),
            modifier = Modifier.padding(bottom = 16.dp) // Add some bottom padding for separation
        )

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

        TextField(
            value = frequency.toString(),
            onValueChange = { frequency = it.toInt() },
            label = { Text("Reminder frequency in hours") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Button(
            onClick = {
                viewModel.addTask(
                    GeneralPersonalTask(
                        viewModel.tasks.size + 1,
                        title,
                        description,
                        frequency
                    )
                )
                title = ""
                description = ""
                frequency = 0
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
fun TaskList(tasks: List<GeneralPersonalTask>) {
    LazyColumn {
        item {
            Text(
                text = "Your General Tasks",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = task.title, fontWeight = FontWeight.Bold)
                            Text(text = task.description)
                        }
                        Text(text = "Every ${task.frequency} hours")
                    }
                }
            }
        }
    }

}

@Composable
fun TaskFormAndList() {
    val viewModel = TaskViewModel()

    Column {
        TaskForm(viewModel)
        TaskList(viewModel.tasks)
    }
}

@Preview(showSystemUi = true)
@Composable
fun TaskFormAndListPreview() {
    val viewModel = TaskViewModel()
    val dummyTasks = listOf(
        GeneralPersonalTask(2, "Task 2", "Description 2", 1),
        GeneralPersonalTask(1, "Task 1", "Description 1", 2),
        GeneralPersonalTask(3, "Task 3", "Description 3", 3)
    )

    Column {
        TaskForm(viewModel)
        TaskList(dummyTasks)
    }
}

