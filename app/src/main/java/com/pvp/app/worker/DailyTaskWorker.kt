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
import com.pvp.app.model.Points
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.math.roundToInt

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
        return userService.user
            .firstOrNull()
            ?.let { user ->
                val activities = user.activities

                val dailyTasks = generateDaily(
                    configuration.dailyTaskCount,
                    user.email,
                    activities
                )

                try {
                    userService.merge(
                        user.copy(
                            //dailyTasks = dailyTasks
                        )
                    )

                    Result.success()
                } catch (e: Exception) {
                    Result.failure()
                }
            } ?: Result.failure()
    }

    private fun generateDaily(
        count: Int,
        userEmail: String,
        activities: List<SportActivity>
    ): List<SportTask> {
        println("generateDaily called")
        val tasks = mutableListOf<SportTask>()

        repeat(count) {
            val activity = activities.random()
            val description = "One of your daily tasks for today"
            var distance: Double? = null

            if (activity.supportsDistanceMetrics) {
                distance = Random.nextDouble(
                    750.0,
                    1000.0
                ).roundToInt().toDouble()
            }

            val duration = null //Duration.ofMinutes(Random.nextLong(10, 30))
            val title = "Task ${it + 1}: $activity"
            val isCompleted = Random.nextBoolean()
            val points = Points(null)
            val scheduledAt = LocalDateTime.now().plusDays(1)

            val task = SportTask(
                activity = activity,
                description = description,
                distance = distance,
                duration = duration,
                isCompleted = isCompleted,
                isDaily = true,
                points = points,
                scheduledAt = scheduledAt,
                title = title,
                userEmail = userEmail
            )

            tasks.add(task)
        }

        println("TASKS")
        println(tasks)
        postNotification()

        return tasks
    }

    private fun postNotification() {
        notificationService.show(
            Notification(
                channel = NotificationChannel.DailyTaskReminder,
                title = "Daily task reminder",
                text = "Your daily tasks have been created!"
            )
        )
    }

    companion object {
        const val WORKER_NAME = "DailyTaskWorker"
    }
}