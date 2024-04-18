package com.pvp.app.model

import kotlinx.serialization.Serializable

@Serializable
data class Reward (
    var points: Int = 0,
    var experience: Int = 0,
    val decorationId: String? = null
)