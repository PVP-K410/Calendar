package com.pvp.app.service

import com.google.firebase.Timestamp
import com.pvp.app.api.StreakService
import com.pvp.app.api.UserService
import com.pvp.app.model.Streak
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class StreakServiceImpl @Inject constructor(
    private val userService: UserService
) : StreakService {

    override suspend fun checkStreak(): Boolean {
        var needsReward = false
        val user = userService.user.firstOrNull() ?: return false

        val date = user.streak.incrementedAt
            .toDate()
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        var incrementedAt = user.streak.incrementedAt
        var value = user.streak.value

        when (date) {
            LocalDate.now() -> {
                if (value == 0) {
                    needsReward = true
                    value = 1
                }
            }

            LocalDate
                .now()
                .minusDays(1) -> {
                incrementedAt = Timestamp.now()
                needsReward = true
                value += 1
            }

            else -> {
                incrementedAt = Timestamp.now()
                value = 1
            }
        }

        if (value != user.streak.value || incrementedAt != user.streak.incrementedAt) {
            userService.merge(
                user.copy(
                    streak = Streak(
                        incrementedAt = incrementedAt,
                        value = value
                    )
                )
            )
        }

        return needsReward
    }
}