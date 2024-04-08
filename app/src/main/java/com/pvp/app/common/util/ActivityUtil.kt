package com.pvp.app.common.util

import androidx.health.connect.client.records.ExerciseSessionRecord
import com.pvp.app.model.SportActivity

object ActivityUtil {

    /**
     * Converts a list of [ExerciseSessionRecord] to a list of [SportActivity] by mapping the
     * exercise type to the corresponding [SportActivity] id.
     */
    fun List<ExerciseSessionRecord>.toSportActivities(): List<SportActivity> {
        return map { SportActivity.fromId(it.exerciseType) }
    }

    /**
     * Returns a list of occurrences of each activity, where the first element of the pair is the activity
     * and the second element is the number of occurrences. The list is sorted by the number of occurrences
     * in descending order.
     */
    fun List<SportActivity>.getOccurrences(): List<Pair<SportActivity, Int>> {
        return groupingBy { it }
            .eachCount()
            .toList()
            .sortedByDescending { it.second }
    }
}