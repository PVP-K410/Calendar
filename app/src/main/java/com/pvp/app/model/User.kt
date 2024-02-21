package com.pvp.app.model

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val height: Int,
    val mass: Int,
    var points: Int
)
