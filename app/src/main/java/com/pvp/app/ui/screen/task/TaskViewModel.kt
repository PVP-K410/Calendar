package com.pvp.app.ui.screen.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.NotificationService
import com.pvp.app.api.SettingService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.model.Notification
import com.pvp.app.model.Setting
import com.pvp.app.model.SportActivity
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

    /**
     * Create a meal task for the user with the given parameters
     */
    fun create(
        description: String? = null,
        duration: Duration? = null,
        ingredients: String,
        preparation: String,
        scheduledAt: LocalDateTime,
        title: String
    ) {
        viewModelScope.launch {
            state.collectLatest { state ->
                if (state.user.email.isBlank()) {
                    return@collectLatest
                }

                val recipe = if (
                    ingredients.isNotEmpty() &&
                    preparation.isNotEmpty()
                ) {
                    "$ingredients\n$preparation"
                } else if (ingredients.isNotEmpty() && preparation.isEmpty()) {
                    ingredients
                } else {
                    preparation
                }

                taskService
                    .create(
                        description,
                        duration,
                        recipe,
                        scheduledAt,
                        title,
                        state.user.email
                    )
                    .toNotification()
                    ?.also { notificationService.post(it) }
            }
        }
    }

    /**
     * Create a sport task for the user with the given parameters
     */
    fun create(
        activity: SportActivity,
        description: String? = null,
        distance: Double? = null,
        duration: Duration? = null,
        scheduledAt: LocalDateTime,
        title: String
    ) {
        viewModelScope.launch {
            state.collectLatest { state ->
                if (state.user.email.isBlank()) {
                    return@collectLatest
                }

                taskService
                    .create(
                        activity,
                        description,
                        distance,
                        duration,
                        scheduledAt,
                        title,
                        state.user.email
                    )
                    .toNotification()
                    ?.also { notificationService.post(it) }
            }
        }
    }

    /**
     * Create a general task for the user with the given parameters
     */
    fun create(
        description: String? = null,
        duration: Duration? = null,
        scheduledAt: LocalDateTime,
        title: String
    ) {
        viewModelScope.launch {
            state.collectLatest { state ->
                if (state.user.email.isBlank()) {
                    return@collectLatest
                }

                taskService
                    .create(
                        description,
                        duration,
                        scheduledAt,
                        title,
                        state.user.email
                    )
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

    fun update(
        task: Task
    ) {
        viewModelScope.launch {
            taskService.update(task)
        }
    }
}

data class TaskState(
    val reminderMinutes: Int = Setting.Notifications.ReminderBeforeTaskMinutes.defaultValue,
    val user: User = User()
)