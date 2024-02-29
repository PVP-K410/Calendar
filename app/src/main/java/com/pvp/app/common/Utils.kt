package com.pvp.app.common

import java.time.Duration
/**
 * Parses Duration object to a string
 * Format <HH> h <mm> min
 */
fun getDurationString(duration: Duration): String {
    val hours = duration.toHours()
    val minutes = duration.minusHours(hours).toMinutes()

    return buildString {
        if (hours > 0) {
            append("$hours h")
        }

        if (minutes > 0) {
            append("${if (hours > 0) " " else ""}$minutes min")
        }
    }
}