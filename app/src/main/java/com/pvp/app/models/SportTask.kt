package com.pvp.app.models

import java.time.LocalDate
import java.time.LocalTime

class SportTask(
    date: LocalDate,
    startTime: LocalTime,
    val activity: SportActivity
) : Task(date, startTime)
