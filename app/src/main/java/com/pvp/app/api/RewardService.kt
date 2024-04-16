package com.pvp.app.api

import com.pvp.app.model.Reward

interface RewardService : DocumentsCollection {
    override val identifier: String
        get() = "rewards"

    /**
     * Checks what reward the user should get for their current streak
     * @return Returns the reward
     */
    suspend fun get(): Reward

    /**
     * Gives the specified reward to the user
     * @param reward Reward that the user will receive
     */
    suspend fun rewardUser(reward: Reward)
}