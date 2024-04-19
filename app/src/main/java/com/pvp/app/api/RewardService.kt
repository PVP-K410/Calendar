package com.pvp.app.api

import com.pvp.app.model.Reward

interface RewardService : DocumentsCollection {

    override val identifier: String
        get() = "rewards"

    /**
     * Checks what reward the user should get for their current streak
     *
     * @return reward for the current application user
     */
    suspend fun get(): Reward

    /**
     * Gives the specified reward to the current application user
     *
     * @param reward Reward that the user will receive
     */
    suspend fun rewardUser(reward: Reward)
}