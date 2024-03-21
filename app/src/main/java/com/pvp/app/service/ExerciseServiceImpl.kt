package com.pvp.app.service

import androidx.health.connect.client.records.ExerciseSessionRecord
import com.pvp.app.api.ExerciseService
import com.pvp.app.api.HealthConnectService
import com.pvp.app.model.ExerciseSessionInfo
import com.pvp.app.model.SportActivity
import java.time.Duration
import javax.inject.Inject

class ExerciseServiceImpl @Inject constructor(
    private val service: HealthConnectService
) : ExerciseService {

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
}