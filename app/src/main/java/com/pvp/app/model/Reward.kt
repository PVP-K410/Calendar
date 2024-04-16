package com.pvp.app.model

import kotlinx.serialization.Serializable

@Serializable
data class Reward (
    var points: Int = -1,
    var experience: Int = -1,
    val decorationId: Int = -1
)