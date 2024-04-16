package com.pvp.app.service

import android.util.Log
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

        userService.user
            .firstOrNull()
            ?.let { user ->

                val date = user.streak.incrementedAt
                    .toDate()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                var value = user.streak.value
                var incrementedAt = user.streak.incrementedAt

                when (date) {
                    LocalDate.now() -> {
                        if (value == 0){
                            value = 1
                            needsReward = true
                        }
                    }

                    LocalDate
                        .now()
                        .minusDays(1) -> {
                        value += 1
                        incrementedAt = Timestamp.now()
                        needsReward = true
                    }

                    else -> {
                        value = 1
                        incrementedAt = Timestamp.now()
                    }
                }

                if(value != user.streak.value || incrementedAt != user.streak.incrementedAt){
                    userService.merge(
                        user.copy(
                            streak = Streak(
                                value = value,
                                incrementedAt = incrementedAt
                            )
                        )
                    )
                }
            }

        return needsReward
    }
}