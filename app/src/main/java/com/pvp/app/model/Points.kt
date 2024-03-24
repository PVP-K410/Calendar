package com.pvp.app.model

import com.pvp.app.common.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Points(
    @Serializable(LocalDateTimeSerializer::class)
    val claimedAt: LocalDateTime? = null,
    val isExpired: Boolean = false,
    val value: Int = 0
)
