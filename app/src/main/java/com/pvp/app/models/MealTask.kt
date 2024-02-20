package com.pvp.app.models

import java.time.LocalDate
import java.time.LocalTime

class MealTask(
    date: LocalDate,
    startTime: LocalTime,
    var recipe: List<String>,
    var kCal: Int
) : Task(date, startTime)
