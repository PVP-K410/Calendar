package com.pvp.app.model

import kotlinx.serialization.Serializable

@Serializable
data class Reward (
    val points: Int = -1,
    val experience: Int = -1,
    val decorationId: Int = -1
)