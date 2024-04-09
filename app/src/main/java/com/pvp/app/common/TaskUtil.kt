package com.pvp.app.common

import com.pvp.app.model.SportTask
import com.pvp.app.model.Task

object TaskUtil {

    /**
     * Sorts the list of tasks by the following criteria:
     * 1. Completed status
     * 2. Scheduled date
     * 3. Activity (if the task is a sport task)
     * 4. Duration (if the task is a sport task)
     * 5. Distance (if the task is a sport task)
     */
    fun List<Task>.sort(): List<Task> {
        return sortedWith(
            compareBy<Task> { it.isCompleted }
                .thenBy { it.date }
                .thenBy { it.duration }
                .thenBy { (it as? SportTask)?.distance }
                .thenBy { (it as? SportTask)?.activity }
        )
    }
}