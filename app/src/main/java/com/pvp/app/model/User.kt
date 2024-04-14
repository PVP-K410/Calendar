package com.pvp.app.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    var activities: List<SportActivity> = emptyList(),
    val email: String = "",
    var experience: Int = 0,
    var height: Int = 0,
    var ingredients: List<Ingredient> = emptyList(),
    var level: Int = 0,
    var mass: Int = 0,
    var points: Int = 0,
    val surveys: List<Survey> = emptyList(),
    var username: String = "",
    var weeklyActivities: List<SportActivity> = emptyList(),
    var dailyTasks: List<SportTask> = emptyList(),
)
