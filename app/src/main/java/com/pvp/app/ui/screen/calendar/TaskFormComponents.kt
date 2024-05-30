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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.R
import com.pvp.app.common.TimeUtil.asString
import com.pvp.app.model.GoogleTask
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
import com.pvp.app.ui.common.InfoDateField
import com.pvp.app.ui.common.InfoTextField
import com.pvp.app.ui.common.TextError
import com.pvp.app.ui.common.pixelsToDp
import kotlinx.coroutines.launch
import java.time.LocalTime

@Composable
fun TaskFormButtonsRow(
    model: TaskViewModel = hiltViewModel(),
    onClose: () -> Unit,
    state: TaskFormState<*>
) {
    val showUpdate = state.task !is GoogleTask

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
            val localeDelete = stringResource(R.string.action_delete)
            val localeDeleteConfirmation =
                stringResource(R.string.input_field_task_delete_confirm_label)
            val localeDeleteDescription =
                stringResource(R.string.input_field_task_delete_confirm_description)

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
                            text = localeDelete
                        )
                    }
                },
                confirmationButtonContent = { Text(localeDelete) },
                confirmationDescription = { Text(localeDeleteDescription) },
                confirmationTitle = { Text(localeDeleteConfirmation) },
                onConfirm = {
                    if (state.task is GoogleTask) {
                        model.removeGoogle(state.task as GoogleTask)
                    } else {
                        model.remove(state.task!!)
                    }

                    onClose()
                },
                shape = MaterialTheme.shapes.extraLarge
            )
        }

        if (showUpdate) {
            val localeCreate = stringResource(R.string.action_create)
            val localeUpdate = stringResource(R.string.action_update)

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.surface
                ),
                enabled = state.isFormValid,
                onClick = {
                    model.mergeTaskFromState(state = state)

                    onClose()
                },
                shape = MaterialTheme.shapes.extraLarge
            ) { Text(if (state.task == null) localeCreate else localeUpdate) }
        }
    }
}

@Composable
fun TaskFormFieldActivity(state: TaskFormState.Sport) {
    val localeEditLabel = stringResource(R.string.input_field_activity_edit_label)
    val localeLabel = stringResource(R.string.input_field_activity_label)

    EditableSportActivityItem(
        editLabel = localeEditLabel,
        label = localeLabel,
        onValueChange = { state.activity = it },
        value = state.activity
    )
}

@Composable
fun TaskFormFieldDate(
    editable: Boolean,
    state: TaskFormState<*>
) {
    val localeLabel = stringResource(R.string.input_field_date_label)

    if (editable) {
        EditableDateItem(
            label = localeLabel,
            value = state.date,
            onValueChange = { state.date = it }
        )
    } else {
        InfoDateField(
            label = localeLabel,
            value = state.date
        )
    }
}

@Composable
fun TaskFormFieldDescription(
    editable: Boolean,
    state: TaskFormState<*>
) {
    val isRecipe = remember(state) { state is TaskFormState.CustomMeal }

    val localeEditLabel = if (isRecipe) {
        stringResource(R.string.input_field_recipe_edit_label)
    } else {
        stringResource(R.string.input_field_description_edit_label)
    }

    val localeError = if (isRecipe) {
        stringResource(R.string.input_field_recipe_error_cannot_be_empty)
    } else {
        ""
    }

    val localeLabel = if (isRecipe) {
        stringResource(R.string.input_field_recipe_label)
    } else {
        stringResource(R.string.input_field_description_label)
    }

    val description by remember(state) {
        derivedStateOf {
            when (state) {
                is TaskFormState.CustomMeal -> state.recipe
                is TaskFormState.General -> state.description
                is TaskFormState.Google -> state.description
                is TaskFormState.Sport -> state.description
                else -> null
            } ?: ""
        }
    }

    if (editable) {
        val onChange = remember<(String) -> Unit>(state) {
            {
                when (state) {
                    is TaskFormState.CustomMeal -> state.recipe = it
                    is TaskFormState.General -> state.description = it
                    is TaskFormState.Google -> Unit
                    is TaskFormState.Sport -> state.description = it
                    else -> Unit
                }
            }
        }

        EditableTextItem(
            editLabel = localeEditLabel,
            label = localeLabel,
            value = description,
            onValueChange = { onChange(it) },
            validate = {
                if (isRecipe) {
                    it.isNotBlank()
                } else {
                    true
                }
            },
            errorMessage = localeError
        )
    } else {
        if (description.isNotBlank()) {
            InfoTextField(
                label = localeLabel,
                value = description
            )
        }
    }
}

@Composable
fun TaskFormFieldDistance(
    model: TaskViewModel = hiltViewModel(),
    state: TaskFormState.Sport
) {
    val localeEditLabel = stringResource(R.string.input_field_distance_edit_label)
    val localeLabel = stringResource(R.string.input_field_distance_label)

    EditableDistanceItem(
        editLabel = localeEditLabel,
        label = localeLabel,
        onValueChange = { state.distance = it },
        rangeKilometers = model.rangeKilometers,
        rangeMeters = model.rangeMeters,
        value = state.distance
    )
}

