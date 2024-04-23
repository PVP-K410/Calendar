package com.pvp.app.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class ActivityEntry(
    @Contextual
    val date: Date = Date(),
    val email: String = "",
    var id: String? = null,
    var steps: Int = 0,
    var calories: Long = 0
)