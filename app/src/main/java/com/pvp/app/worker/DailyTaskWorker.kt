package com.pvp.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pvp.app.api.Configuration
import com.pvp.app.api.NotificationService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.model.Notification
import com.pvp.app.model.NotificationChannel
import com.pvp.app.model.SportTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.time.LocalDate

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

    companion object {

        const val WORKER_NAME = "DailyTaskWorker"
    }

    override suspend fun doWork(): Result {
        return userService.user
            .firstOrNull()
            ?.let { user ->
                try {
                    val tomorrow = LocalDate
                        .now()
                        .plusDays(1)

                    val tasks = taskService
                        .get(userEmail = user.email)
                        .map { tasks ->
                            tasks.filter { task ->
                                task is SportTask &&
                                        task.isDaily &&
                                        task.scheduledAt
                                            .toLocalDate()
                                            .isBefore(tomorrow)
                            }
                        }
                        .first()

                    val today = LocalDate.now()

                    if (
                        tasks.filter {
                            it.scheduledAt
                                .toLocalDate()
                                .equals(today)
                        }.size >= configuration.dailyTaskCount
                    ) {
                        return Result.success()
                    }

                    tasks.forEach { task ->
                        taskService.remove(task)
                    }

                    taskService.generateDaily(
                        configuration.dailyTaskCount,
                        user.email
                    )

                    postNotification()

                    Result.success()
                } catch (e: Exception) {
                    e.printStackTrace()

                    Result.failure()
                }
            }
            ?: Result.failure()
    }

    private fun postNotification() {
        notificationService.show(
            Notification(
                channel = NotificationChannel.DailyTaskReminder,
                title = "Daily Task Reminder",
                text = "Your daily tasks have been created! Check them out in the app."
            )
        )
    }
}