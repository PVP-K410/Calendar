package com.pvp.app.model

import com.pvp.app.common.DurationSerializer
import com.pvp.app.common.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.LocalDateTime

@Serializable
class MealTask : Task {

    companion object {

        fun copy(
            task: MealTask,
            description: String? = task.description,
            duration: Duration? = task.duration,
            id: String? = task.id,
            isCompleted: Boolean = task.isCompleted,
            points: Points = task.points,
            recipe: String = task.recipe,
            scheduledAt: LocalDateTime = task.scheduledAt,
            title: String = task.title,
            userEmail: String = task.userEmail
        ): MealTask {
            return MealTask(
                description = description,
                duration = duration,
                id = id,
                isCompleted = isCompleted,
                points = points,
                recipe = recipe,
                scheduledAt = scheduledAt,
                title = title,
                userEmail = userEmail
            )
        }
    }

    var recipe: String

    constructor(
        description: String? = null,
        duration: Duration? = null,
        id: String? = null,
        isCompleted: Boolean,
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

    companion object {

        fun copy(
            task: SportTask,
            activity: SportActivity = task.activity,
            description: String? = task.description,
            distance: Double? = task.distance,
            duration: Duration? = task.duration,
            id: String? = task.id,
            isCompleted: Boolean = task.isCompleted,
            isDaily: Boolean = task.isDaily,
            points: Points = task.points,
            scheduledAt: LocalDateTime = task.scheduledAt,
            title: String = task.title,
            userEmail: String = task.userEmail
        ): SportTask {
            return SportTask(
                activity = activity,
                description = description,
                distance = distance,
                duration = duration,
                id = id,
                isCompleted = isCompleted,
                isDaily = isDaily,
                points = points,
                scheduledAt = scheduledAt,
                title = title,
                userEmail = userEmail
            )
        }
    }

    var activity: SportActivity
    var distance: Double? = null
    var isDaily: Boolean = false

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
        points,
        scheduledAt,
        title,
        userEmail
    ) {
        this.activity = activity
        this.distance = distance
        this.isDaily = isDaily
    }

    override fun toString(): String {
        return "SportTask(activity=$activity, distance=$distance, isDaily=$isDaily) && " + super.toString()
    }
}

@Serializable
open class Task(
    var description: String? = null,
    @Serializable(DurationSerializer::class)
    var duration: Duration? = null,
    val id: String? = null,
    var isCompleted: Boolean,
    var points: Points,
    @Serializable(LocalDateTimeSerializer::class)
    var scheduledAt: LocalDateTime,
    var title: String,
    val userEmail: String
) {

    companion object {

        fun copy(
            task: Task,
            description: String? = task.description,
            duration: Duration? = task.duration,
            id: String? = task.id,
            isCompleted: Boolean = task.isCompleted,
            points: Points = task.points,
            scheduledAt: LocalDateTime = task.scheduledAt,
            title: String = task.title,
            userEmail: String = task.userEmail
        ): Task {
            return Task(
                description = description,
                duration = duration,
                id = id,
                isCompleted = isCompleted,
                points = points,
                scheduledAt = scheduledAt,
                title = title,
                userEmail = userEmail
            )
        }
    }

    override fun toString(): String {
        return "Task(description=$description, duration=$duration, id=$id, isCompleted=$isCompleted, points=$points, scheduledAt=$scheduledAt, title='$title', userEmail='$userEmail')"
    }
}