package com.pvp.app.model

import androidx.health.connect.client.records.ExerciseSessionRecord
import java.time.Duration

data class ExerciseSessionInfo(
    val record: ExerciseSessionRecord,
    var distance: Double?,
    var duration: Duration?
)