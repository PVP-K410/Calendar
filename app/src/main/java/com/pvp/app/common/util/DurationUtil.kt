package com.pvp.app.common.util

import java.time.Duration

object DurationUtil {

    /**
     * Parses Duration object to a string
     * Format <HH> h <mm> min
     */
    fun Duration.asString(): String {
        val hours = toHours()

        val minutes = minusHours(hours)
            .toMinutes()

        return buildString {
            if (hours > 0) {
                append("$hours h")
            }

            if (minutes > 0) {
                append("${if (hours > 0) " " else ""}$minutes min")
            }
        }
    }
}

