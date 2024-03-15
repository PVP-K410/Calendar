package com.pvp.app.model

import android.content.Context
import com.pvp.app.common.DurationSerializer
import com.pvp.app.common.LocalDateTimeSerializer
import com.pvp.app.service.scheduleNotification
import com.pvp.app.ui.screen.task.TaskViewModel
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random

@Serializable
class MealTask : Task {

    var recipe: String

    constructor(
        description: String? = null,
        duration: Duration? = null,
        id: String? = null,
        isCompleted: Boolean,
        recipe: String,
        scheduledAt: LocalDateTime,
        title: String,
        userEmail: String
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
        scheduledAt: LocalDateTime,
        title: String,
        userEmail: String
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
    @Serializable(LocalDateTimeSerializer::class)
    var scheduledAt: LocalDateTime,
    var title: String,
    val userEmail: String
) {
    override fun toString(): String {
        return "Task(description=$description, duration=$duration, id=$id, isCompleted=$isCompleted, scheduledAt=$scheduledAt, title='$title', userEmail='$userEmail')"
    }

    fun scheduleReminder(
        context: Context,
        minutesToReminder: Int)
    {
        val now = LocalDateTime.now()
        val timeDifference = ChronoUnit.SECONDS.between(
            now,
            scheduledAt
        )
        val reminderTime = timeDifference - minutesToReminder * 60

        if (reminderTime <= 0) {
            return
        }

        val text = "Task: '$title' is in ${minutesToReminder} minutes!"
        val notificationId = Random(System.currentTimeMillis()).nextInt()

        scheduleNotification(
            context,
            text,
            reminderTime.toInt(),
            notificationId
        )
    }
}