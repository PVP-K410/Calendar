package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pvp.app.model.Meal
import com.pvp.app.ui.common.AsyncImage

@Composable
fun MealCard(meal: Meal) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            contentDescription = "${meal.name} representation image",
            modifier = Modifier
                .size(
                    width = 300.dp,
                    height = 200.dp
                )
                .background(Color.Black),
            url = meal.image
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )

                Text(
                    text = "${meal.readyInMinutes} min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )

                Text(
                    text = "${meal.servings} servings",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }

            Column {
                with(meal.nutrition.caloricBreakdown) {
                    Text(
                        text = "Protein: $percentProtein",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )

                    Text(
                        text = "Fat: $percentFat",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )

                    Text(
                        text = "Carbs: $percentCarbs",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}