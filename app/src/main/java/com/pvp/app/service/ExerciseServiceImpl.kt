package com.pvp.app.service

import androidx.health.connect.client.records.ExerciseSessionRecord
import com.pvp.app.api.ExerciseService
import com.pvp.app.api.HealthConnectService
import com.pvp.app.common.getOccurences
import com.pvp.app.model.ExerciseSessionInfo
import com.pvp.app.model.SportActivity
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class ExerciseServiceImpl @Inject constructor(
    private val service: HealthConnectService,
) : ExerciseService {

    private suspend fun getActivitiesWithOccurrencesMap(): Map<SportActivity, Int> {
        val end = Instant.now()

        val start = LocalDate
            .now()
            .minusDays(29)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        return service.readActivityData(
            record = ExerciseSessionRecord::class,
            start = start,
            end = end
        ).map { record ->
            SportActivity.fromId(record.exerciseType)
        }
            .getOccurences()
            .toMap()
    }

    override suspend fun getExerciseInfo(record: ExerciseSessionRecord): ExerciseSessionInfo {
        return ExerciseSessionInfo(
            record = record,
            distance = when (SportActivity.fromId(record.exerciseType)?.supportsDistanceMetrics) {
                true -> service.aggregateDistance(
                    record.startTime,
                    record.endTime
                )

                else -> null
            },
            duration = Duration.between(
                record.startTime,
                record.endTime
            )
        )
    }

    override suspend fun getInfrequentActivities(maxOccurrence: Int): List<SportActivity> {
        val activityOccurrences = getActivitiesWithOccurrencesMap()

        return SportActivity.entries.filter { activity ->
            (activityOccurrences[activity] == null
                    || activityOccurrences[activity]!! <= maxOccurrence)
                    && activity.id != 0
        }
    }
}