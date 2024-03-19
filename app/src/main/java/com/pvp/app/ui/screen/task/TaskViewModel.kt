package com.pvp.app.ui.screen.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.Application.Companion.appContext
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskService: TaskService,
    private val userService: UserService,
) : ViewModel() {

    private val user = MutableStateFlow<User?>(null)

    init {
        viewModelScope.launch {
            userService.user
                .map {
                    user.value = it
                }
                .launchIn(viewModelScope)
        }
    }

    fun createTaskMeal(
        description: String? = null,
        duration: Duration? = null,
        recipe: String,
        scheduledAt: LocalDateTime,
        title: String
    ): MealTask {
        val task = MealTask(
            description,
            duration,
            null,
            false,
            recipe,
            scheduledAt,
            title,
            user.value!!.email
        )

        viewModelScope.launch {
            taskService.merge(task)
        }

        task.scheduleReminder(appContext, 10)

        return task
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
    ): SportTask {
        val task = SportTask(
            activity,
            description,
            distance,
            duration,
            id,
            isCompleted,
            scheduledAt,
            title,
            user.value!!.email
        )

        viewModelScope.launch {
            taskService.merge(task)
        }

        task.scheduleReminder(appContext, 10)

        return task
    }

    fun createTask(
        description: String? = null,
        duration: Duration? = null,
        id: String? = null,
        isCompleted: Boolean,
        scheduledAt: LocalDateTime,
        title: String
    ): Task {
        val task = Task(
            description,
            duration,
            id,
            isCompleted,
            scheduledAt,
            title,
            user.value!!.email
        )

        viewModelScope.launch {
            taskService.merge(task)
        }

        task.scheduleReminder(appContext, 10)

        return task
    }

    fun updateTask(
        task: Task
    ) {
        viewModelScope.launch {
            taskService.merge(task)
        }
    }
}