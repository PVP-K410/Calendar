package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.common.TimeUtil.asString
import com.pvp.app.model.Meal
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.common.ButtonConfirm
import com.pvp.app.ui.common.EditableDateItem
import com.pvp.app.ui.common.EditableDistanceItem
import com.pvp.app.ui.common.EditablePickerItem
import com.pvp.app.ui.common.EditableSportActivityItem
import com.pvp.app.ui.common.EditableTextItem
import com.pvp.app.ui.common.EditableTimeItem
import com.pvp.app.ui.common.FoldableContent
import com.pvp.app.ui.common.pixelsToDp
import kotlinx.coroutines.launch
import java.time.LocalTime

@Composable
fun TaskFormButtonsRow(
    model: TaskViewModel = hiltViewModel(),
    onClose: () -> Unit,
    state: TaskFormState<*>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 24.dp,
                vertical = 15.dp
            ),
        horizontalArrangement = if (state.task == null) Arrangement.Center else Arrangement.SpaceBetween
    ) {
        if (state.task != null) {
            ButtonConfirm(
                border = BorderStroke(
                    1.dp,
                    Color.Red
                ),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                content = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            contentDescription = "Delete task icon",
                            imageVector = Icons.Outlined.Delete
                        )

                        Text(
                            style = MaterialTheme.typography.titleSmall,
                            text = "Delete"
                        )
                    }
                },
                confirmationButtonContent = { Text("Delete") },
                confirmationDescription = { Text("If the task is deleted, it cannot be recovered") },
                confirmationTitle = { Text("Are you sure you want to delete this task?") },
                onConfirm = {
                    model.remove(state.task!!)

                    onClose()
                },
                shape = MaterialTheme.shapes.extraLarge
            )
        }

        Button(
            enabled = state.isFormValid,
            onClick = {
                model.mergeTaskFromState(state = state)

                onClose()
            }
        ) { Text(if (state.task == null) "Create" else "Update") }
    }
}

@Composable
fun TaskFormFieldActivity(state: TaskFormState.Sport) {
    EditableSportActivityItem(
        label = "Activity",
        value = state.activity,
        onValueChange = { state.activity = it }
    )
}

@Composable
fun TaskFormFieldDate(state: TaskFormState<*>) {
    EditableDateItem(
        label = "Date",
        value = state.date,
        onValueChange = { state.date = it }
    )
}

@Composable
fun TaskFormFieldDescription(state: TaskFormState<*>) {
    val isRecipe = remember(state) { state is TaskFormState.CustomMeal }

    val description by remember(state) {
        derivedStateOf {
            when (state) {
                is TaskFormState.CustomMeal -> state.recipe
                is TaskFormState.General -> state.description
                is TaskFormState.Sport -> state.description
                else -> null
            } ?: ""
        }
    }

    val onChange = remember<(String) -> Unit>(state) {
        {
            when (state) {
                is TaskFormState.CustomMeal -> state.recipe = it
                is TaskFormState.General -> state.description = it
                is TaskFormState.Sport -> state.description = it
                else -> Unit
            }
        }
    }

    EditableTextItem(
        label = if (isRecipe) "Recipe" else "Description",
        value = description,
        onValueChange = { onChange(it) }
    )
}

@Composable
fun TaskFormFieldDistance(
    model: TaskViewModel = hiltViewModel(),
    state: TaskFormState.Sport
) {
    EditableDistanceItem(
        label = "Distance",
        value = state.distance,
        rangeKilometers = model.rangeKilometers,
        rangeMeters = model.rangeMeters,
        onValueChange = { state.distance = it }
    )
}

@Composable
fun TaskFormFieldDuration(
    model: TaskViewModel = hiltViewModel(),
    state: TaskFormState<*>
) {
    EditablePickerItem(
        label = "Duration",
        value = state.duration,
        valueLabel = "minutes",
        items = model.rangeDuration,
        itemsLabels = "minutes",
        onValueChange = { state.duration = it }
    )
}

@Composable
fun TaskFormFieldReminderTime(
    model: TaskViewModel = hiltViewModel(),
    state: TaskFormState<*>
) {
    EditablePickerItem(
        label = "Reminder Time",
        value = state.reminderTime,
        valueLabel = "minutes before task",
        items = model.rangeReminderTime,
        itemsLabels = "minutes",
        onValueChange = { state.reminderTime = it }
    )
}

@Composable
fun TaskFormFieldScheduledAt(state: TaskFormState<*>) {
    val supportsDuration = remember(
        (state as? TaskFormState.Sport)?.activity,
        state.duration
    ) {
        when (state) {
            is TaskFormState.Sport -> !state.activity.supportsDistanceMetrics
            else -> true
        }
    }

    EditableTimeItem(
        label = "Scheduled At",
        value = state.time ?: LocalTime.now(),
        valueDisplay = state.time?.asString(if (supportsDuration && state.duration?.isZero == false) state.duration else null),
        onValueChange = { state.time = it }
    )
}

