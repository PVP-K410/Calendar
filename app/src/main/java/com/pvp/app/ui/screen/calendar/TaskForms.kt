@file:OptIn(ExperimentalMaterial3Api::class)

package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.common.ObjectUtil.isNotNull
import com.pvp.app.common.ObjectUtil.isNull
import com.pvp.app.common.TimeUtil.asString
import com.pvp.app.model.CustomMealTask
import com.pvp.app.model.GeneralTask
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.common.ButtonConfirm
import com.pvp.app.ui.common.EditableDateItem
import com.pvp.app.ui.common.EditableDistanceItem
import com.pvp.app.ui.common.EditablePickerItem
import com.pvp.app.ui.common.EditableSportActivityItem
import com.pvp.app.ui.common.EditableTextItem
import com.pvp.app.ui.common.EditableTimeItem
import com.pvp.app.ui.common.TabSelector
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import kotlin.reflect.KClass

@Composable
private fun ButtonsRow(
    model: TaskViewModel = hiltViewModel(),
    onClose: () -> Unit,
    onDelete: (() -> Unit)? = null,
    state: TaskFormState<*>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 24.dp,
                vertical = 15.dp
            ),
        horizontalArrangement = if (state.task.isNull()) Arrangement.Center else Arrangement.SpaceBetween
    ) {
        if (state.task.isNotNull()) {
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
                onConfirm = { onDelete?.invoke() },
                shape = MaterialTheme.shapes.extraLarge
            )
        }

        Button(
            onClick = {
                mergeTask(
                    model = model,
                    state = state,
                )

                onClose()
            },
            enabled = state.isFormValid
        ) {
            Text(if (state.task.isNull()) "Create" else "Update")
        }
    }
}

@Composable
private fun TaskCustomMealForm(
    model: TaskViewModel = hiltViewModel(),
    state: TaskFormState.CustomMeal
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clip(MaterialTheme.shapes.medium)
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EditableTextItem(
            label = "Title",
            value = state.title ?: "",
            onValueChange = { state.title = it }
        )

        EditableTextItem(
            label = "Recipe",
            value = state.recipe ?: "",
            onValueChange = { state.recipe = it }
        )

        EditablePickerItem(
            label = "Duration",
            value = state.duration,
            valueLabel = "minutes",
            items = model.rangeDuration,
            itemsLabels = "minutes",
            onValueChange = { state.duration = it }
        )

        EditableTimeItem(
            label = "Scheduled At",
            value = state.time ?: LocalTime.now(),
            valueDisplay = state.time?.asString(state.duration),
            onValueChange = { state.time = it }
        )

        EditableDateItem(
            label = "Date",
            value = state.date,
            onValueChange = { state.date = it }
        )

        EditablePickerItem(
            label = "Reminder Time",
            value = state.reminderTime,
            valueLabel = "minutes before task",
            items = model.rangeReminderTime,
            itemsLabels = "minutes",
            onValueChange = { state.reminderTime = it }
        )
    }
}

@Composable
private fun TaskGeneralForm(
    model: TaskViewModel = hiltViewModel(),
    state: TaskFormState.General
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clip(MaterialTheme.shapes.medium)
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (state.task.isMeal() || state.task.isCustomMeal()) {
            var tab by remember { mutableIntStateOf(0) }

            TabSelector(
                onSelect = { tab = it },
                tabs = listOf(
                    "Custom",
                    "Predefined Recipes"
                )
            )
        }

        EditableTextItem(
            label = "Title",
            value = state.title ?: "",
            onValueChange = { state.title = it }
        )

        EditableTextItem(
            label = "Description",
            value = state.description ?: "",
            onValueChange = { state.description = it }
        )

        EditablePickerItem(
            label = "Duration",
            value = state.duration,
            valueLabel = "minutes",
            items = model.rangeDuration,
            itemsLabels = "minutes",
            onValueChange = { state.duration = it }
        )

        EditableTimeItem(
            label = "Scheduled At",
            value = state.time ?: LocalTime.now(),
            valueDisplay = state.time?.asString(state.duration),
            onValueChange = { state.time = it }
        )

        EditableDateItem(
            label = "Date",
            value = state.date,
            onValueChange = { state.date = it }
        )

        EditablePickerItem(
            label = "Reminder Time",
            value = state.reminderTime,
            valueLabel = "minutes before task",
            items = model.rangeReminderTime,
            itemsLabels = "minutes",
            onValueChange = { state.reminderTime = it }
        )
    }
}

