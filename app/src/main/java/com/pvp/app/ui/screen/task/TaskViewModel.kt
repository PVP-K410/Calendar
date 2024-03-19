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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
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

    private val reminderMinutes = settingService
        .get(Setting.Notifications.ReminderBeforeTaskMinutes)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Setting.Notifications.ReminderBeforeTaskMinutes.defaultValue
        )

    private val user = userService.user.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    fun createTaskMeal(
        description: String? = null,
        duration: Duration? = null,
        recipe: String,
        scheduledAt: LocalDateTime,
        title: String
    ) {
        user.value?.run {
            val task = MealTask(
                description,
                duration,
                null,
                false,
                recipe,
                scheduledAt,
                title,
                email
            )

            viewModelScope.launch {
                taskService.merge(task)
            }

            task
                .toNotification()
                ?.also { notificationService.post(it) }
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
        user.value?.run {
            val task = SportTask(
                activity,
                description,
                distance,
                duration,
                id,
                isCompleted,
                scheduledAt,
                title,
                email
            )

            viewModelScope.launch {
                taskService.merge(task)
            }

            task
                .toNotification()
                ?.also { notificationService.post(it) }
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
        user.value?.run {
            val task = Task(
                description,
                duration,
                id,
                isCompleted,
                scheduledAt,
                title,
                email
            )

            viewModelScope.launch {
                taskService.merge(task)
            }

            task
                .toNotification()
                ?.also { notificationService.post(it) }
        }
    }

    private fun Task.toNotification(): Notification? {
        val difference = ChronoUnit.SECONDS.between(
            LocalDateTime.now(),
            scheduledAt
        )

        val reminderTime = difference - (reminderMinutes.value * 60)

        if (reminderTime <= 0) {
            return null
        }

        return Notification(
            delay = Duration.ofMinutes(reminderMinutes.value.toLong()),
            text = "Task '${title}' is in $reminderTime minutes!"
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