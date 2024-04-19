@file:OptIn(ExperimentalSerializationApi::class)

package com.pvp.app.model

import com.pvp.app.common.DurationSerializer
import com.pvp.app.common.LocalDateSerializer
import com.pvp.app.common.LocalTimeSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Serializable
class MealTask : Task {

    var recipe: String

    constructor(
        date: LocalDate,
        description: String? = null,
        duration: Duration? = null,
        reminderTime: Duration? = null,
        id: String? = null,
        isCompleted: Boolean,
        points: Points,
        recipe: String,
        time: LocalTime? = null,
        title: String,
        userEmail: String
    ) : super(
        date,
        description,
        duration,
        reminderTime,
        id,
        isCompleted,
        points,
        time,
        title,
        userEmail
    ) {
        this.recipe = recipe
    }

    override fun toString(): String {
        return "MealTask(recipe='$recipe') && " + super.toString()
    }

    companion object {

        fun copy(
            task: MealTask,
            date: LocalDate = task.date,
            description: String? = task.description,
            duration: Duration? = task.duration,
            id: String? = task.id,
            isCompleted: Boolean = task.isCompleted,
            points: Points = task.points,
            recipe: String = task.recipe,
            time: LocalTime? = task.time,
            title: String = task.title,
            userEmail: String = task.userEmail
        ): MealTask {
            return MealTask(
                date = date,
                description = description,
                duration = duration,
                id = id,
                isCompleted = isCompleted,
                points = points,
                recipe = recipe,
                time = time,
                title = title,
                userEmail = userEmail
            )
        }
    }
}

@Serializable
class SportTask : Task {

    var activity: SportActivity
    var distance: Double? = null
    var isDaily: Boolean = false

    constructor(
        activity: SportActivity = SportActivity.Other,
        date: LocalDate,
        description: String? = null,
        distance: Double? = null,
        duration: Duration? = null,
        reminderTime: Duration? = null,
        id: String? = null,
        isCompleted: Boolean,
        isDaily: Boolean,
        points: Points,
        time: LocalTime? = null,
        title: String,
        userEmail: String
    ) : super(
        date,
        description,
        duration,
        reminderTime,
        id,
        isCompleted,
        points,
        time,
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

    companion object {

        fun copy(
            task: SportTask,
            activity: SportActivity = task.activity,
            date: LocalDate = task.date,
            description: String? = task.description,
            distance: Double? = task.distance,
            duration: Duration? = task.duration,
            id: String? = task.id,
            isCompleted: Boolean = task.isCompleted,
            isDaily: Boolean = task.isDaily,
            points: Points = task.points,
            time: LocalTime? = task.time,
            title: String = task.title,
            userEmail: String = task.userEmail
        ): SportTask {
            return SportTask(
                activity = activity,
                date = date,
                description = description,
                distance = distance,
                duration = duration,
                id = id,
                isCompleted = isCompleted,
                isDaily = isDaily,
                points = points,
                time = time,
                title = title,
                userEmail = userEmail
            )
        }
    }
}

@Serializable
open class Task(
    @JsonNames("scheduledAt")
    @Serializable(LocalDateSerializer::class)
    var date: LocalDate,
    var description: String? = null,
    @Serializable(DurationSerializer::class)
    var duration: Duration? = null,
    @Serializable(DurationSerializer::class)
    var reminderTime: Duration? = null,
    val id: String? = null,
    var isCompleted: Boolean,
    var points: Points,
    @Serializable(LocalTimeSerializer::class)
    var time: LocalTime? = null,
    var title: String,
    val userEmail: String
) {

    override fun toString(): String {
        return "Task(date=$date, description=$description, duration=$duration, id=$id, isCompleted=$isCompleted, points=$points, time=$time, title='$title', userEmail='$userEmail')"
    }

    fun getNotification(): Notification? {
        if (reminderTime == null || time == null || isCompleted) {
            return null
        }

        val reminderMinutes = reminderTime!!.toMinutes()

        val reminderDateTime = date
            .atTime(time)
            .minusMinutes(reminderMinutes)

        if (reminderDateTime.isBefore(LocalDateTime.now())) {
            return null
        }

        return Notification(
            channel = NotificationChannel.TaskReminder,
            title = "Task Reminder",
            text = "Task '$title' is in $reminderMinutes minute" +
                    "${if (reminderMinutes > 1) "s" else ""}..."
        )
    }

    companion object {

        fun copy(
            task: Task,
            date: LocalDate = task.date,
            description: String? = task.description,
            duration: Duration? = task.duration,
            reminderTime: Duration? = task.reminderTime,
            id: String? = task.id,
            isCompleted: Boolean = task.isCompleted,
            points: Points = task.points,
            time: LocalTime? = task.time,
            title: String = task.title,
            userEmail: String = task.userEmail
        ): Task {
            return Task(
                date = date,
                description = description,
                duration = duration,
                reminderTime = reminderTime,
                id = id,
                isCompleted = isCompleted,
                points = points,
                time = time,
                title = title,
                userEmail = userEmail
            )
        }
    }
}