@Composable
private fun TaskMealForm(
    model: TaskViewModel = hiltViewModel(),
    state: TaskFormState.Meal
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clip(MaterialTheme.shapes.medium)
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EditableTextItem(
            label = "Meal",
            value = state.meal?.name ?: "",
            // TODO: Implement meal fetching
            onValueChange = { state.meal = null }
        )

        EditableTimeItem(
            label = "Scheduled At",
            value = state.time ?: LocalTime.now(),
            valueDisplay = state.time?.asString(state.duration),
            onValueChange = { state.time = it }
        )

        EditableDateItem(
            label = "Date",
            value = state.date,
            onValueChange = { state.date = it }
        )

        EditablePickerItem(
            label = "Reminder Time",
            value = state.reminderTime,
            valueLabel = "minutes before task",
            items = model.rangeReminderTime,
            itemsLabels = "minutes",
            onValueChange = { state.reminderTime = it }
        )
    }
}

@Composable
private fun TaskSportForm(
    model: TaskViewModel = hiltViewModel(),
    state: TaskFormState.Sport
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clip(MaterialTheme.shapes.medium)
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EditableTextItem(
            label = "Title",
            value = state.title ?: "",
            onValueChange = { state.title = it }
        )

        EditableTextItem(
            label = "Description",
            value = state.description ?: "",
            onValueChange = { state.description = it }
        )

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
            EditablePickerItem(
                label = "Duration",
                value = state.duration,
                valueLabel = "minutes",
                items = model.rangeDuration,
                itemsLabels = "minutes",
                onValueChange = { state.duration = it }
            )
        }

        EditableTimeItem(
            label = "Scheduled At",
            value = state.time ?: LocalTime.now(),
            valueDisplay = state.time?.asString(if (!state.activity.supportsDistanceMetrics) state.duration else null),
            onValueChange = { state.time = it }
        )

        EditableDateItem(
            label = "Date",
            value = state.date,
            onValueChange = { state.date = it }
        )

        EditablePickerItem(
            label = "Reminder Time",
            value = state.reminderTime,
            valueLabel = "minutes before task",
            items = model.rangeReminderTime,
            itemsLabels = "minutes",
            onValueChange = { state.reminderTime = it }
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
        ) {
            val stateCustomMeal = rememberCustomMealFormState(date = date)
            val stateGeneral = rememberGeneralFormState(date = date)
            val stateMeal = rememberMealFormState(date = date)
            val stateSport = rememberSportFormState(date = date)

            var tab by remember { mutableStateOf(GeneralTask::class as KClass<out Task>) }
            var tabState: TaskFormState<*> = stateGeneral

            TabSelector(
                onSelect = {
                    when (it) {
                        1 -> {
                            tab = CustomMealTask::class
                            tabState = stateCustomMeal
                        }

                        2 -> {
                            tab = MealTask::class
                            tabState = stateMeal
                        }

                        3 -> {
                            tab = SportTask::class
                            tabState = stateSport
                        }

                        else -> {
                            tab = GeneralTask::class
                            tabState = stateGeneral
                        }
                    }
                },
                tabs = listOf(
                    "General",
                    "Meal",
                    "Meal (Recipe)",
                    "Sport"
                )
            )

            Spacer(modifier = Modifier.size(16.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.size(8.dp))

            StateValidator(state = tabState)

            when (tab) {
                CustomMealTask::class -> TaskCustomMealForm(state = stateCustomMeal)
                GeneralTask::class -> TaskGeneralForm(state = stateGeneral)
                MealTask::class -> TaskMealForm(state = stateMeal)
                SportTask::class -> TaskSportForm(state = stateSport)
            }

            ButtonsRow(
                onClose = onClose,
                state = tabState
            )
        }

        Spacer(modifier = Modifier.size(16.dp))
    }
}

