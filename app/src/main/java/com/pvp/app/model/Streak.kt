package com.pvp.app.model

import com.google.firebase.Timestamp
import com.pvp.app.common.LocalDateSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
class Streak(
    var value: Int = 0,
    @Contextual
    var incrementedAt: Timestamp = Timestamp.now()
)