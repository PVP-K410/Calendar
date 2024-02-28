package com.pvp.app.ui.screen.task

import androidx.compose.runtime.Composable
import com.pvp.app.model.Task
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.stringResource
import com.pvp.app.R
import androidx.compose.material3.Slider
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

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
                    .border(0.5.dp,
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
fun TaskBox(
    task: Task
) {

}

@Composable
fun TaskScreen(
    task: Task
) {

}