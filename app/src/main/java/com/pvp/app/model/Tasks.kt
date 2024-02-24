@file:UseSerializers(
    DurationSerializer::class,
    LocalDateTimeSerializer::class,
    SportActivitySerializer::class
)

package com.pvp.app.model

import com.pvp.app.common.DurationSerializer
import com.pvp.app.common.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Duration
import java.time.LocalDateTime

@Serializable
class MealTask : Task {

    var recipe: String

    constructor(
        description: String? = null,
        duration: Duration? = null,
        id: String? = null,
        isCompleted: Boolean = false,
        recipe: String = "",
        scheduledAt: LocalDateTime = LocalDateTime.now(),
        title: String = "",
        userEmail: String = ""
    ) : super(
        description,
        duration,
        id,
        isCompleted,
        scheduledAt,
        title,
        userEmail
    ) {
        this.recipe = recipe
    }
}

@Serializable
class SportTask : Task {

    var activity: SportActivity?
    var distance: Double?

    constructor(
        activity: SportActivity? = null,
        description: String? = null,
        distance: Double? = null,
        duration: Duration? = null,
        id: String? = null,
        isCompleted: Boolean = false,
        scheduledAt: LocalDateTime = LocalDateTime.now(),
        title: String = "",
        userEmail: String = ""
    ) : super(
        description,
        duration,
        id,
        isCompleted,
        scheduledAt,
        title,
        userEmail
    ) {
        this.activity = activity
        this.distance = distance
    }
}

@Serializable
open class Task(
    var description: String? = null,
    var duration: Duration? = null,
    val id: String? = null,
    var isCompleted: Boolean = false,
    var scheduledAt: LocalDateTime = LocalDateTime.now(),
    var title: String = "",
    val userEmail: String = ""
)
