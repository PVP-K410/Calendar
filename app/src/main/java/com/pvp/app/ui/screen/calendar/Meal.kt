package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.model.Meal
import com.pvp.app.ui.common.AsyncImage
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.common.InfoTooltip

@Composable
private fun BoxScope.ButtonColumn(
    background: Color,
    buttonContent: (@Composable RowScope.() -> Unit)?,
    buttonEnabled: Boolean,
    onBackground: Color,
    onClick: () -> Unit
) {
    if (buttonContent != null) {
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = background,
                contentColor = onBackground
            ),
            enabled = buttonEnabled,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.BottomEnd),
            onClick = onClick,
            shape = CircleShape
        ) { buttonContent() }
    }
}

@Composable
private fun BoxScope.NutritionColumn(
    ingredientsToAvoid: List<String>,
    meal: Meal,
    onBackground: Color
) {
    Box(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        Color.Black
                    )
                )
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                color = onBackground,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                modifier = Modifier.width(120.dp),
                style = MaterialTheme.typography.titleSmall,
                text = meal.name
            )

            with(meal.nutrition.caloricBreakdown) {
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    color = onBackground,
                    text = "Carbs: $percentCarbs %"
                )

                Text(
                    color = onBackground,
                    style = MaterialTheme.typography.bodySmall,
                    text = "Fat: $percentFat %"
                )

                Text(
                    style = MaterialTheme.typography.bodySmall,
                    color = onBackground,
                    text = "Protein: $percentProtein %"
                )
            }
        }

        if (ingredientsToAvoid.isNotEmpty()) {
            Column(modifier = Modifier.align(Alignment.BottomEnd)) {
                TooltipIngredientsWarning(ingredientsToAvoid = ingredientsToAvoid)
            }
        }
    }
}

@Composable
fun MealCard(
    buttonContent: (@Composable RowScope.() -> Unit)? = null,
    buttonEnabled: Boolean = true,
    meal: Meal,
    model: MealViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val background = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.65f)
    val onBackground = MaterialTheme.colorScheme.onTertiary

    val ingredientsToAvoid by model
        .getIngredientsToAvoid(meal)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    Card(
        elevation = CardDefaults.elevatedCardElevation(),
        modifier = modifier,
        shape = MaterialTheme.shapes.medium
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                contentDescription = "${meal.name} representation image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                url = meal.image
            )

            NutritionColumn(
                ingredientsToAvoid = ingredientsToAvoid,
                meal = meal,
                onBackground = onBackground
            )

            ButtonColumn(
                background = background,
                buttonContent = buttonContent,
                buttonEnabled = buttonEnabled,
                onBackground = onBackground,
                onClick = onClick
            )

            MealCardToolTip(
                colorContainer = background,
                colorContent = onBackground,
                meal = meal
            )
        }
    }
}

@Composable
private fun BoxScope.MealCardToolTip(
    colorContainer: Color,
    colorContent: Color,
    meal: Meal
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.align(Alignment.TopEnd),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        InfoTooltip(
            iconTint = colorContent,
            modifier = Modifier
                .padding(8.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(colorContainer),
            tooltip = {
                Column(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(colorContainer)
                        .padding(8.dp)
                ) {
                    CompositionLocalProvider(LocalContentColor provides colorContent) {
                        Text(
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            text = meal.name
                        )

                        Spacer(modifier = Modifier.size(8.dp))

                        Text(
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            text = "Servings Breakdown"
                        )

                        TextBody("${meal.servings} servings")

                        with(meal.nutrition.weightPerServing) {
                            TextBody("$amount $unit per serving")
                        }

                        val calories = meal.nutrition.nutrients.firstOrNull {
                            it.name.equals(
                                "Calories",
                                ignoreCase = true
                            )
                        }?.amount

                        if (calories != null) {
                            TextBody("$calories calories per serving")
                        }

                        Spacer(modifier = Modifier.size(8.dp))

                        Text(
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            text = "Ingredients"
                        )

                        val ingredients = meal.recipe
                            .first().steps
                            .flatMap { it.ingredients }
                            .distinct()
                            .sorted()
                            .map { it.capitalize(Locale.current) }

                        if (ingredients.size < 5) {
                            ingredients.forEach {
                                TextBody(it)
                            }
                        } else {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ingredients
                                    .chunked(5)
                                    .forEach { column ->
                                        Column {
                                            column.forEach {
                                                TextBody(it)
                                            }
                                        }
                                    }
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun TextBody(text: String) {
    Text(
        style = MaterialTheme.typography.bodySmall,
        text = text
    )
}

@Composable
private fun TooltipIngredientsWarning(ingredientsToAvoid: List<String>) {
    InfoTooltip(
        iconTint = MaterialTheme.colorScheme.error,
        modifier = Modifier
            .padding(
                bottom = 12.dp,
                end = 8.dp
            )
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)),
        tooltip = {
            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.75f))
                    .padding(8.dp)
            ) {
                Text(
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                    text = "Consists of ingredients you avoid"
                )

                if (ingredientsToAvoid.size > 5) {
                    ingredientsToAvoid.forEach {
                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            text = it
                        )
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ingredientsToAvoid
                            .chunked(5)
                            .forEach { column ->
                                Column {
                                    column.forEach {
                                        Text(
                                            style = MaterialTheme.typography.bodyMedium,
                                            text = it
                                        )
                                    }
                                }
                            }
                    }
                }

            }
        }
    )
}