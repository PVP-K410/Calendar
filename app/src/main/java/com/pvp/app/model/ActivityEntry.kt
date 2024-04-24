package com.pvp.app.model

import com.pvp.app.common.LocalDateSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class ActivityEntry(
    var calories: Double = 0.0,
    @Serializable(LocalDateSerializer::class)
    var date: LocalDate,
    val email: String = "",
    var id: String? = null,
    var steps: Long = 0
)