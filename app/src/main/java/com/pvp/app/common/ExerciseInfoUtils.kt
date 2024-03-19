package com.pvp.app.common

import androidx.health.connect.client.records.ExerciseSessionRecord
import com.pvp.app.api.HealthConnectService
import com.pvp.app.model.ExerciseSessionInfo
import com.pvp.app.model.SportActivity
import java.time.Duration

object ExerciseInfoUtils {
    suspend fun getExerciseInfo(
        service: HealthConnectService,
        record: ExerciseSessionRecord
    ):
            ExerciseSessionInfo {
        return ExerciseSessionInfo(
            record = record,
            distance = when (SportActivity.fromId(record.exerciseType)?.supportsDistanceMetrics) {
                true -> service.aggregateDistance(record.startTime, record.endTime)
                else -> null
            },
            duration = Duration.between(record.startTime, record.endTime)
        )
    }

    suspend fun getExerciseInfo(
        service: HealthConnectService,
        records: List<ExerciseSessionRecord>
    ): List<ExerciseSessionInfo> {
        return records.map { getExerciseInfo(service, it) }
    }
}