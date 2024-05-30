package com.pvp.app.worker

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.pvp.app.api.SettingService
import com.pvp.app.api.TaskService
import com.pvp.app.model.Setting
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@HiltWorker
class GoogleCalendarSynchronizationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val settingService: SettingService,
    private val taskService: TaskService
) : CoroutineWorker(
    context,
    workerParams
) {

    override suspend fun doWork(): Result {
        val interval = settingService
            .get(Setting.ThirdPartyServices.GoogleCalendarSyncInterval)
            .firstOrNull() ?: Setting.ThirdPartyServices.GoogleCalendarSyncInterval.defaultValue

        if (interval == -1) {
            return Result.success()
        }

        val preferences = applicationContext.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )

        // TODO: Remove this block in PR
        preferences.edit {
            clear()

            commit()
        }

        val lastSync = preferences.getLong(
            LAST_SYNC_KEY,
            0
        )

        val difference = TimeUnit.HOURS.convert(
            System.currentTimeMillis() - lastSync,
            TimeUnit.MILLISECONDS
        )

        if (difference < interval) {
            return Result.success()
        }

        return try {
            preferences
                .edit()
                .putLong(
                    LAST_SYNC_KEY,
                    System.currentTimeMillis()
                )
                .apply()

            while (true) {
                try {
                    taskService.synchronizeGoogleCalendar(LocalDate.now())

                    break
                } catch (e: Exception) {
                    val intent = when (e) {
                        is UserRecoverableAuthIOException -> e.intent
                        else -> null
                    }

                    if (intent != null) {
                        applicationContext.startActivity(intent)
                    } else {
                        return Result.failure()
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(
                this::class.simpleName,
                "Failed to synchronize Google Calendar",
                e
            )

            Result.retry()
        }
    }

    companion object {

        const val LAST_SYNC_KEY = "last-sync"
        const val PREFERENCES_NAME = "google-calendar-synchronizer"
    }
}