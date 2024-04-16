package com.pvp.app.model

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

sealed class Setting<T>(
    val key: Preferences.Key<T>,
    val defaultValue: T
) {

    object Notifications {

        /**
         * Minutes for task reminder notification to execute before [Task.date] comes.
         *
         * If [Task.date] is at **12:30 pm** and this setting is set to **10**, user will get
         * reminder notification at **12:20 pm**.
         */
        data object ReminderBeforeTaskMinutes : Setting<Int>(
            intPreferencesKey("reminderBeforeTaskMinutes"),
            10
        )

        data object CupVolumeMl : Setting<Int>(
            intPreferencesKey("cupVolumeMl"),
            250
        )

        data object HydrationNotificationsEnabled : Setting<Boolean>(
            booleanPreferencesKey("HydrationNotificationsEnabled"),
            true
        )
    }

    data object ApplicationTheme : Setting<Int>(
        intPreferencesKey("applicationTheme"),
        1
    )
}