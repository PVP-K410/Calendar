package com.pvp.app.model

sealed class SportActivity(
    val supportsDistanceMetrics: Boolean,
    val title: String
) {

    companion object {
        fun fromTitle(title: String): SportActivity? {
            return when (title) {
                Cycling.title -> Cycling
                Gym.title -> Gym
                Running.title -> Running
                Swimming.title -> Swimming
                Walking.title -> Walking
                Yoga.title -> Yoga
                else -> null
            }
        }
    }

    data object Cycling : SportActivity(true, "Cycling")
    data object Gym : SportActivity(false, "Gym")
    data object Running : SportActivity(true, "Running")
    data object Swimming : SportActivity(true, "Swimming")
    data object Walking : SportActivity(true, "Walking")
    data object Yoga : SportActivity(false, "Yoga")
}
