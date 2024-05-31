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
import java.time.LocalDate

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

    override suspend fun doWork(): Result {
        val email = userService.user.first()?.email ?: return Result.retry()
        val now = LocalDate.now()

        taskService
            .get(email)
            .first()
            .filter { it.date >= now }
            .forEach { task ->
                notificationService
                    .getNotificationForTask(task)
                    ?.let { notification ->
                        notificationService.cancel(notification)

                        notificationService.post(notification)
                    }
            }

        return Result.success()
    }
}
