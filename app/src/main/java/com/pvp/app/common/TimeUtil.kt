package com.pvp.app.common

import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

object TimeUtil {

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

    /**
     * Parses LocalTime object to a string
     * @return HH:mm or HH:mm - HH:mm if range is provided
     */
    fun LocalTime.asString(range: Duration? = null): String {
        return if (range == null) {
            format(DateTimeFormatter.ofPattern("HH:mm"))
        } else {
            format(DateTimeFormatter.ofPattern("HH:mm")) + " - " +
                    plus(range)
                        .format(DateTimeFormatter.ofPattern("HH:mm"))
        }
    }

    /**
     * Converts TimeUnit to ChronoUnit
     */
    fun TimeUnit.asChronoUnit(): ChronoUnit {
        return when (this) {
            TimeUnit.NANOSECONDS -> ChronoUnit.NANOS
            TimeUnit.MICROSECONDS -> ChronoUnit.MICROS
            TimeUnit.MILLISECONDS -> ChronoUnit.MILLIS
            TimeUnit.SECONDS -> ChronoUnit.SECONDS
            TimeUnit.MINUTES -> ChronoUnit.MINUTES
            TimeUnit.HOURS -> ChronoUnit.HOURS
            TimeUnit.DAYS -> ChronoUnit.DAYS
            else -> error("Unsupported time unit: $this")
        }
    }
}

