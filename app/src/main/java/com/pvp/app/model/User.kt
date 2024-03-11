package com.pvp.app.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    var activities: List<String> = emptyList(),
    val email: String = "",
    var height: Int = 0,
    var ingredients: List<String> = emptyList(),
    var mass: Int = 0,
    var points: Int = 0,
    var username: String = ""
)
