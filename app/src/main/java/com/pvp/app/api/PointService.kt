package com.pvp.app.api

import com.pvp.app.model.Task

/**
 * TODO :: PLAN
 * - Tasks are created only in the TaskService
 *   - Tasks are updated only in the TaskService
 *   - TaskService#merge split into TaskService#create, TaskService#update and TaskService#claim
 * - TaskService#claim is used to claim points of the task
 * - If claimed, points are added to the user's total points
 *   - Multiple claims are not allowed. Check if claimed before claiming
 * - Tasks contain an attribute as a state:
 *   - claimedAt
 *   - points
 * - Points are calculated upon task creation
 *   - Points are recalculated upon task update (manually triggered)
 *   - Points are recalculated upon user status change (manually triggered)
 */

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