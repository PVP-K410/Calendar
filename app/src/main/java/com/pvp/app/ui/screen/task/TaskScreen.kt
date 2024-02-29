package com.pvp.app.ui.screen.task

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pvp.app.R
import java.util.Calendar
import java.util.Date
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import com.pvp.app.common.getDurationString
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun CreateMealTaskForm() {
    var description by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf(0) }
    var ingredients by remember { mutableStateOf("") }
    var preparation by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                stringResource(R.string.meal_Title),
                style = TextStyle(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .border(
                        0.5.dp,
                        Color.Black,
                        shape = RoundedCornerShape(5.dp)
                    ),
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    color = Color.Black
                )
            )

            Text(
                stringResource(R.string.meal_Duration),
                style = TextStyle(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
            )

            Slider(
                value = duration.toFloat(),
                onValueChange = { newValue -> duration = newValue.toInt() },
                valueRange = 1f..180f,
                steps = 180,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            )

            Text(
                text = "Duration: $duration minutes",
                style = TextStyle(
                    fontSize = 15.sp,
                    color = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )

            Text(
                stringResource(R.string.meal_Ingredients),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
            )

            OutlinedTextField(
                value = ingredients,
                onValueChange = { ingredients = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .border(
                        0.5.dp,
                        Color.Black,
                        shape = RoundedCornerShape(5.dp)
                    ),
                textStyle = TextStyle(
                    fontSize = 15.sp
                )
            )

            Text(
                stringResource(R.string.meal_Preparation),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
            )

            OutlinedTextField(
                value = preparation,
                onValueChange = { preparation = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .border(
                        0.5.dp,
                        Color.Black,
                        shape = RoundedCornerShape(5.dp)
                    ),
                textStyle = TextStyle(
                    fontSize = 15.sp
                )
            )

            Text(
                stringResource(R.string.meal_Description),
                style = TextStyle(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .border(
                        0.5.dp,
                        Color.Black,
                        shape = RoundedCornerShape(5.dp)
                    ),
                textStyle = TextStyle(
                    fontSize = 15.sp
                )
            )

            Button(
                onClick = {
                    /*TODO: Functionality */
                },
                modifier = Modifier
                    .width(120.dp)
                    .height(70.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 30.dp),
            ) {
                Text(
                    "Create",
                    style = TextStyle(
                        fontSize = 15.sp,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun MealTaskBoxBody(
    task: MealTask
) {
    Text(
        "Main ingredient: " + task.recipe,
        textAlign = TextAlign.Left,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
    )

    if (task.duration != null) {
        Text(
            "Duration: " + getDurationString(task.duration!!),
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        )
    }
}

@Composable
fun SportTaskBoxBody(
    task: SportTask
) {
    task.activity?.let { activity ->
        val activityText = buildString {
            append(activity.title)

            when {
                task.distance != null ->
                    append(" for ${task.distance} km")

                task.duration != null ->
                    append(" for ${getDurationString(task.duration!!)}")
            }
        }

        Text(
            text = activityText,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        )
    }
}

@Composable
fun TaskBoxBody(
    task: Task
) {
    task.description?.let { description ->
        Text(
            text = description,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        )
    }

    task.duration?.let { duration ->
        Text(
            text = "Duration: ${getDurationString(duration)}",
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        )
    }
}

@Composable
fun TaskBox(
    task: Task
) {
    // Later task.isCompleted should be used
    var checked by remember {
        mutableStateOf(false)
    }

    Card(
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline))
    ) {

        Column() {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    modifier = Modifier
                        .size(36.dp)
                        .align(CenterVertically)
                )

                Text(
                    task.title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(CenterVertically)
                        .weight(1f),
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            when (task) {
                is SportTask -> SportTaskBoxBody(task)
                is MealTask -> MealTaskBoxBody(task)
                else -> TaskBoxBody(task)
            }

            val timeString = "Scheduled at ${
                task.scheduledAt
                    .toLocalTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm"))
            }"

            Text(
                timeString,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun SportTaskForm() {
    var activity by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(Date()) }
    var supportsDistanceMetrics by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }

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
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            TextField(
                value = activity,
                onValueChange = { activity = it },
                label = { Text("Activity") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Checkbox(
                    checked = supportsDistanceMetrics,
                    onCheckedChange = { supportsDistanceMetrics = it },
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Supports distance metrics",
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            TextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duration") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            DatePicker(
                selectedDate = startDate,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Button(onClick = { /* TODO: Implement creation logic */ }) {
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

    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH)
    val year = calendar.get(Calendar.YEAR)

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

@Composable
fun TaskScreen(
    task: Task
) {
    // Only for testing purposes, delete later
    // To add preview, comment out task: Task from constructor.
    val mealTask = MealTask(
        description = "Prepare dinner",
        duration = Duration.ofMinutes(48),
        id = "1",
        isCompleted = false,
        recipe = "Chicken breast",
        scheduledAt = LocalDateTime.now(),
        title = "Cook Dinner",
        userEmail = "example@example.com"
    )

    val sportTask = SportTask(
        activity = SportActivity.Running,
        description = "Run in the park",
        distance = 5.0,
        duration = Duration.ofMinutes(65),
        id = "2",
        isCompleted = false,
        scheduledAt = LocalDateTime.now(),
        title = "Morning Run",
        userEmail = "example@example.com"
    )

    val task = Task(
        description = "Complete project tasks",
        duration = Duration.ofHours(2),
        id = "3",
        isCompleted = false,
        scheduledAt = LocalDateTime.now(),
        title = "Project Tasks",
        userEmail = "example@example.com"
    )

    TaskBox(task)
}
