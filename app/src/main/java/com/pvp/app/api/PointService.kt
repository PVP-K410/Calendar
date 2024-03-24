package com.pvp.app.api

import com.pvp.app.model.Task
import java.time.LocalDate

interface PointService {

    /**
     * Calculate the points of a task. Points are determined by the task type and the attributes of
     * the task. User status can also be taken into account, hence, [Task.userEmail] is required.
     *
     * @param task task to calculate points for
     *
     * @return points of the task
     */
    suspend fun calculate(
        task: Task
    ): Int

    /**
     * Calculate the total points of a list of tasks. Each task is calculated using [calculate].
     *
     * @param tasks list of tasks to calculate points for
     *
     * @return total points of the tasks
     */
    suspend fun calculate(
        tasks: List<Task>
    ): Int {
        return tasks.sumOf { calculate(it) }
    }

    /**
     * Deduct points from current user for not completing tasks.
     *
     * Points are deducted based on the date of tasks. In case there are no tasks for the given
     * date or points were already deducted, no points are deducted.
     *
     * @param date date to deduct points for
     */
    suspend fun deduct(
        date: LocalDate
    )
}