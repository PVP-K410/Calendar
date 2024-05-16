package com.pvp.app.model

import com.google.firebase.Timestamp
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ActivityEntry(
    var calories: Double = 0.0,
    @Contextual
    var date: Timestamp = Timestamp.now(),
    var distance: Double = 0.0,
    val email: String = "",
    var id: String? = null,
    var steps: Long = 0
)