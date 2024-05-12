@file:OptIn(ExperimentalMaterial3Api::class)

package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.model.CustomMealTask
import com.pvp.app.model.GeneralTask
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import com.pvp.app.ui.common.EditableDistanceItem
import com.pvp.app.ui.common.EditableSportActivityItem
import com.pvp.app.ui.common.ProgressIndicator
import com.pvp.app.ui.common.TabSelector
import com.pvp.app.ui.screen.calendar.TaskFormState.Companion.rememberCustomMealFormState
import com.pvp.app.ui.screen.calendar.TaskFormState.Companion.rememberGeneralFormState
import com.pvp.app.ui.screen.calendar.TaskFormState.Companion.rememberMealFormState
import com.pvp.app.ui.screen.calendar.TaskFormState.Companion.rememberSportFormState
import java.time.LocalDate
import kotlin.reflect.KClass

@Composable
private fun TaskCustomMealForm(
    onClose: () -> Unit,
    state: TaskFormState.CustomMeal
) {
    TaskFormStateGeneralValidator(state = state)

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clip(MaterialTheme.shapes.medium)
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TaskFormFieldsTopShared(state = state)

        TaskFormFieldDuration(state = state)

        TaskFormFieldsBottomShared(state = state)

        TaskFormButtonsRow(
            onClose = onClose,
            state = state
        )
    }
}

@Composable
private fun TaskGeneralForm(
    onClose: () -> Unit,
    state: TaskFormState.General
) {
    TaskFormStateGeneralValidator(state = state)

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clip(MaterialTheme.shapes.medium)
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TaskFormFieldsTopShared(state = state)

        TaskFormFieldDuration(state = state)

        TaskFormFieldsBottomShared(state = state)

        TaskFormButtonsRow(
            onClose = onClose,
            state = state
        )
    }
}

@Composable
private fun TaskMealForm(
    model: TaskViewModel = hiltViewModel(),
    onClose: () -> Unit,
    state: TaskFormState.Meal
) {
    val query by model.mealsQuery.collectAsStateWithLifecycle()
    val querying by model.mealsQuerying.collectAsStateWithLifecycle()
    val meals by model.meals.collectAsStateWithLifecycle()

    val isValid by remember {
        derivedStateOf {
            state.meal != null
        }
    }

    LaunchedEffect(isValid) {
        state.isFormValid = isValid
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clip(MaterialTheme.shapes.medium)
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (querying) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) { ProgressIndicator(modifier = Modifier.size(200.dp)) }
        } else {
            TaskFormFieldMealCards(
                meals = meals,
                onChangeQuery = model::onMealsQueryChange,
                query = query,
                state = state
            )
        }

        TaskFormFieldsMealBreakdown(state.meal)

        TaskFormFieldsBottomShared(state = state)

        TaskFormButtonsRow(
            onClose = onClose,
            state = state
        )
    }
}

@Composable
private fun TaskSportForm(
    model: TaskViewModel = hiltViewModel(),
    onClose: () -> Unit,
    state: TaskFormState.Sport
) {
    TaskFormStateGeneralValidator(state = state)

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clip(MaterialTheme.shapes.medium)
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TaskFormFieldsTopShared(state = state)

        EditableSportActivityItem(
            label = "Activity",
            value = state.activity,
            onValueChange = { state.activity = it }
        )

        if (state.activity.supportsDistanceMetrics) {
            EditableDistanceItem(
                label = "Distance",
                value = state.distance,
                rangeKilometers = model.rangeKilometers,
                rangeMeters = model.rangeMeters,
                onValueChange = { state.distance = it }
            )
        } else {
            TaskFormFieldDuration(state = state)
        }

        TaskFormFieldsBottomShared(state = state)

        TaskFormButtonsRow(
            onClose = onClose,
            state = state
        )
    }
}

@Composable
fun TaskCreateSheet(
    date: LocalDate? = null,
    isOpen: Boolean,
    onClose: () -> Unit
) {
    if (!isOpen) {
        return
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = rememberModalBottomSheetState(true)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            val stateCustomMeal = rememberCustomMealFormState(date = date)
            val stateGeneral = rememberGeneralFormState(date = date)
            val stateMeal = rememberMealFormState(date = date)
            val stateSport = rememberSportFormState(date = date)
            var tab by remember { mutableStateOf(GeneralTask::class as KClass<out Task>) }

            TabSelector(
                onSelect = {
                    tab = when (it) {
                        1 -> CustomMealTask::class
                        2 -> MealTask::class
                        3 -> SportTask::class
                        else -> GeneralTask::class
                    }
                },
                tabs = listOf(
                    "General",
                    "Meal",
                    "Our Meals",
                    "Sport"
                )
            )

            Spacer(modifier = Modifier.size(16.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.size(8.dp))

            when (tab) {
                CustomMealTask::class -> TaskCustomMealForm(
                    onClose = onClose,
                    state = stateCustomMeal
                )

                GeneralTask::class -> TaskGeneralForm(
                    onClose = onClose,
                    state = stateGeneral
                )

                MealTask::class -> TaskMealForm(
                    onClose = onClose,
                    state = stateMeal
                )

                SportTask::class -> TaskSportForm(
                    onClose = onClose,
                    state = stateSport
                )
            }
        }

        Spacer(modifier = Modifier.size(16.dp))
    }
}

@Composable
fun TaskEditSheet(
    isOpen: Boolean,
    onClose: () -> Unit,
    task: Task
) {
    if (!isOpen) {
        return
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = rememberModalBottomSheetState(true)
    ) {
        val state = when (task) {
            is CustomMealTask -> rememberCustomMealFormState(
                date = task.date,
                task = task
            )

            is MealTask -> rememberMealFormState(
                date = task.date,
                task = task
            )

            is SportTask -> rememberSportFormState(
                date = task.date,
                task = task
            )

            else -> rememberGeneralFormState(
                date = task.date,
                task = task
            )
        }

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when (task) {
                is CustomMealTask -> TaskCustomMealForm(
                    onClose = onClose,
                    state = state as TaskFormState.CustomMeal
                )

                is GeneralTask -> TaskGeneralForm(
                    onClose = onClose,
                    state = state as TaskFormState.General
                )

                is MealTask -> TaskMealForm(
                    onClose = onClose,
                    state = state as TaskFormState.Meal
                )

                is SportTask -> TaskSportForm(
                    onClose = onClose,
                    state = state as TaskFormState.Sport
                )
            }
        }
    }
}