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
import com.pvp.app.model.User
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime

@HiltWorker
class DrinkReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    val notificationService: NotificationService,
    val settingService: SettingService,
    val userService: UserService
) : CoroutineWorker(context, workerParams) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    companion object {
        const val WORKER_NAME = "com.pvp.app.service.DrinkReminderWorker"
    }

    override suspend fun doWork(): Result {
        Log.e("DrinkReminderWorker", "YOOOOOOOOOOOOO")

        scheduleNotifications()

        return Result.success()
    }

    private fun scheduleNotifications() {
        val stateFlow = settingService
            .get(Setting.Notifications.CupVolumeMl)
            .combine(userService.user) { volume, user ->
                DrinkReminderState(
                    cupVolume = volume,
                    user = user!!
                )
            }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = DrinkReminderState()
            )

        coroutineScope.launch {
            stateFlow.collect { state ->
                val mass = state.user.mass

                if (mass != 0) {
                    Log.e("DrinkReminderWorker mass", mass.toString())
                    val cupVolume = state.cupVolume
                    Log.e("DrinkReminderWorker cupVolume", cupVolume.toString())
                    val recommendedIntake = mass * 30
                    val nbOfReminders = recommendedIntake / cupVolume
                    Log.e("DrinkReminderWorker recommendedIntake", recommendedIntake.toString())

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
        }
    }
}

data class DrinkReminderState(
    val cupVolume: Int = Setting.Notifications.CupVolumeMl.defaultValue,
    val user: User = User()
)