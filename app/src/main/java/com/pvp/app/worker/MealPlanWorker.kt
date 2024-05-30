package com.pvp.app.worker

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

@HiltWorker
class MealPlanWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val taskService: TaskService,
    private val userService: UserService
) : CoroutineWorker(
    context,
    workerParams
) {

    override suspend fun doWork(): Result {
        val user = userService.user.firstOrNull()

        user ?: return Result.retry()

        val week = LocalDate
            .now()
            .get(
                WeekFields
                    .of(AppCompatDelegate.getApplicationLocales()[0] ?: Locale.getDefault())
                    .weekOfWeekBasedYear()
            )

        if (user.lastMealPlanGeneratedWeek >= week) {
            return Result.success()
        }

        taskService.generateMeal()

        user.lastMealPlanGeneratedWeek = week

        userService.merge(user)

        return Result.success()
    }

    companion object {

        const val WORKER_NAME = "MealPlanWorker"
    }
}
