package com.pvp.app.model

import com.google.firebase.Timestamp
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class User(
    var activities: List<SportActivity> = emptyList(),
    var decorationsApplied: List<String> = emptyList(),
    var decorationsOwned: List<String> = emptyList(),
    var diet: Diet = Diet.Carbohydrates,
    var hasDisability: Boolean = false,
    val email: String = "",
    var experience: Int = 0,
    var height: Int = 0,
    var ingredients: List<Ingredient> = emptyList(),
    @Contextual
    var lastActivitySync: Timestamp? = null,
    var lastMealPlanGeneratedWeek: Int = 0,
    var level: Int = 0,
    var mass: Int = 0,
    var points: Int = 0,
    val streak: Streak = Streak(),
    val surveys: List<Survey> = emptyList(),
    var username: String = "",
    var weeklyActivities: List<SportActivity> = emptyList()
)