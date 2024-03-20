package com.pvp.app.api

import androidx.health.connect.client.records.ExerciseSessionRecord
import com.pvp.app.model.ExerciseSessionInfo

interface ExerciseService {
    /**
     * @param record ExerciseSessionRecord object
     * @return record object processed into an ExerciseSessionInfo object
     */
    suspend fun getExerciseInfo(record: ExerciseSessionRecord): ExerciseSessionInfo
}