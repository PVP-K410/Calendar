package com.pvp.app.api

interface ExperienceService {

    /**
     * Calculates the required experience points for a given level.
     *
     * @param level level to calculate the experience points for.
     *
     * @return experience points for the given level to be reached
     */
    fun experienceOf(
        level: Int
    ): Int

    /**
     * Calculates the level for a given experience points.
     *
     * @param experience experience points to calculate the level for
     *
     * @return level for the given experience points
     */
    fun levelOf(
        experience: Int
    ): Int
}