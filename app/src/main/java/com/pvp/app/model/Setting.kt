package com.pvp.app.model

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

sealed class Setting<T>(
    val key: Preferences.Key<T>,
    val defaultValue: T
) {

    object Appearance {

        /**
         * Application theme. Uses [Theme] entries order as a value. Default is [Theme.Light].
         */
        data object ApplicationTheme : Setting<Int>(
            intPreferencesKey("applicationTheme"),
            1
        )

        /**
         * Whether to enable dynamic theme color palette.
         */
        data object DynamicThemeEnabled : Setting<Boolean>(
            booleanPreferencesKey("DynamicThemeEnabled"),
            false
        )

        enum class Theme {
            Dark,
            Light,
            Auto
        }
    }

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

        /**
         * Size of the cup in milliliters.
         */
        data object CupVolumeMl : Setting<Int>(
            intPreferencesKey("cupVolumeMl"),
            250
        )

        /**
         * Whether to show hydration notifications.
         */
        data object HydrationNotificationsEnabled : Setting<Boolean>(
            booleanPreferencesKey("HydrationNotificationsEnabled"),
            true
        )
    }

    object ThirdPartyServices {

        /**
         * Interval in hours for google calendar synchronization.
         * Default is **-1** hours. Should be either of **-1, 6, 12, 24**.
         * **-1** means that synchronization is disabled.
         */
        data object GoogleCalendarSyncInterval : Setting<Int>(
            intPreferencesKey("GoogleCalendarSyncInterval"),
            -1
        )
    }
}