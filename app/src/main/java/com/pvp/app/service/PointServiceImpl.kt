package com.pvp.app.service

import com.pvp.app.api.PointService
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import javax.inject.Inject
import kotlin.random.Random

class PointServiceImpl @Inject constructor(

) : PointService {

    override suspend fun calculate(
        task: Task
    ): Int {
        val isDaily = task is SportTask && task.isDaily

        val points = mutableSetOf<Int>()
            .apply {
                add(if (Random.nextFloat() <= 0.1) 2 else 1)

                when (task) {
                    is MealTask -> {
                        task.duration?.let {
                            when (it.toMinutes()) {
                                in 0..30 -> add(1)
                                in 30..60 -> add(2)
                                else -> add(3)
                            }
                        }
                    }

                    is SportTask -> {
                        task.duration?.let {
                            when (it.toMinutes()) {
                                in 0..120 -> add(1)
                                in 120..240 -> add(2)
                                else -> add(3)
                            }
                        }

                        task.distance?.let {
                            when (it.toInt()) {
                                in 2000..5000 -> add(1)
                                in 5000..15000 -> add(2)
                                else -> add(3)
                            }
                        }
                    }
                }
            }

        return points
            .sum()
            .apply {
                if (isDaily) {
                    this * 2
                }
            }
    }
}