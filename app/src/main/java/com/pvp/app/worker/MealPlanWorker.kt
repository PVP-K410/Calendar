package com.pvp.app.worker

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pvp.app.R
import com.pvp.app.api.MealService
import com.pvp.app.api.NotificationService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.model.Meal
import com.pvp.app.model.Notification
import com.pvp.app.model.NotificationChannel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

@HiltWorker
class MealPlanWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val mealService: MealService,
    private val notificationService: NotificationService,
    private val taskService: TaskService,
    private val userService: UserService
) : CoroutineWorker(
    context,
    workerParams
) {

    override suspend fun doWork(): Result {
        val meals = mealService.get().first()

        postActivityNotification()

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

    private fun postActivityNotification() {
        notificationService.show(
            Notification(
                channel = NotificationChannel.WeeklyMealPlan,
                title = applicationContext.getString(R.string.worker_meal_plan_notification_title),
                text = applicationContext.getString(R.string.worker_meal_plan_notification_description)
            )
        )
    }

    companion object {

        const val WORKER_NAME = "MealPlanWorker"
    }
}
