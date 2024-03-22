package com.pvp.app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.pvp.app.api.NotificationService
import com.pvp.app.api.SettingService
import com.pvp.app.api.UserService
import com.pvp.app.model.Notification
import com.pvp.app.model.NotificationChannel
import com.pvp.app.model.Setting
import com.pvp.app.model.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Duration
import java.time.LocalTime
import javax.inject.Inject

@AndroidEntryPoint
class WaterDrinkReminder : Service() {

    @Inject
    lateinit var notificationService: NotificationService

    @Inject
    lateinit var settingService: SettingService

    @Inject
    lateinit var userService: UserService

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        Log.e("WATEEEEEER", "YOOOOOOOOOOOOO")
        super.onCreate()
        setup()
    }

    private fun setup() {
        val state = settingService
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

        val mass = state.value.user.mass
        val cupVolume = state.value.cupVolume
        val recommendedIntake = mass * 3
        val nbOfReminders = recommendedIntake / cupVolume

        val startHour = 8
        val endHour = 22
        val totalDuration = Duration.ofHours(endHour.toLong() - startHour.toLong())
        val intervalDuration = totalDuration.dividedBy(nbOfReminders.toLong())

        var notificationTime = LocalTime.of(startHour, 0)

        repeat(nbOfReminders) {
            val relativeDuration = calculateDuration(notificationTime)

            notificationService.post(
                notification = Notification(
                    delay = relativeDuration,
                    channel = NotificationChannel.DrinkReminder,
                    title = "Water Drinking Reminder",
                    text = "It's time to drink a cup of water!"
                )
            )

            notificationTime = notificationTime.plus(intervalDuration)
        }
    }

    fun calculateDuration(notificationTime: LocalTime): Duration {
        val currentDateTime = LocalTime.now()
        val notificationDateTime =
            LocalTime.of(notificationTime.hour, notificationTime.minute, notificationTime.second)

        return if (notificationDateTime.isBefore(currentDateTime)) {
            Duration.between(currentDateTime, notificationDateTime.plusHours(24))
        } else {
            Duration.between(currentDateTime, notificationDateTime)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

data class DrinkReminderState(
    val cupVolume: Int = Setting.Notifications.CupVolumeMl.defaultValue,
    val user: User = User()
)