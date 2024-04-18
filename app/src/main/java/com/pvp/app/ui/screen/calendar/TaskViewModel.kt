package com.pvp.app.ui.screen.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.Configuration
import com.pvp.app.api.NotificationService
import com.pvp.app.api.SettingService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.model.MealTask
import com.pvp.app.model.Notification
import com.pvp.app.model.NotificationChannel
import com.pvp.app.model.Setting
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    configuration: Configuration,
    private val notificationService: NotificationService,
    settingService: SettingService,
    private val taskService: TaskService,
    userService: UserService
) : ViewModel() {

    val rangeKilometers = configuration.rangeKilometers

    private val state = settingService
        .get(Setting.Notifications.ReminderBeforeTaskMinutes)
        .combine(userService.user) { minutes, user ->
            TaskState(
                reminderMinutes = minutes,
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
     * Create a meal task for the user with the given parameters
     */
    fun create(
        date: LocalDate,
        description: String? = null,
        duration: Duration? = null,
        reminderTime: Duration? = null,
        recipe: String,
        time: LocalTime? = null,
        title: String
    ) {
        viewModelScope.launch {
            state
                .first()
                .let { state ->
                    taskService
                        .create(
                            date,
                            description,
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
     * Create a sport task for the user with the given parameters
     */
    fun create(
        date: LocalDate,
        activity: SportActivity,
        description: String? = null,
        distance: Double? = null,
        duration: Duration? = null,
        reminderTime: Duration? = null,
        time: LocalTime? = null,
        title: String
    ) {
        viewModelScope.launch {
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
    fun create(
        date: LocalDate,
        description: String? = null,
        duration: Duration? = null,
        reminderTime: Duration? = null,
        time: LocalTime? = null,
        title: String
    ) {
        viewModelScope.launch {
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

    private suspend fun Task.postNotification() {
        time ?: return

        val reminderMinutes = state
            .first().reminderMinutes
            .toLong()

        val reminderDateTime = date
            .atTime(time!!)
            .minusMinutes(reminderMinutes)

        if (reminderDateTime.isBefore(LocalDateTime.now())) {
            return
        }

        val notification = Notification(
            channel = NotificationChannel.TaskReminder,
            title = "Task Reminder",
            text = "Task '${title}' is in $reminderMinutes minute" +
                    "${if (reminderMinutes > 1) "s" else ""}..."
        )

        notificationService.post(
            notification = notification,
            dateTime = reminderDateTime
        )
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
    @Suppress("UNCHECKED_CAST")
    private fun <T : Task> resolve(
        handle: (T) -> Unit,
        task: T
    ): Pair<T, Boolean> {
        val taskNew: T
        val update: Boolean

        when (task) {
            is SportTask -> {
                taskNew = SportTask.copy(task) as T

                handle(taskNew)

                with(taskNew as SportTask) {
                    update = activity != task.activity ||
                            distance != task.distance ||
                            duration != task.duration
                }
            }

            is MealTask -> {
                taskNew = MealTask.copy(task) as T

                handle(taskNew)

                update = taskNew.duration != task.duration
            }

            else -> {
                taskNew = Task.copy(task) as T

                handle(taskNew)

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
        handle: (T) -> Unit,
        task: T
    ) {
        viewModelScope.launch {
            val (taskModified, updatePoints) = resolve(
                handle,
                task
            )

            taskModified.id ?: error("Task ID is required. Cannot update task.")

            val taskUpdated = taskService.update(
                taskModified,
                updatePoints
            )

            if (taskUpdated.isCompleted && taskUpdated.points.claimedAt == null) {
                taskService.claim(taskUpdated)
            }
        }
    }

    fun remove(task: Task) {
        viewModelScope.launch {
            taskService.remove(task)
        }
    }
}

data class TaskState(
    val reminderMinutes: Int = Setting.Notifications.ReminderBeforeTaskMinutes.defaultValue,
    val user: User = User()
)