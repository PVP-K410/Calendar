package com.pvp.app.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.pvp.app.R
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

    private val preferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val scope = CoroutineScope(Dispatchers.IO)
    private val scheduledNotificationIds = getScheduledNotificationIds()

    override suspend fun doWork(): Result {
        return try {
            initializeReminderListener()

            Result.success()
        } catch (e: Exception) {
            Log.e(
                WORKER_NAME,
                "Failed to initialize drink reminder listener. Retrying...",
                e
            )

            Result.retry()
        }
    }

    private suspend fun initializeReminderListener(): Result {
        settingService
            .get(Setting.Notifications.CupVolumeMl)
            .combine(settingService.get(Setting.Notifications.HydrationNotificationsEnabled)) { volume, isEnabled ->
                Pair(
                    volume,
                    isEnabled
                )
            }
            .combine(userService.user) { (volume, isEnabled), user ->
                DrinkReminderState(
                    volume,
                    isEnabled,
                    user?.mass ?: 0
                )
            }
            .stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = DrinkReminderState()
            )
            .collect { (volume, isEnabled, userMass) ->
                cancelScheduledNotifications()

                if (userMass != 0 && isEnabled) {
                    scheduleNotifications(
                        mass = userMass,
                        cupVolume = volume
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

        var notificationTime = LocalTime.of(
            startHour,
            0
        )

        repeat(count) {
            val currentCupVolume = if (it == initialCount && remainder > 0) remainder else cupVolume
            val cumulativeVolume = (it * cupVolume) + currentCupVolume

            val notification = Notification(
                channel = NotificationChannel.DrinkReminder,
                title = applicationContext.getString(R.string.worker_hydration_notification_title),
                text = applicationContext.getString(
                    R.string.worker_hydration_notification_description,
                    currentCupVolume,
                    cumulativeVolume / 1000.0,
                    recommendedIntake / 1000.0
                )
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
        val notificationIdsJson = Gson()
            .toJson(scheduledNotificationIds)

        preferences
            .edit()
            .putString(
                NOTIFICATION_IDS_KEY,
                notificationIdsJson
            )
            .apply()
    }

    private fun getScheduledNotificationIds(): MutableList<Int> {
        val ids = mutableListOf<Int>()

        preferences
            .getString(
                NOTIFICATION_IDS_KEY,
                null
            )
            ?.let {
                ids.addAll(
                    Gson()
                        .fromJson(
                            it,
                            object : TypeToken<List<Int>>() {}.type
                        )
                )
            }

        return ids
    }

    companion object {

        const val WORKER_NAME = "com.pvp.app.worker.DrinkReminderWorker"
    }
}

data class DrinkReminderState(
    val cupVolume: Int = Setting.Notifications.CupVolumeMl.defaultValue,
    val hydrationNotificationsEnabled: Boolean = Setting.Notifications.HydrationNotificationsEnabled.defaultValue,
    val userMass: Int = 0
)