package com.pvp.app.models

import java.time.LocalDate
import java.time.LocalTime

open class Task(
    val date: LocalDate,
    var startTime: LocalTime
)