package com.pvp.app.ui.screen.task

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TaskBox(
    task: Task
) {
    var checked by remember {
        mutableStateOf(false)
    }
    Card(
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline))

    ) {
        Column(
        ) {
            val timeString = "Scheduled at " + task.scheduledAt.toLocalTime().format(
                DateTimeFormatter.ofPattern("HH:mm"))
            Row(
                modifier = Modifier.fillMaxWidth(),

                //horizontalArrangement = Arrangement.Absolute.Left
            ) {

                Checkbox(
                    checked = checked,
                    onCheckedChange = {checked = it},
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
            when(task){
                is SportTask -> SportTaskBoxBody(task)
                is MealTask -> MealTaskBoxBody(task)
                else -> TaskBoxBody(task)
            }
            Text(
                timeString,
                modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp)
            )
        }
    }
}

@Composable
fun SportTaskBoxBody(
    sportTask: SportTask
){


    //Divider(color = Color.Black, thickness = 1.dp)
//    if(task.description != null){
//
//        Text(
//            task.description.orEmpty(),
//            textAlign = TextAlign.Left,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp, 0.dp, 0.dp, 0.dp)
//        )
//    }
    if(sportTask.activity != null){
        var activityText = sportTask.activity?.title + ""
        if(sportTask.distance != null){
            activityText += " for " + sportTask.distance.toString() + " km"
        }else if(sportTask.duration != null){
            activityText += " for " + getDurationString(sportTask.duration!!)
        }

        Text(
            activityText,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 0.dp, 0.dp, 0.dp)
        )
    }
}
@Composable
fun MealTaskBoxBody(
    mealTask: MealTask
){
    Text(
        "Main ingredient: " + mealTask.recipe,
        textAlign = TextAlign.Left,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 0.dp, 0.dp, 0.dp)
    )
    if(mealTask.duration != null){
        Text(
            "Duration: " + getDurationString(mealTask.duration!!),
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 0.dp, 0.dp, 0.dp)
        )
    }
}

@Composable
fun TaskBoxBody(
    task: Task
){
    if(task.description != null){
        Text(
            task.description.toString(),
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 0.dp, 0.dp, 0.dp)
        )
    }

    if(task.duration != null){
        Text(
            "Duration: " + getDurationString(task.duration!!),
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 0.dp, 0.dp, 0.dp)
        )
    }
}

@Preview
@Composable
fun TaskScreen(
    //task: Task
) {
    // Only for testing purposes, delete later
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
        distance = null,
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

// Parses Duration object to a string
fun getDurationString(duration: Duration): String {
    val hours = duration.toHours()
    val minutes = duration.minusHours(hours).toMinutes()

    return buildString {
        if (hours > 0) append("$hours h")
        if (minutes > 0) append("${if (hours > 0) " " else ""}$minutes min")
    }
}