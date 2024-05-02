package com.pvp.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pvp.app.api.FriendService
import com.pvp.app.api.GoalService
import com.pvp.app.api.NotificationService
import com.pvp.app.api.UserService
import com.pvp.app.model.Notification
import com.pvp.app.model.NotificationChannel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.LocalDate

@HiltWorker
class GoalMotivationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationService: NotificationService,
    private val userService: UserService,
    private val goalService: GoalService,
    private val friendService: FriendService
) : CoroutineWorker(
    context,
    workerParams
) {

    companion object {

        const val WORKER_NAME = "com.pvp.app.worker.GoalMotivationWorker"
    }

    override suspend fun doWork(): Result {
        val email = userService.user.first()?.email ?: return Result.failure()

        goalService
            .get(email)
            .first()
            .forEachIndexed { index, goal ->
                if (goal.completed || goal.endDate.isBefore(LocalDate.now())) {
                    return@forEachIndexed
                }

                val weekOrMonth = if (goal.monthly) "month" else "week"

                val goalType = goal.activity
                    .toString()
                    .lowercase()

                val target = if (goal.steps) {
                    "${goal.target.toInt()} steps"
                } else {
                    "${goal.target} km"
                }

                val notification = Notification(
                    channel = NotificationChannel.GoalMotivation,
                    title = "🏆 One Step Closer to Success!",
                    text = "You have a $goalType goal to achieve" +
                            "this $weekOrMonth ($target). Keep going!"
                )

                notificationService.post(
                    notification = notification,
                    delay = Duration.ofMinutes((index + 1) * 30L)
                )
            }

        var hasActiveFriends = false

        run friends@{
            friendService
                .get(email)
                .first()
                ?.friends
                ?.forEach friendsForEach@{ friend ->
                    goalService
                        .get(friend.email)
                        .first()
                        .forEach goalsForEach@{ goal ->
                            if (goal.endDate.isBefore(LocalDate.now())) {
                                return@goalsForEach
                            }

                            if (goal.completed) {
                                hasActiveFriends = true
                                return@friends
                            }
                        }
                }
        }

        if (hasActiveFriends) {
            val notification = Notification(
                channel = NotificationChannel.GoalMotivation,
                title = "🚀 Keep Up with Your Active Buddies!",
                text = "Participate in goals to keep up with your friends!"
            )

            notificationService.post(
                notification = notification,
                delay = Duration.ofMinutes(10L)
            )
        }

        return Result.success()
    }
}