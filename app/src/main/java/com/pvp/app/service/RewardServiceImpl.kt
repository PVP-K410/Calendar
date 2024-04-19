package com.pvp.app.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.api.DecorationService
import com.pvp.app.api.ExperienceService
import com.pvp.app.api.RewardService
import com.pvp.app.api.UserService
import com.pvp.app.model.Reward
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.lang.Integer.min
import javax.inject.Inject

class RewardServiceImpl @Inject constructor(
    private val database: FirebaseFirestore,
    private val decorationService: DecorationService,
    private val experienceService: ExperienceService,
    private val userService: UserService
) : RewardService {

    private fun calculatePoints(streak: Int): Int {
        return 1 + 2 * (min(streak, 7) - 1)
    }

    private fun calculateExperience(streak: Int): Int {
        return 10 + 10 * (min(streak, 7) - 1)
    }

    override suspend fun get(): Reward {
        val user = userService.user.firstOrNull() ?: return Reward()
        val streak = user.streak.value

        val reward = database
            .collection(identifier)
            .document(user.streak.value.toString())
            .snapshots()
            .map { it.toObject(Reward::class.java) }
            .firstOrNull() ?: Reward()

        if (
            reward.decorationId != null &&
            !user.decorationsOwned.contains(reward.decorationId) &&
            !user.decorationsApplied.contains(reward.decorationId)
        ) {
            reward.decoration = decorationService
                .get(reward.decorationId)
                .firstOrNull()
        }

        reward.points += calculatePoints(streak)
        reward.experience += calculateExperience(streak)

        return reward
    }

    override suspend fun rewardUser(reward: Reward) {
        val user = userService.user.firstOrNull() ?: return
        val experience = user.experience + reward.experience

        userService.merge(
            user.copy(
                decorationsOwned = if (reward.decorationId != null
                    && reward.decoration != null
                ) {
                    user.decorationsOwned + reward.decorationId
                } else {
                    user.decorationsOwned
                },
                experience = experience,
                level = experienceService.levelOf(experience),
                points = user.points + reward.points
            )
        )
    }
}