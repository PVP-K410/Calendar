package com.pvp.app.service

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Duration
import java.time.LocalTime

@HiltWorker
class DrinkReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationService: NotificationService,
    private val settingService: SettingService,
    private val userService: UserService
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORKER_NAME = "com.pvp.app.service.DrinkReminderWorker"
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override suspend fun doWork(): Result {
        setup()

        return Result.success()
    }

    private suspend fun setup() {
        val stateFlow = settingService
            .get(Setting.Notifications.CupVolumeMl)
            .combine(userService.user) { volume, user ->
                DrinkReminderState(
                    cupVolume = volume,
                    userMass = user?.mass ?: 0
                )
            }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = DrinkReminderState()
            )

            stateFlow.collect { state ->
                if (state.userMass != 0) {
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
        Log.e("DrinkReminderWorker mass", mass.toString())
        Log.e("DrinkReminderWorker cupVolume", cupVolume.toString())

        val recommendedIntake = mass * 30
        Log.e("DrinkReminderWorker recommendedIntake", recommendedIntake.toString())

        val nbOfReminders = recommendedIntake / cupVolume
        Log.e("DrinkReminderWorker nbOfReminders", nbOfReminders.toString())

        val startHour = 8
        val endHour = 22
        val totalDuration = Duration.ofHours(endHour.toLong() - startHour.toLong())
        val intervalDuration = totalDuration.dividedBy(nbOfReminders.toLong())

        var notificationTime = LocalTime.of(startHour, 0)

        repeat(nbOfReminders) {
            notificationService.post(
                notification = Notification(
                    channel = NotificationChannel.DrinkReminder,
                    title = "Water Drinking Reminder",
                    text = "It's time to drink a cup of water!"
                ),
                time = notificationTime
            )

            notificationTime = notificationTime.plus(intervalDuration)
            Log.e("DrinkReminderWorker", notificationTime.toString())
        }
    }
}

data class DrinkReminderState(
    val cupVolume: Int = Setting.Notifications.CupVolumeMl.defaultValue,
    val userMass: Int = 0
)