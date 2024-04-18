package com.pvp.app.api

interface StreakService {

    /**
     * Checks and updates streak information of the current user and
     * checks whether user needs streak reward
     * @return true if user needs a reward, false they don't
     */
    suspend fun checkStreak(): Boolean
}