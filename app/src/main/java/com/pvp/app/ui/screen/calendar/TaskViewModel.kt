@file:OptIn(FlowPreview::class)

package com.pvp.app.ui.screen.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.Configuration
import com.pvp.app.api.MealService
import com.pvp.app.api.NotificationService
import com.pvp.app.api.SettingService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.model.CustomMealTask
import com.pvp.app.model.GeneralTask
import com.pvp.app.model.MealTask
import com.pvp.app.model.Setting
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    configuration: Configuration,
    mealService: MealService,
    private val notificationService: NotificationService,
    settingService: SettingService,
    private val taskService: TaskService,
    userService: UserService
) : ViewModel() {

    private val _mealsQuery = MutableStateFlow("")
    val mealsQuery = _mealsQuery.asStateFlow()

    private val _mealsQuerying = MutableStateFlow(true)
    val mealsQuerying = _mealsQuerying.asStateFlow()

    val meals = mealService
        .get()
        .combine(
            _mealsQuery
                .debounce(750)
                .onEach { _mealsQuerying.value = true }
        ) { meals, query ->
            val mealsQueried = meals.filter { meal ->
                meal.name.contains(
                    query,
                    ignoreCase = true
                ) ||
                        meal.recipe.first().steps.any { step ->
                            step.ingredients.any { ingredient ->
                                ingredient.contains(
                                    query,
                                    ignoreCase = true
                                )
                            }
                        }
            }

            _mealsQuerying.value = false

            mealsQueried
        }
        .stateIn(
            scope = viewModelScope.plus(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    val rangeDuration = configuration.rangeDuration
    val rangeKilometers = configuration.rangeKilometers
    val rangeMeters = configuration.rangeMeters
    val rangeReminderTime = configuration.rangeReminderMinutes

    init {
        viewModelScope.launch(Dispatchers.IO) {
            mealService
                .get()
                .first()
                .let {
                    it
                        .filter { meal ->
                            if (meal.recipe.isEmpty()) {
                                Log.d(
                                    "TaskViewModel",
                                    "Meal ${meal.name} has no recipe. Deleting."
                                )

                                true
                            } else {
                                false
                            }
                        }
                        .forEach { meal ->
                            mealService.remove(meal)
                        }
                }
        }
    }

    private val state = settingService
        .get(Setting.Notifications.ReminderBeforeTaskMinutes)
        .combine(userService.user) { minutes, user ->
            TaskState(
                reminderMinutesSetting = minutes,
                user = user!!
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TaskState()
        )
        .filter {
            it.user.email.isNotBlank()
        }

    /**
     * Create a custom meal task for the user with the given parameters
     */
    private fun create(
        date: LocalDate,
        duration: Duration? = null,
        reminderTime: Duration? = null,
        recipe: String?,
        time: LocalTime? = null,
        title: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            state
                .first()
                .let { state ->
                    taskService
                        .create(
                            date,
                            duration,
                            reminderTime,
                            recipe,
                            time,
                            title,
                            state.user.email
                        )
                        .postNotification()
                }
        }
    }

    /**
     * Create a meal task for the user with the given parameters
     */
    private fun create(
        date: LocalDate,
        duration: Duration? = null,
        mealId: String,
        reminderTime: Duration? = null,
        time: LocalTime? = null,
        title: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            state
                .first()
                .let { state ->
                    taskService
                        .create(
                            date,
                            duration,
                            mealId,
                            reminderTime,
                            time,
                            title,
                            state.user.email
                        )
                        .postNotification()
                }
        }
    }

    /**
     * Create a sport task for the user with the given parameters
     */
    private fun create(
        date: LocalDate,
        activity: SportActivity,
        description: String? = null,
        distance: Double? = null,
        duration: Duration? = null,
        reminderTime: Duration? = null,
        time: LocalTime? = null,
        title: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            state
                .first()
                .let { state ->
                    taskService
                        .create(
                            activity,
                            date,
                            description,
                            distance,
                            duration,
                            reminderTime,
                            false,
                            time,
                            title,
                            state.user.email
                        )
                        .postNotification()
                }
        }
    }

    /**
     * Create a general task for the user with the given parameters
     */
    private fun create(
        date: LocalDate,
        description: String? = null,
        duration: Duration? = null,
        reminderTime: Duration? = null,
        time: LocalTime? = null,
        title: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            state
                .first()
                .let { state ->
                    taskService
                        .create(
                            date,
                            description,
                            duration,
                            reminderTime,
                            time,
                            title,
                            state.user.email
                        )
                        .postNotification()
                }
        }
    }

    fun mergeTaskFromState(state: TaskFormState<*>) {
        state.title ?: error("Title is required. Cannot reach this state without title")

        if (state is TaskFormState.Meal) {
            state.meal?.id ?: error(
                "Meal is required for meal task. Cannot reach this state without meal"
            )
        }

        if (state.task == null) {
            when (state) {
                is TaskFormState.CustomMeal -> create(
                    date = state.date,
                    duration = state.duration,
                    recipe = state.recipe,
                    reminderTime = state.reminderTime,
                    time = state.time,
                    title = state.title!!
                )

                is TaskFormState.General -> create(
                    date = state.date,
                    description = state.description,
                    duration = state.duration,
                    reminderTime = state.reminderTime,
                    time = state.time,
                    title = state.title!!
                )

                is TaskFormState.Meal -> create(
                    date = state.date,
                    duration = state.duration,
                    mealId = state.meal!!.id,
                    reminderTime = state.reminderTime,
                    time = state.time,
                    title = state.title!!
                )

                is TaskFormState.Sport -> create(
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
            update(
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

    fun onMealsQueryChange(query: String) {
        _mealsQuery.value = query
    }

    /**
     * Handles the provided task with a provided function block
     *
     * @param handle Function block to handle the task
     * @param task Task to handle
     *
     * @return Pair of the modified task and a boolean indicating if the task points should also
     * be updated
     */
    private fun <T : Task> resolve(
        handle: (T) -> T,
        task: T
    ): Pair<T, Boolean> {
        val taskNew: T
        val update: Boolean

        when (task) {
            is SportTask -> {
                taskNew = handle(task)

                with(taskNew as SportTask) {
                    update = activity != task.activity ||
                            distance != task.distance ||
                            duration != task.duration
                }
            }

            is CustomMealTask -> {
                taskNew = handle(task)

                update = taskNew.duration != task.duration
            }

            else -> {
                taskNew = handle(task)

                update = false
            }
        }

        return Pair(
            taskNew,
            update
        )
    }

    /**
     * Update the task with the provided handle function block
     *
     * @param handle Function block to handle the task
     * @param task Task to update
     */
    fun <T : Task> update(
        handle: (T) -> T,
        task: T
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            task.cancelNotification()

            val (taskModified, updatePoints) = resolve(
                handle,
                task
            )

            taskModified.id ?: error("Task ID is required. Cannot update task.")

            val taskUpdated = taskService.update(
                taskModified,
                updatePoints
            )

            taskUpdated.postNotification()

            if (taskUpdated.isCompleted && taskUpdated.points.claimedAt == null) {
                taskService.claim(taskUpdated)
            }
        }
    }

    fun remove(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            task.cancelNotification()

            taskService.remove(task)
        }
    }

    private suspend fun Task.postNotification() {
        notificationService
            .getNotificationForTask(this)
            ?.let { notification ->
                notificationService.post(notification = notification)
            }
    }

    private suspend fun Task.cancelNotification() {
        notificationService
            .getNotificationForTask(this)
            ?.let { notification ->
                notificationService.cancel(notification)
            }
    }
}

data class TaskState(
    val reminderMinutesSetting: Int = Setting.Notifications.ReminderBeforeTaskMinutes.defaultValue,
    val user: User = User()
)