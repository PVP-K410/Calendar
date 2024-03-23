package com.pvp.app.service

import com.pvp.app.BuildConfig
import com.pvp.app.api.Configuration
import javax.inject.Inject

class ConfigurationImpl @Inject constructor() : Configuration {

    override val googleOAuthClientId: String = BuildConfig.GOOGLE_OAUTH_CLIENT_ID
    override val limitPointsDeduction: Int = 5
    override val limitPointsReclaimDays: Int = 2

    override val rangeHeight: List<Int> = (10..300)
        .toList()

    override val rangeMass: List<Int> = (5..500)
        .toList()

    override val rangeReminderMinutes: List<Int> = (1..120)
        .toList()

    override val rangeCupVolume: List<Int> = (100..500)
        .toList()

    override val intervalDrinkReminder: Pair<Int, Int> = Pair(8, 22)
}