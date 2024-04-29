@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pvp.app.service

import com.pvp.app.api.Configuration
import com.pvp.app.api.PointService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.model.Goal
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapLatest
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.cosh
import kotlin.math.ln
import kotlin.random.Random

class PointServiceImpl @Inject constructor(
    private val configuration: Configuration,
    private val taskServiceProvider: Provider<TaskService>,
    private val userService: UserService
) : PointService {

    private val weeklyMultiplier = 2

    override suspend fun calculate(
        task: Task,
        increasePointYield: Boolean
    ): Int {
        val isDaily = task is SportTask && task.isDaily

        val points = mutableListOf<Int>()
            .apply {
                add(if (Random.nextFloat() <= 0.1) 2 else 1)

                when (task) {
                    is MealTask -> {
                        task.duration
                            ?.toMinutes()
                            ?.let {
                                add(
                                    calculateDurationPoints(
                                        duration = it,
                                        max = 3,
                                        ratio = 1f
                                    )
                                )
                            }
                    }

                    is SportTask -> {
                        if (task.activity.supportsDistanceMetrics) {
                            task.distance?.let { distance ->
                                add(
                                    calculateDistancePoints(
                                        distance = distance,
                                        ratio = task.activity.pointsRatioDistance,
                                        increasePointYield = increasePointYield
                                    )
                                )
                            }
                        } else {
                            task.duration
                                ?.toMinutes()
                                ?.let {
                                    add(
                                        calculateDurationPoints(
                                            duration = it,
                                            ratio = task.activity.pointsRatioDuration,
                                            increasePointYield = increasePointYield
                                        )
                                    )
                                }
                        }
                    }
                }
            }

        return points
            .sum()
            .let {
                if (isDaily) {
                    it * 2
                }

                it
            }
    }

    override suspend fun calculate(goal: Goal): Int {
        return when (goal.monthly) {
            false -> {
                calculateDistancePoints(
                    distance = goal.target / 7.0,
                    ratio = goal.activity.pointsRatioDistance
                ) * 7
            }

            true -> {
                calculateDistancePoints(
                    distance = goal.target / 30.0,
                    ratio = goal.activity.pointsRatioDistance
                ) * 30
            }
        }
    }

    private fun calculateDistancePoints(
        distance: Double,
        ratio: Float,
        increasePointYield: Boolean = false
    ): Int {
        if (distance == 0.0 || ratio == 0.0f) {
            return 0
        }

        val multiplier = if (increasePointYield) weeklyMultiplier else 1

        return multiplier * when (cosh(ln(distance * ratio))) {
            in 0.0..1.65 -> 1
            in 1.65..2.5 -> 2
            else -> 3
        }
    }

    private fun calculateDurationPoints(
        duration: Long,
        max: Int = 5,
        ratio: Float,
        increasePointYield: Boolean = false
    ): Int {
        if (duration == 0L || ratio == 0.0f) {
            return 0
        }

        val multiplier = if (increasePointYield) weeklyMultiplier else 1

        val result = multiplier * when ((duration / 60.0) * ratio) {
            in 0.0..0.75 -> 1
            in 0.75..1.5 -> 2
            in 1.5..2.8 -> 3
            in 2.8..4.2 -> 4
            else -> 5
        }

        return minOf(
            result,
            max
        )
    }

    override suspend fun deduct(
        date: LocalDate
    ) {
        val taskService = taskServiceProvider.get()
        val user = userService.user.firstOrNull() ?: error("User not found while deducting points")

        val tasks = taskService
            .get(user.email)
            .mapLatest {
                it
                    .filter { task -> !task.isCompleted }
                    .filter { task -> !task.points.isExpired }
                    .filter { task ->
                        task.date.year == date.year &&
                                task.date.monthValue == date.monthValue &&
                                task.date.dayOfMonth == date.dayOfMonth
                    }
            }
            .first()
            .map { it.markExpired() }

        tasks.forEach {
            taskService.update(it)
        }

        user.points -= minOf(
            tasks.size,
            configuration.limitPointsDeduction
        )

        userService.merge(user)
    }

    private fun Task.markExpired(): Task {
        return when (this) {
            is MealTask -> {
                MealTask.copy(
                    points = this.points.copy(
                        isExpired = true
                    ),
                    task = this
                )
            }

            is SportTask -> {
                SportTask.copy(
                    points = this.points.copy(
                        isExpired = true
                    ),
                    task = this
                )
            }

            else -> {
                Task.copy(
                    points = this.points.copy(
                        isExpired = true
                    ),
                    task = this
                )
            }
        }
    }
}