package com.pvp.app.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pvp.app.api.ExerciseService
import com.pvp.app.api.NotificationService
import com.pvp.app.api.UserService
import com.pvp.app.model.Notification
import com.pvp.app.model.NotificationChannel
import com.pvp.app.model.SportActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class WeeklyActivityWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val exerciseService: ExerciseService,
    private val notificationService: NotificationService,
    private val userService: UserService
) : CoroutineWorker(
    context,
    workerParams
) {

    companion object {

        const val WORKER_NAME = "WeeklyActivitiesWorker"
    }

    override suspend fun doWork(): Result {
        val activities = getRandomInfrequentActivities()
        postActivityNotification(activities)

        Log.e("WEEKLY", "WORKER")

        return userService.user
            .firstOrNull()
            ?.let { user ->
                try {
                    userService.merge(
                        user.copy(
                            increasedPointActivities = activities
                        )
                    )

                    Result.success()
                } catch (e: Exception) {
                    Result.failure()
                }
            } ?: Result.failure()
    }

    private suspend fun getRandomInfrequentActivities(count: Int = 4): List<SportActivity> {
        return exerciseService
            .getInfrequentActivities()
            .shuffled()
            .take(count)
    }

    private fun postActivityNotification(activities: List<SportActivity>) {
        val notification = Notification(
            channel = NotificationChannel.IncreasedPointActivityReminder,
            title = "Weekly Activities",
            text = "Participating in " +
                    activities.joinToString(separator = ", ") { it.title } +
                    " this week will give you more points! "
        )

        notificationService.show(notification)
    }
}