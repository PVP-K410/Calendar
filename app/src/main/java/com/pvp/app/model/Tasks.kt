@file:OptIn(ExperimentalSerializationApi::class)

@file:UseSerializers(
    LocalDateSerializer::class,
    LocalTimeSerializer::class,
    DurationSerializer::class
)

package com.pvp.app.model

import com.pvp.app.common.DurationSerializer
import com.pvp.app.common.LocalDateSerializer
import com.pvp.app.common.LocalTimeSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.JsonNames
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@Serializable
class CustomMealTask(
    override val date: LocalDate,
    override val duration: Duration? = null,
    override val id: String? = null,
    override val isCompleted: Boolean,
    override val points: Points,
    val recipe: String? = null,
    override val reminderTime: Duration? = null,
    override val time: LocalTime? = null,
    override val title: String,
    override val userEmail: String
) : Task() {

    override fun toString(): String {
        return "CustomMealTask(recipe=$recipe) && " + super.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (javaClass != other?.javaClass) {
            return false
        }

        if (!super.equals(other)) {
            return false
        }

        other as CustomMealTask

        return recipe == other.recipe
    }

    override fun hashCode(): Int {
        var result = super.hashCode()

        result = 31 * result + (recipe?.hashCode() ?: 0)

        return result
    }

    companion object {

        fun copy(
            task: CustomMealTask,
            date: LocalDate = task.date,
            duration: Duration? = task.duration,
            id: String? = task.id,
            isCompleted: Boolean = task.isCompleted,
            points: Points = task.points,
            recipe: String? = task.recipe,
            reminderTime: Duration? = task.reminderTime,
            time: LocalTime? = task.time,
            title: String = task.title,
            userEmail: String = task.userEmail
        ): CustomMealTask {
            return CustomMealTask(
                date = date,
                duration = duration,
                id = id,
                isCompleted = isCompleted,
                points = points,
                recipe = recipe,
                reminderTime = reminderTime,
                time = time,
                title = title,
                userEmail = userEmail
            )
        }
    }
}

@Serializable
class MealTask(
    override val date: LocalDate,
    override val duration: Duration? = null,
    override val id: String? = null,
    override val isCompleted: Boolean,
    val mealId: String,
    override val points: Points,
    override val reminderTime: Duration? = null,
    override val time: LocalTime? = null,
    override val title: String,
    override val userEmail: String
) : Task() {

    override fun toString(): String {
        return "MealTask(mealId='$mealId') && " + super.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (javaClass != other?.javaClass) {
            return false
        }

        if (!super.equals(other)) {
            return false
        }

        other as MealTask

        return mealId == other.mealId
    }

    override fun hashCode(): Int {
        var result = super.hashCode()

        result = 31 * result + mealId.hashCode()

        return result
    }

    companion object {

        fun copy(
            task: MealTask,
            date: LocalDate = task.date,
            duration: Duration? = task.duration,
            id: String? = task.id,
            isCompleted: Boolean = task.isCompleted,
            mealId: String = task.mealId,
            points: Points = task.points,
            reminderTime: Duration? = task.reminderTime,
            time: LocalTime? = task.time,
            title: String = task.title,
            userEmail: String = task.userEmail
        ): MealTask {
            return MealTask(
                date = date,
                duration = duration,
                id = id,
                isCompleted = isCompleted,
                mealId = mealId,
                points = points,
                reminderTime = reminderTime,
                time = time,
                title = title,
                userEmail = userEmail
            )
        }
    }
}

@Serializable
class SportTask(
    val activity: SportActivity = SportActivity.Other,
    override val date: LocalDate,
    val description: String? = null,
    val distance: Double? = null,
    override val duration: Duration? = null,
    override val id: String? = null,
    override val isCompleted: Boolean,
    val isDaily: Boolean = false,
    override val points: Points,
    override val reminderTime: Duration? = null,
    override val time: LocalTime? = null,
    override val title: String,
    override val userEmail: String
) : Task() {

    override fun toString(): String {
        return "SportTask(activity=$activity, description=$description, distance=$distance, isDaily=$isDaily) && " + super.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (javaClass != other?.javaClass) {
            return false
        }

        if (!super.equals(other)) {
            return false
        }

        other as SportTask

        return activity == other.activity &&
                description == other.description &&
                distance == other.distance &&
                isDaily == other.isDaily
    }

    override fun hashCode(): Int {
        var result = super.hashCode()

        result = 31 * result + activity.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (distance?.hashCode() ?: 0)
        result = 31 * result + isDaily.hashCode()

        return result
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
            reminderTime: Duration? = task.reminderTime,
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
                reminderTime = reminderTime,
                time = time,
                title = title,
                userEmail = userEmail
            )
        }
    }
}