@Composable
fun TaskFormFieldDuration(
    model: TaskViewModel = hiltViewModel(),
    state: TaskFormState<*>
) {
    val localeEditLabel = stringResource(R.string.input_field_duration_edit_label)
    val localeLabel = stringResource(R.string.input_field_duration_label)
    val localeMeasurementMinutes = stringResource(R.string.measurement_minutes)

    EditablePickerItem(
        editLabel = localeEditLabel,
        items = model.rangeDuration,
        itemsLabels = localeMeasurementMinutes,
        label = localeLabel,
        onValueChange = { state.duration = it },
        value = state.duration,
        valueLabel = localeMeasurementMinutes,
    )
}

@Composable
fun TaskFormFieldReminderTime(
    editable: Boolean,
    model: TaskViewModel = hiltViewModel(),
    state: TaskFormState<*>
) {
    val localeEditLabel = stringResource(R.string.input_field_reminder_time_edit_label)
    val localeLabel = stringResource(R.string.input_field_reminder_time_label)
    val localeMeasurementMinutes = stringResource(R.string.measurement_minutes)
    val localeResultLabel = stringResource(R.string.input_field_reminder_time_result_label)

    if (editable) {
        EditablePickerItem(
            editLabel = localeEditLabel,
            items = model.rangeReminderTime,
            itemsLabels = localeMeasurementMinutes,
            label = localeLabel,
            onValueChange = { state.reminderTime = it },
            value = state.reminderTime,
            valueLabel = localeResultLabel.format(state.reminderTime),
        )
    } else {
        if (state.reminderTime != null) {
            InfoTextField(
                label = localeLabel,
                value = localeResultLabel.format(state.reminderTime)
            )
        }
    }
}

@Composable
fun TaskFormFieldScheduledAt(
    editable: Boolean,
    state: TaskFormState<*>
) {
    val localeEditLabel = stringResource(R.string.input_field_scheduled_at_edit_label)
    val localeLabel = stringResource(R.string.input_field_scheduled_at_label)

    if (editable) {
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
            editLabel = localeEditLabel,
            label = localeLabel,
            onValueChange = { state.time = it },
            value = state.time ?: LocalTime.now(),
            valueDisplay = state.time?.asString(if (supportsDuration && state.duration?.isZero == false) state.duration else null),
        )
    } else {
        if (state.time != null) {
            InfoTextField(
                label = localeLabel,
                value = state.time!!.asString()
            )
        }
    }
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
        val localeLabel = stringResource(R.string.input_field_meals_label)

        Text(
            fontWeight = FontWeight.Bold,
            text = localeLabel
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
            val localeIngredients = stringResource(R.string.input_field_ingredients_label)

            Text(
                fontWeight = FontWeight.Bold,
                text = localeIngredients
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
            val localeRecipe = stringResource(R.string.input_field_recipe_label)

            Text(
                fontWeight = FontWeight.Bold,
                text = localeRecipe
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
        val localeNotFound = stringResource(R.string.input_field_meals_error_not_found)

        Text(
            style = MaterialTheme.typography.labelLarge,
            text = localeNotFound
        )
    }

    Column {
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
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
            value = query
        )

        val localeNotSelected = stringResource(R.string.input_field_meals_error_not_selected)

        TextError(
            enabled = state.meal == null,
            text = localeNotSelected
        )
    }
}

@Composable
fun TaskFormFieldTitle(
    editable: Boolean,
    state: TaskFormState<*>
) {
    val localeEditLabel = stringResource(R.string.input_field_title_edit_label)
    val localeLabel = stringResource(R.string.input_field_title_label)
    val localeError = stringResource(R.string.input_field_title_error_cannot_be_empty)

    if (editable) {
        EditableTextItem(
            editLabel = localeEditLabel,
            errorMessage = localeError,
            label = localeLabel,
            onValueChange = { state.title = it },
            validate = { it.isNotBlank() },
            value = state.title ?: "",
        )
    } else {
        InfoTextField(
            label = localeLabel,
            value = state.title ?: ""
        )
    }
}

@Composable
fun TaskFormFieldsBottomShared(
    editable: Boolean,
    state: TaskFormState<*>
) {
    TaskFormFieldScheduledAt(
        editable = editable,
        state = state
    )

    TaskFormFieldDate(
        editable = editable,
        state = state
    )

    TaskFormFieldReminderTime(
        editable = editable,
        state = state
    )
}

@Composable
fun TaskFormFieldsTopShared(
    editable: Boolean,
    state: TaskFormState<*>
) {
    TaskFormFieldTitle(
        editable = editable,
        state = state
    )

    TaskFormFieldDescription(
        editable = editable,
        state = state
    )
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
                val localeSelect = stringResource(R.string.action_select)

                Text(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge,
                    text = localeSelect
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