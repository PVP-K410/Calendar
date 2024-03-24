package com.pvp.app.api

import androidx.health.connect.client.records.ExerciseSessionRecord
import com.pvp.app.model.ExerciseSessionInfo
import com.pvp.app.model.SportActivity

interface ExerciseService {

    /**
     * @param record ExerciseSessionRecord object
     * @return record object processed into an ExerciseSessionInfo object
     */
    suspend fun getExerciseInfo(record: ExerciseSessionRecord): ExerciseSessionInfo

    /**
     * Reads user activity data from HealthConnect and finds infrequent activities
     * (activities that the user has not done in the last 30 days or has done only a few times)
     * @return a list of sport activities that the user does not participate in frequently
     */
    suspend fun getInfrequentActivities(): List<SportActivity>
}