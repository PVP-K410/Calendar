package com.pvp.app.ui.screen.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pvp.app.model.Task
import java.util.Calendar
import java.util.Date

@Composable
fun TaskBox(
    task: Task
) {

}

@Composable
fun TaskScreen(
    task: Task
) {

}

@Composable
fun SportTaskForm() {
    var supportsDistanceMetrics by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(Date()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            //horizontalAlignment = Alignment.CenterHorizontally
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
            Checkbox(
                checked = supportsDistanceMetrics,
                onCheckedChange = { supportsDistanceMetrics = it },
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text (
                text = "Supports distance metrics",
                modifier = Modifier.padding(start = 16.dp)
            )}

            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            TextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duration") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            DatePicker(
                selectedDate = startDate,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            Button(onClick = { /* ???? */ }) {
                Text("Submit")
            }
        }
    }
}

@Composable
fun DatePicker(
    selectedDate: Date,
    modifier: Modifier = Modifier
) {
    val calendar = Calendar.getInstance()
    calendar.time = selectedDate
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    Column(modifier) {
        Text("Start Date")
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Year:")
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = year.toString(),
                onValueChange = { },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Month:")
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = (month + 1).toString(),
                onValueChange = { },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Day:")
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = day.toString(),
                onValueChange = { },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SportTaskForm()
}