package com.pvp.app.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val email: String = "",
    var height: Int = 0,
    var mass: Int = 0,
    var points: Int = 0,
    var username: String = ""
)
