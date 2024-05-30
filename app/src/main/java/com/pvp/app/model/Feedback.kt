package com.pvp.app.model

import com.google.firebase.Timestamp
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Feedback(
    val id: String = "",
    val bug: Boolean = false,
    val email: String = "",
    val description: String = "",
    val rating: Int = 0,
    @Contextual
    val date: Timestamp = Timestamp.now()
)