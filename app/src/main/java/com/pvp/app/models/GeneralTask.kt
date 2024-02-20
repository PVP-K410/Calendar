package com.pvp.app.models

import java.time.LocalDate
import java.time.LocalTime

class GeneralTask (
    date: LocalDate,
    startTime: LocalTime,
    val description: String
) : Task(date, startTime)