@Serializable
class GeneralTask(
    override val date: LocalDate,
    val description: String?,
    override val duration: Duration? = null,
    override val id: String? = null,
    override val isCompleted: Boolean,
    override val points: Points,
    override val reminderTime: Duration? = null,
    override val time: LocalTime? = null,
    override val title: String,
    override val userEmail: String
) : Task() {

    override fun toString(): String {
        return "GeneralTask(description=$description) && " + super.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (javaClass != other?.javaClass) {
            return false
        }

        if (!super.equals(other)) {
            return false
        }

        other as GeneralTask

        return description == other.description
    }

    override fun hashCode(): Int {
        var result = super.hashCode()

        result = 31 * result + (description?.hashCode() ?: 0)

        return result
    }

    companion object {

        fun copy(
            task: GeneralTask,
            date: LocalDate = task.date,
            description: String? = task.description,
            duration: Duration? = task.duration,
            id: String? = task.id,
            isCompleted: Boolean = task.isCompleted,
            points: Points = task.points,
            reminderTime: Duration? = task.reminderTime,
            time: LocalTime? = task.time,
            title: String = task.title,
            userEmail: String = task.userEmail
        ): GeneralTask {
            return GeneralTask(
                date = date,
                description = description,
                duration = duration,
                id = id,
                isCompleted = isCompleted,
                points = points,
                reminderTime = reminderTime,
                time = time,
                title = title,
                userEmail = userEmail
            )
        }
    }
}

@Serializable
abstract class Task {

    @JsonNames("scheduledAt")
    abstract val date: LocalDate

    abstract val duration: Duration?
    abstract val id: String?
    abstract val isCompleted: Boolean
    abstract val points: Points
    abstract val reminderTime: Duration?
    abstract val time: LocalTime?
    abstract val title: String
    abstract val userEmail: String

    override fun toString(): String {
        return "Task(date=$date, duration=$duration, id=$id, isCompleted=$isCompleted, points=$points, time=$time, title='$title', userEmail='$userEmail')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (javaClass != other?.javaClass) {
            return false
        }

        other as Task

        return date == other.date &&
                duration == other.duration &&
                id == other.id &&
                isCompleted == other.isCompleted &&
                points == other.points &&
                reminderTime == other.reminderTime &&
                time == other.time &&
                title == other.title &&
                userEmail == other.userEmail
    }

    override fun hashCode(): Int {
        var result = date.hashCode()

        result = 31 * result + (duration?.hashCode() ?: 0)
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + isCompleted.hashCode()
        result = 31 * result + points.hashCode()
        result = 31 * result + (reminderTime?.hashCode() ?: 0)
        result = 31 * result + (time?.hashCode() ?: 0)
        result = 31 * result + title.hashCode()
        result = 31 * result + userEmail.hashCode()

        return result
    }

    companion object {

        fun copy(
            task: Task,
            date: LocalDate = task.date,
            duration: Duration? = task.duration,
            id: String? = task.id,
            isCompleted: Boolean = task.isCompleted,
            points: Points = task.points,
            reminderTime: Duration? = task.reminderTime,
            time: LocalTime? = task.time,
            title: String = task.title,
            userEmail: String = task.userEmail
        ): Task {
            return when (task) {
                is CustomMealTask -> CustomMealTask.copy(
                    task,
                    date = date,
                    duration = duration,
                    id = id,
                    isCompleted = isCompleted,
                    points = points,
                    reminderTime = reminderTime,
                    time = time,
                    title = title,
                    userEmail = userEmail
                )

                is MealTask -> MealTask.copy(
                    task,
                    date = date,
                    duration = duration,
                    id = id,
                    isCompleted = isCompleted,
                    points = points,
                    reminderTime = reminderTime,
                    time = time,
                    title = title,
                    userEmail = userEmail
                )

                is SportTask -> SportTask.copy(
                    task,
                    date = date,
                    duration = duration,
                    id = id,
                    isCompleted = isCompleted,
                    points = points,
                    reminderTime = reminderTime,
                    time = time,
                    title = title,
                    userEmail = userEmail
                )

                is GeneralTask -> GeneralTask.copy(
                    task,
                    date = date,
                    duration = duration,
                    id = id,
                    isCompleted = isCompleted,
                    points = points,
                    reminderTime = reminderTime,
                    time = time,
                    title = title,
                    userEmail = userEmail
                )

                else -> error("Unsupported task type for copy operation")
            }
        }
    }
}