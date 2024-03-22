package com.pvp.app.model

import com.pvp.app.common.DurationSerializer
import com.pvp.app.common.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.LocalDateTime

@Serializable
class MealTask : Task {

    var recipe: String

    constructor(
        description: String? = null,
        duration: Duration? = null,
        id: String? = null,
        isCompleted: Boolean,
        isDaily: Boolean,
        points: Points,
        recipe: String,
        scheduledAt: LocalDateTime,
        title: String,
        userEmail: String
    ) : super(
        description,
        duration,
        id,
        isCompleted,
        isDaily,
        points,
        scheduledAt,
        title,
        userEmail
    ) {
        this.recipe = recipe
    }

    override fun toString(): String {
        return "MealTask(recipe='$recipe') && " + super.toString()
    }
}

@Serializable
class SportTask : Task {

    var activity: SportActivity?
    var distance: Double?

    constructor(
        activity: SportActivity,
        description: String? = null,
        distance: Double? = null,
        duration: Duration? = null,
        id: String? = null,
        isCompleted: Boolean,
        isDaily: Boolean,
        points: Points,
        scheduledAt: LocalDateTime,
        title: String,
        userEmail: String
    ) : super(
        description,
        duration,
        id,
        isCompleted,
        isDaily,
        points,
        scheduledAt,
        title,
        userEmail
    ) {
        this.activity = activity
        this.distance = distance
    }

    override fun toString(): String {
        return "SportTask(activity=$activity, distance=$distance) && " + super.toString()
    }
}

@Serializable
open class Task(
    var description: String? = null,
    @Serializable(DurationSerializer::class)
    var duration: Duration? = null,
    val id: String? = null,
    var isCompleted: Boolean,
    var isDaily: Boolean,
    var points: Points,
    @Serializable(LocalDateTimeSerializer::class)
    var scheduledAt: LocalDateTime,
    var title: String,
    val userEmail: String
) {
    override fun toString(): String {
        return "Task(description=$description, duration=$duration, id=$id, isCompleted=$isCompleted, isDaily=$isDaily, points=$points, scheduledAt=$scheduledAt, title='$title', userEmail='$userEmail')"
    }
}