package com.pvp.app.ui.screen.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.TaskService
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskService: TaskService
) : ViewModel() {

    fun createTaskMeal(
        description: String? = null,
        duration: Duration? = null,
        recipe: String,
        scheduledAt: LocalDateTime,
        title: String,
        userEmail: String
    ): MealTask {
        val task = MealTask(
            description,
            duration,
            null,
            false,
            recipe,
            scheduledAt,
            title,
            userEmail
        )

        viewModelScope.launch {
            taskService.merge(task)
        }

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
        title: String,
        userEmail: String
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
            userEmail
        )

        viewModelScope.launch {
            taskService.merge(task)
        }

        return task
    }
}