package com.pvp.app.ui.screen.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.NotificationService
import com.pvp.app.api.SettingService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.model.MealTask
import com.pvp.app.model.Notification
import com.pvp.app.model.Setting
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val notificationService: NotificationService,
    settingService: SettingService,
    private val taskService: TaskService,
    userService: UserService
) : ViewModel() {

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

    fun createTaskMeal(
        description: String? = null,
        duration: Duration? = null,
        recipe: String,
        scheduledAt: LocalDateTime,
        title: String
    ) {
        viewModelScope.launch {
            state.collectLatest { state ->
                if (state.user.email.isBlank()) {
                    return@collectLatest
                }

                val task = MealTask(
                    description,
                    duration,
                    null,
                    false,
                    recipe,
                    scheduledAt,
                    title,
                    state.user.email
                )

                taskService.merge(task)

                task
                    .toNotification()
                    ?.also { notificationService.post(it) }
            }
        }
    }

    fun createTaskSport(
        activity: SportActivity,
        description: String? = null,
        distance: Double? = null,
        duration: Duration? = null,
        id: String? = null,
        isCompleted: Boolean,
        scheduledAt: LocalDateTime,
        title: String
    ) {
        viewModelScope.launch {
            state.collectLatest { state ->
                if (state.user.email.isBlank()) {
                    return@collectLatest
                }

                val task = SportTask(
                    activity,
                    description,
                    distance,
                    duration,
                    id,
                    isCompleted,
                    scheduledAt,
                    title,
                    state.user.email
                )

                taskService.merge(task)

                task
                    .toNotification()
                    ?.also { notificationService.post(it) }
            }
        }
    }

    fun createTask(
        description: String? = null,
        duration: Duration? = null,
        id: String? = null,
        isCompleted: Boolean,
        scheduledAt: LocalDateTime,
        title: String
    ) {
        viewModelScope.launch {
            state.collectLatest { state ->
                if (state.user.email.isBlank()) {
                    return@collectLatest
                }

                val task = Task(
                    description,
                    duration,
                    id,
                    isCompleted,
                    scheduledAt,
                    title,
                    state.user.email
                )

                taskService.merge(task)

                task
                    .toNotification()
                    ?.also { notificationService.post(it) }
            }
        }
    }

    private suspend fun Task.toNotification(): Notification? {
        val difference = ChronoUnit.SECONDS.between(
            LocalDateTime.now(),
            scheduledAt
        )

        val reminderMinutes = state.first().reminderMinutes
        val secondsUntilRemind = difference - (reminderMinutes * 60)

        if (secondsUntilRemind <= 0) {
            return null
        }

        return Notification(
            delay = Duration.ofSeconds(secondsUntilRemind),
            text = "'${title}' is in $reminderMinutes minute(s)..."
        )
    }

    fun updateTask(
        task: Task
    ) {
        viewModelScope.launch {
            taskService.merge(task)
        }
    }
}

data class TaskState(
    val reminderMinutes: Int = Setting.Notifications.ReminderBeforeTaskMinutes.defaultValue,
    val user: User = User()
)