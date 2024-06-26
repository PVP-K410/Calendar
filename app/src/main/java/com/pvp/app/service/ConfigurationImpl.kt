package com.pvp.app.service

import com.pvp.app.BuildConfig
import com.pvp.app.api.Configuration
import javax.inject.Inject

class ConfigurationImpl @Inject constructor() : Configuration {

    override val dailyTaskCount: Int = 3

    override val defaultDecorationIds: List<String> = listOf(
        "default-body",
        "default-face",
        "default-hands",
        "default-head",
        "default-leggings",
        "default-shoes"
    )

    override val googleOAuthClientId: String = BuildConfig.GOOGLE_OAUTH_CLIENT_ID
    override val limitPointsDeduction: Int = 5
    override val limitPointsReclaimDays: Int = 2
    override val rangeCupVolume: List<Int> = (100..1000 step 50).toList()
    override val rangeDuration: List<Int> = (0..300 step 5).toList()
    override val rangeHeight: List<Int> = (10..300).toList()
    override val rangeKilometers: List<Int> = (0..99).toList()
    override val rangeMeters: List<Int> = (0..1000 step 100).toList()
    override val rangeMass: List<Int> = (5..500).toList()
    override val rangeReminderMinutes: List<Int> = (0..120 step 5).toList()
    override val rangeStepsPerDayGoal: List<Int> = (5000..35000 step 2500).toList()
    override val intervalDrinkReminder: Pair<Int, Int> = Pair(8, 22)
    override val intervalUsernameLength: Pair<Int, Int> = Pair(3, 18)
}