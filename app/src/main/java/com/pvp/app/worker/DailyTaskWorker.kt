@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pvp.app.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pvp.app.R
import com.pvp.app.api.Configuration
import com.pvp.app.api.NotificationService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.model.Notification
import com.pvp.app.model.NotificationChannel
import com.pvp.app.model.SportTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapLatest
import java.time.LocalDate
import kotlin.math.max

@HiltWorker
class DailyTaskWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val taskService: TaskService,
    private val notificationService: NotificationService,
    private val userService: UserService,
    private val configuration: Configuration
) : CoroutineWorker(
    context,
    workerParams
) {

    override suspend fun doWork(): Result {
        val user = userService.user.firstOrNull() ?: return Result.retry()

        val now = LocalDate.now()
        val tomorrow = now.plusDays(1)

        val tasks = taskService
            .get(userEmail = user.email)
            .mapLatest { tasks ->
                tasks.filter { task ->
                    task is SportTask &&
                            task.isDaily &&
                            task.date.isBefore(tomorrow)
                }
            }
            .first()

        tasks
            .filter { it.date.isBefore(now) && !it.isCompleted }
            .forEach { task ->
                taskService.remove(task)
            }

        val dailyTaskCount = tasks.filter { it.date.isEqual(now) }.size

        if (dailyTaskCount >= configuration.dailyTaskCount) {
            return Result.success()
        }

        return try {
            taskService.generateDaily(
                max(
                    0,
                    configuration.dailyTaskCount - dailyTaskCount
                ),
                user.hasDisability,
                user.email
            )

            postNotification()

            Result.success()
        } catch (e: Exception) {
            Log.e(
                this::class.simpleName,
                "Failed to generate daily tasks for ${user.email}. Retrying...",
                e
            )

            Result.retry()
        }
    }

    private fun postNotification() {
        notificationService.show(
            Notification(
                channel = NotificationChannel.DailyTaskReminder,
                title = applicationContext.getString(R.string.worker_daily_notification_title),
                text = applicationContext.getString(R.string.worker_daily_notification_description)
            )
        )
    }
}