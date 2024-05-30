package com.pvp.app.service

import androidx.health.connect.client.records.ExerciseSessionRecord
import com.pvp.app.api.ExerciseService
import com.pvp.app.api.HealthConnectService
import com.pvp.app.common.ActivityUtil.getOccurrences
import com.pvp.app.model.ExerciseSessionInfo
import com.pvp.app.model.SportActivity
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlin.math.max

class ExerciseServiceImpl @Inject constructor(
    private val service: HealthConnectService
) : ExerciseService {

    override suspend fun calculateActivityLevel(): Double {
        val activityLevel = calculateActivityPoints()

        // Base level is calculated as activity level for people who walk ~1km every day for 30 days
        // this will equal multiplier of 1.
        val baseLevel = 7250.00

        val multiplier = 1 + (activityLevel / baseLevel - 1)

        return max(
            1.0,
            multiplier
        )
    }

    private suspend fun calculateActivityPoints(): Double {
        return getAllExerciseInfo()
            .mapNotNull { exercise ->
                val activity = SportActivity.fromId(exercise.record.exerciseType)

                if (activity.supportsDistanceMetrics) {
                    exercise.distance?.times(activity.pointsRatioDistance)
                } else {
                    exercise.duration?.seconds
                        ?.times(activity.pointsRatioDuration)
                        ?.toDouble()
                }
            }
            .sum()
    }

    private suspend fun getActivitiesWithOccurrencesMap(): Map<SportActivity, Int> {
        val end = Instant.now()

        val start = LocalDate
            .now()
            .minusDays(29)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        return service
            .readActivityData(
                record = ExerciseSessionRecord::class,
                start = start,
                end = end
            )
            .map { record ->
                SportActivity
                    .fromId(record.exerciseType)
            }
            .getOccurrences()
            .toMap()
    }

    private suspend fun getAllExerciseInfo(): List<ExerciseSessionInfo> {
        val end = Instant.now()

        val start = LocalDate
            .now()
            .minusDays(29)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        return service
            .readActivityData(
                record = ExerciseSessionRecord::class,
                start = start,
                end = end
            )
            .map { record ->
                getExerciseInfo(record)
            }
    }

    override suspend fun getExerciseInfo(record: ExerciseSessionRecord): ExerciseSessionInfo {
        return ExerciseSessionInfo(
            record = record,
            distance = when (SportActivity.fromId(record.exerciseType).supportsDistanceMetrics) {
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
            (
                    activityOccurrences[activity] == null ||
                            activityOccurrences[activity]!! <= maxOccurrence
                    ) &&
                    activity != SportActivity.Other
        }
    }

    override suspend fun getMostFrequentActivity(): SportActivity {
        val activitiesWithOccurrences = getActivitiesWithOccurrencesMap()
        return activitiesWithOccurrences
            .maxByOrNull { it.value }?.key ?: SportActivity.Walking
    }
}