@Composable
fun TaskFormFieldsMealBreakdown(meal: Meal?) {
    if (meal == null) {
        return
    }

    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            fontWeight = FontWeight.Bold,
            text = "Meal"
        )

        Text(text = meal.name)
    }

    FoldableContent(
        content = {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val ingredients = meal.recipe
                    .first().steps
                    .flatMap { it.ingredients }
                    .distinct()
                    .sorted()
                    .map { it.capitalize(Locale.current) }

                if (ingredients.size < 5) {
                    ingredients.forEach {
                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            text = it
                        )
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ingredients
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
        },
        darken = false,
        header = {
            Text(
                fontWeight = FontWeight.Bold,
                text = "Ingredients"
            )
        },
        isFoldedInitially = true,
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
    )

    FoldableContent(
        content = {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                meal.recipe
                    .first().steps
                    .sortedBy { it.number }
                    .forEach {
                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            text = it.step
                        )
                    }
            }
        },
        darken = false,
        header = {
            Text(
                fontWeight = FontWeight.Bold,
                text = "Recipe"
            )
        },
        isFoldedInitially = true,
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
    )
}

@Composable
fun TaskFormFieldMealCards(
    meals: List<Meal> = emptyList(),
    onChangeQuery: (String) -> Unit,
    query: String,
    state: TaskFormState.Meal
) {
    var clickEnabled by remember(state) { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val stateRow = rememberLazyListState()

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth(),
        state = stateRow,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (state.meal != null) {
            item {
                TaskFormMealCard(
                    buttonEnabled = false,
                    meal = state.meal!!,
                    state = state
                )
            }
        }

        if (meals.isEmpty()) {
            return@LazyRow
        }

        items(meals.filter { it.id != state.meal?.id }) {
            TaskFormMealCard(
                buttonEnabled = clickEnabled,
                meal = it,
                onClick = {
                    clickEnabled = false

                    state.meal = it
                    state.title = it.name

                    scope.launch {
                        stateRow.animateScrollToItem(0)

                        clickEnabled = true
                    }
                },
                state = state
            )
        }
    }

    if (meals.isEmpty()) {
        Text(
            style = MaterialTheme.typography.labelLarge,
            text = "No meals found"
        )
    }

    BasicTextField(
        decorationBox = { innerTextField ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    contentDescription = null,
                    imageVector = Icons.Outlined.Search,
                )

                Spacer(modifier = Modifier.size(8.dp))

                innerTextField()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp),
        onValueChange = onChangeQuery,
        singleLine = true,
        value = query,
    )
}

@Composable
fun TaskFormFieldTitle(state: TaskFormState<*>) {
    EditableTextItem(
        label = "Title",
        value = state.title ?: "",
        onValueChange = { state.title = it }
    )
}

@Composable
fun TaskFormFieldsBottomShared(state: TaskFormState<*>) {
    TaskFormFieldScheduledAt(state = state)

    TaskFormFieldDate(state = state)

    TaskFormFieldReminderTime(state = state)
}

@Composable
fun TaskFormFieldsTopShared(state: TaskFormState<*>) {
    TaskFormFieldTitle(state = state)

    TaskFormFieldDescription(state = state)
}

@Composable
private fun TaskFormMealCard(
    buttonEnabled: Boolean,
    meal: Meal,
    onClick: () -> Unit = { },
    state: TaskFormState.Meal
) {
    MealCard(
        buttonContent = {
            if (state.meal != meal) {
                Text(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge,
                    text = "Select"
                )
            } else {
                Icon(
                    contentDescription = "Meal selected icon",
                    imageVector = Icons.Outlined.Check,
                    modifier = Modifier
                        .size(24.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = MaterialTheme.shapes.medium
                        ),
                    tint = MaterialTheme.colorScheme.onTertiary
                )
            }
        },
        buttonEnabled = buttonEnabled,
        meal = meal,
        modifier = Modifier
            .size(
                height = 200.dp,
                width = (LocalView.current.width / 3 * 2).pixelsToDp()
            )
            .shadow(
                elevation = 4.dp,
                shape = MaterialTheme.shapes.medium
            )
            .then(
                if (state.meal != meal) Modifier else Modifier.border(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = MaterialTheme.shapes.medium,
                    width = 3.dp
                )
            ),
        onClick = onClick
    )
}

@Composable
fun TaskFormStateGeneralValidator(state: TaskFormState<*>) {
    LaunchedEffect(state.title) {
        state.isFormValid = state.title?.isNotBlank() ?: false
    }
}

@Composable
fun TaskFormStateCustomMealValidator(state: TaskFormState.CustomMeal) {
    LaunchedEffect(
        state.recipe,
        state.title
    ) {
        state.isFormValid = state.recipe.isNotBlank() && (state.title?.isNotBlank() ?: false)
    }
}

@Composable
fun TaskFormStateMealValidator(state: TaskFormState.Meal) {
    LaunchedEffect(state.meal) {
        state.isFormValid = state.meal != null
    }
}