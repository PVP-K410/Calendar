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
     * Calculates the experience points required to reach the next level from a given experience.
     *
     * @param experience current experience points
     *
     * @return experience points required to reach the next level
     */
    fun experienceToNextLevel(
        experience: Int
    ): Int {
        return experienceOf(levelOf(experience) + 1) - experience
    }

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