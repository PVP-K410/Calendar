package com.pvp.app.model

import com.pvp.app.common.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Goal(
    val activity: SportActivity = SportActivity.Other,
    var completed: Boolean = false,
    @Serializable(LocalDateSerializer::class)
    val endDate: LocalDate,
    val email: String = "",
    val goal: Double = 0.0,
    val id: String = "",
    val monthly: Boolean = false,
    var points: Points,
    var progress: Double = 0.0,
    @Serializable(LocalDateSerializer::class)
    val startDate: LocalDate,
    val steps: Boolean = false
)
