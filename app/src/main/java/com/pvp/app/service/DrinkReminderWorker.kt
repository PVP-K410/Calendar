package com.pvp.app.service

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pvp.app.api.Configuration
import com.pvp.app.api.NotificationService
import com.pvp.app.api.SettingService
import com.pvp.app.api.UserService
import com.pvp.app.model.Notification
import com.pvp.app.model.NotificationChannel
import com.pvp.app.model.Setting
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Duration
import java.time.LocalTime
import kotlin.coroutines.cancellation.CancellationException

@HiltWorker
class DrinkReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationService: NotificationService,
    private val settingService: SettingService,
    private val userService: UserService,
    private val configuration: Configuration
) : CoroutineWorker(
    context,
    workerParams
) {

    companion object {

        const val WORKER_NAME = "com.pvp.app.service.DrinkReminderWorker"
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private val scheduledNotifications = mutableListOf<Notification>()

    override suspend fun doWork(): Result {
        return coroutineScope {
            val job = async {
                initializeReminderListener()
            }

            job.invokeOnCompletion { exception: Throwable? ->
                when (exception) {
                    is CancellationException -> {
                        Log.e("DrinkReminderWorker", "Cleanup on completion", exception)
                        cancelScheduledNotifications()
                    }

                    else -> {
                        cancelScheduledNotifications()
                    }
                }
            }

            job.await()
        }
    }


    private suspend fun initializeReminderListener(): Result {
        val stateFlow = settingService
            .get(Setting.Notifications.CupVolumeMl)
            .combine(userService.user) { volume, user ->
                DrinkReminderState(
                    cupVolume = volume,
                    userMass = user?.mass ?: 0
                )
            }
            .stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = DrinkReminderState()
            )

        stateFlow.collect { state ->
            if (state.userMass != 0) {
                cancelScheduledNotifications()

                scheduleNotifications(
                    mass = state.userMass,
                    cupVolume = state.cupVolume
                )
            }
        }
    }

    private fun scheduleNotifications(
        mass: Int,
        cupVolume: Int
    ) {
        val recommendedIntake = mass * 30
        val count = recommendedIntake / cupVolume
        val startHour = configuration.intervalDrinkReminder.first
        val endHour = configuration.intervalDrinkReminder.second
        val totalDuration = Duration.ofHours((endHour - startHour).toLong())
        val intervalDuration = totalDuration.dividedBy(count.toLong())
        var notificationTime = LocalTime.of(startHour, 0)

        repeat(count) {
            val progress = (it + 1) * cupVolume

            val notification = Notification(
                channel = NotificationChannel.DrinkReminder,
                title = "Hydration Reminder 💦",
                text = "Time for a cup of water ($cupVolume ml)! 😋 " +
                        "Today's progress: " +
                        "${"%.1f".format(progress / 1000.0)}/" +
                        "${"%.1f".format(recommendedIntake / 1000.0)} liters"
            )

            scheduledNotifications.add(notification)

            notificationService.post(
                notification = notification,
                time = notificationTime
            )

            notificationTime = notificationTime.plus(intervalDuration)
        }
    }

    private fun cancelScheduledNotifications() {
        for (notification in scheduledNotifications) {
            notificationService.cancel(notification)
        }

        scheduledNotifications.clear()
    }
}

data class DrinkReminderState(
    val cupVolume: Int = Setting.Notifications.CupVolumeMl.defaultValue,
    val userMass: Int = 0
)