@Composable
fun TaskEditSheet(
    isOpen: Boolean,
    model: TaskViewModel = hiltViewModel(),
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
        val state = when {
            task.isCustomMeal() -> rememberCustomMealFormState(
                date = task.date,
                task = task
            )

            task.isGeneral() -> rememberGeneralFormState(
                date = task.date,
                task = task
            )

            task.isSport() -> rememberSportFormState(
                date = task.date,
                task = task
            )

            else -> rememberMealFormState(
                date = task.date,
                task = task
            )
        }

        when (task) {
            is CustomMealTask -> TaskCustomMealForm(state = state as TaskFormState.CustomMeal)
            is GeneralTask -> TaskGeneralForm(state = state as TaskFormState.General)
            is MealTask -> TaskMealForm(state = state as TaskFormState.Meal)
            is SportTask -> TaskSportForm(state = state as TaskFormState.Sport)
        }

        ButtonsRow(
            onClose = onClose,
            onDelete = {
                model.remove(task)

                onClose()
            },
            state = state
        )

        Spacer(modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun StateValidator(state: TaskFormState<*>) {
    val isValid by remember { derivedStateOf { state.title?.isNotBlank() ?: false } }

    LaunchedEffect(isValid) {
        state.isFormValid = isValid
    }
}

private open class TaskFormState<T : Task>(
    date: LocalDate,
    duration: Duration? = null,
    isFormValid: Boolean = false,
    reminderTime: Duration? = null,
    task: T? = null,
    time: LocalTime? = null,
    title: String? = null
) {

    var date by mutableStateOf(date)
    var duration by mutableStateOf(duration)
    var isFormValid by mutableStateOf(isFormValid)
    var reminderTime by mutableStateOf(reminderTime)
    var task by mutableStateOf(task)
    var time by mutableStateOf(time)
    var title by mutableStateOf(title)

    constructor(state: TaskFormState<T>) : this(
        date = state.date,
        time = state.time,
        duration = state.duration,
        isFormValid = state.isFormValid,
        reminderTime = state.reminderTime,
        task = state.task,
        title = state.title
    )

    class CustomMeal(
        recipe: String? = null,
        stateParent: TaskFormState<CustomMealTask>
    ) : TaskFormState<CustomMealTask>(stateParent) {

        var recipe by mutableStateOf(recipe)
    }

    class General(
        description: String? = null,
        stateParent: TaskFormState<GeneralTask>
    ) : TaskFormState<GeneralTask>(stateParent) {

        var description by mutableStateOf(description)
    }

    class Meal(
        meal: com.pvp.app.model.Meal? = null,
        stateParent: TaskFormState<MealTask>
    ) : TaskFormState<MealTask>(stateParent) {

        var meal by mutableStateOf(meal)
    }

    class Sport(
        activity: SportActivity = SportActivity.Walking,
        description: String? = null,
        distance: Double? = null,
        stateParent: TaskFormState<SportTask>
    ) : TaskFormState<SportTask>(stateParent) {

        var activity by mutableStateOf(activity)
        var description by mutableStateOf(description)
        var distance by mutableStateOf(distance)
    }

    companion object {

        fun <T : Task> create(
            date: LocalDate? = null,
            task: T? = null
        ) = TaskFormState(
            date = task?.date ?: date ?: LocalDate.now(),
            duration = task?.duration,
            isFormValid = task.isNotNull(),
            reminderTime = task?.reminderTime,
            task = task,
            time = task?.time,
            title = task?.title
        )
    }
}

private fun Task?.isCustomMeal() = this is CustomMealTask
private fun Task?.isGeneral() = this is GeneralTask
private fun Task?.isSport() = this is SportTask
private fun Task?.isMeal() = this is MealTask

private fun mergeTask(
    model: TaskViewModel,
    state: TaskFormState<*>
) {
    state.title ?: error("Title is required. Cannot reach this state without title")

    if (state is TaskFormState.Meal) {
        state.meal?.id ?: error(
            "Meal is required for meal task. Cannot reach this state without meal"
        )
    }

    if (state.task.isNull()) {
        when (state) {
            is TaskFormState.CustomMeal -> model.create(
                date = state.date,
                duration = state.duration,
                recipe = state.recipe,
                reminderTime = state.reminderTime,
                time = state.time,
                title = state.title!!
            )

            is TaskFormState.General -> model.create(
                date = state.date,
                description = state.description,
                duration = state.duration,
                reminderTime = state.reminderTime,
                time = state.time,
                title = state.title!!
            )

            is TaskFormState.Meal -> model.create(
                date = state.date,
                duration = state.duration,
                mealId = state.meal!!.id,
                reminderTime = state.reminderTime,
                time = state.time,
                title = state.title!!
            )

            is TaskFormState.Sport -> model.create(
                date = state.date,
                activity = state.activity,
                description = state.description,
                distance = state.distance,
                duration = state.duration,
                reminderTime = state.reminderTime,
                time = state.time,
                title = state.title!!
            )
        }
    } else {
        model.update(
            { task ->
                when (state) {
                    is TaskFormState.CustomMeal -> CustomMealTask.copy(
                        task as CustomMealTask,
                        date = state.date,
                        duration = state.duration,
                        recipe = state.recipe,
                        reminderTime = state.reminderTime,
                        time = state.time,
                        title = state.title!!
                    )

                    is TaskFormState.General -> GeneralTask.copy(
                        task as GeneralTask,
                        date = state.date,
                        description = state.description,
                        duration = state.duration,
                        reminderTime = state.reminderTime,
                        time = state.time,
                        title = state.title!!
                    )

                    is TaskFormState.Meal -> MealTask.copy(
                        task as MealTask,
                        date = state.date,
                        duration = state.duration,
                        mealId = state.meal!!.id,
                        reminderTime = state.reminderTime,
                        time = state.time,
                        title = state.title!!
                    )

                    is TaskFormState.Sport -> SportTask.copy(
                        task as SportTask,
                        activity = state.activity,
                        date = state.date,
                        description = state.description,
                        distance = state.distance,
                        duration = state.duration,
                        reminderTime = state.reminderTime,
                        time = state.time,
                        title = state.title!!
                    )

                    else -> {
                        error("Unsupported task form state")
                    }
                }
            },
            state.task!!
        )
    }
}

@Composable
private fun rememberCustomMealFormState(
    date: LocalDate? = null,
    task: Task? = null
): TaskFormState.CustomMeal {
    val state by remember {
        mutableStateOf(
            TaskFormState.CustomMeal(
                recipe = (task as? CustomMealTask)?.recipe,
                stateParent = TaskFormState.create(
                    date,
                    task as? CustomMealTask
                )
            )
        )
    }

    return state
}

@Composable
private fun rememberGeneralFormState(
    date: LocalDate? = null,
    task: Task? = null
): TaskFormState.General {
    val state by remember {
        mutableStateOf(
            TaskFormState.General(
                description = (task as? GeneralTask)?.description,
                stateParent = TaskFormState.create(
                    date,
                    task as? GeneralTask
                )
            )
        )
    }

    return state
}

@Composable
private fun rememberMealFormState(
    date: LocalDate? = null,
    task: Task? = null
): TaskFormState.Meal {
    val state by remember {
        mutableStateOf(
            TaskFormState.Meal(
                // TODO: Implement meal fetching
                meal = null,
                stateParent = TaskFormState.create(
                    date,
                    task as? MealTask
                )
            )
        )
    }

    return state
}

@Composable
private fun rememberSportFormState(
    date: LocalDate? = null,
    task: Task? = null
): TaskFormState.Sport {
    val state by remember {
        mutableStateOf(
            TaskFormState.Sport(
                activity = (task as? SportTask)?.activity ?: SportActivity.Walking,
                description = (task as? SportTask)?.description,
                distance = (task as? SportTask)?.distance,
                stateParent = TaskFormState.create(
                    date,
                    task as? SportTask
                )
            )
        )
    }

    return state
}