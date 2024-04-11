package com.pvp.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Duration
import java.time.LocalTime

private const val PREFS_NAME = "CalendarPrefs"
private const val NOTIFICATION_IDS_KEY = "notification_ids"

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
    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var scheduledNotificationIds = mutableListOf<Int>()

    init {
        scheduledNotificationIds = getScheduledNotificationIds()
    }

    override suspend fun doWork(): Result {
        initializeReminderListener()

        return Result.success()
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
        val initialCount = recommendedIntake / cupVolume
        val remainder = recommendedIntake % cupVolume
        val count = if (remainder > 0) initialCount + 1 else initialCount
        val startHour = configuration.intervalDrinkReminder.first
        val endHour = configuration.intervalDrinkReminder.second
        val totalDuration = Duration.ofHours((endHour - startHour).toLong())
        val intervalDuration = totalDuration.dividedBy(count.toLong())
        var notificationTime = LocalTime.of(startHour, 0)

        repeat(count) {
            val currentCupVolume = if (it == initialCount && remainder > 0) remainder else cupVolume
            val cumulativeVolume = (it * cupVolume) + currentCupVolume

            val notification = Notification(
                channel = NotificationChannel.DrinkReminder,
                title = "Hydration Reminder 💦",
                text = "Time for a cup of water ($currentCupVolume ml)! 😋 " +
                        "Today's progress: " +
                        "${"%.1f".format(cumulativeVolume / 1000.0)}/" +
                        "${"%.1f".format(recommendedIntake / 1000.0)} liters"
            )

            scheduledNotificationIds.add(notification.id)

            notificationService.post(
                notification = notification,
                time = notificationTime
            )

            notificationTime = notificationTime.plus(intervalDuration)
        }

        saveScheduledNotificationIds()
    }

    private fun cancelScheduledNotifications() {
        for (notificationId in scheduledNotificationIds) {
            notificationService.cancel(notificationId)
        }

        scheduledNotificationIds.clear()
        saveScheduledNotificationIds()
    }

    private fun saveScheduledNotificationIds() {
        val notificationIdsJson = Gson().toJson(scheduledNotificationIds)

        sharedPreferences
            .edit()
            .putString(
                NOTIFICATION_IDS_KEY,
                notificationIdsJson
            ).apply()
    }

    private fun getScheduledNotificationIds(): MutableList<Int> {
        val ids = mutableListOf<Int>()

        val notificationIdsJson = sharedPreferences
            .getString(
                NOTIFICATION_IDS_KEY,
                null
            )

        notificationIdsJson?.let {
            ids.addAll(
                Gson().fromJson(
                    it,
                    object : TypeToken<List<Int>>() {}.type
                )
            )
        }

        return ids
    }
}

data class DrinkReminderState(
    val cupVolume: Int = Setting.Notifications.CupVolumeMl.defaultValue,
    val userMass: Int = 0
)