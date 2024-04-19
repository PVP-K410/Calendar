package com.pvp.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pvp.app.api.NotificationService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class TaskNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationService: NotificationService,
    private val userService: UserService,
    private val taskService: TaskService
) : CoroutineWorker(
    context,
    workerParams
) {

    companion object {

        const val WORKER_NAME = "com.pvp.app.worker.TaskNotificationWorker"
    }

    override suspend fun doWork(): Result {
        val email = userService.user.first()?.email ?: return Result.failure()

        taskService
            .get(email)
            .first()
            .forEach { task ->
                notificationService
                    .getNotificationForTask(task)
                    ?.let { notification ->
                        val reminderDateTime = task.date
                            .atTime(task.time)
                            .minusMinutes(task.reminderTime!!.toMinutes())

                        notificationService.post(
                            notification = notification,
                            dateTime = reminderDateTime
                        )
                    }
            }


        return Result.success()
    }
}
