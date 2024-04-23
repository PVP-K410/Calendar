@file:OptIn(ExperimentalSerializationApi::class)

package com.pvp.app.model

import com.pvp.app.common.DurationSerializer
import com.pvp.app.common.LocalDateSerializer
import com.pvp.app.common.LocalTimeSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class ActivityEntry(
    var calories: Double = 0.0,
    @Serializable(LocalDateSerializer::class)
    var date: LocalDate,
    val email: String = "",
    var id: String? = null,
    var steps: Long = 0
)