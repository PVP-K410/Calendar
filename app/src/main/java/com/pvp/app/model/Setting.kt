package com.pvp.app.model

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey

sealed class Setting<T>(
    val key: Preferences.Key<T>,
    val defaultValue: T
) {

    object Notifications {

        /**
         * Minutes for task reminder notification to execute before [Task.scheduledAt] comes.
         *
         * If [Task.scheduledAt] is at **12:30 pm** and this setting is set to **10**, user will get
         * reminder notification at **12:20 pm**.
         */
        data object ReminderBeforeTaskMinutes : Setting<Int>(
            intPreferencesKey("reminderBeforeTaskMinutes"),
            10
        )
    }
}