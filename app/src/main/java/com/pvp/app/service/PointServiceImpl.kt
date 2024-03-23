package com.pvp.app.service

import android.util.Log
import com.pvp.app.api.PointService
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import javax.inject.Inject
import kotlin.math.cosh
import kotlin.math.ln
import kotlin.random.Random

class PointServiceImpl @Inject constructor(

) : PointService {

    override suspend fun calculate(
        task: Task
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
                                        ratio = task.activity.pointsRatioDistance
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
                                            ratio = task.activity.pointsRatioDuration
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

    private fun calculateDistancePoints(
        distance: Double,
        ratio: Float
    ): Int {
        Log.d("PointServiceImpl", "distance: $distance, ratio: $ratio")

        if (distance == 0.0 || ratio == 0.0f) {
            return 0
        }

        return when (cosh(ln(distance * ratio))) {
            in 0.0..1.65 -> 1
            in 1.65..2.5 -> 2
            else -> 3
        }
    }

    private fun calculateDurationPoints(
        duration: Long,
        max: Int = 5,
        ratio: Float
    ): Int {
        if (duration == 0L || ratio == 0.0f) {
            return 0
        }

        val result = when ((duration / 60.0) * ratio) {
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
}