package com.pvp.app.ui.screen.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.TaskService
import com.pvp.app.model.MealTask
import com.pvp.app.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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

    fun updateTask(
        task: Task
    ) {
        viewModelScope.launch {
            taskService.merge(task)
        }
    }
}