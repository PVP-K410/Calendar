package com.pvp.app.api

import com.pvp.app.model.Task

interface PointService {

    /**
     * Calculate the points of a task. Points are determined by the task type and the attributes of
     * the task. User status can also be taken into account, hence, [Task.userEmail] is required.
     *
     * @param task task to calculate points for
     * @return points of the task
     */
    suspend fun calculate(
        task: Task
    ): Int

    /**
     * Calculate the total points of a list of tasks. Each task is calculated using [calculate].
     *
     * @param tasks list of tasks to calculate points for
     * @return total points of the tasks
     */
    suspend fun calculate(
        tasks: List<Task>
    ): Int {
        return tasks.sumOf { calculate(it) }
    }
}