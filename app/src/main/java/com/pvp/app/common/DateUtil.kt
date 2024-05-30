package com.pvp.app.common

import com.google.firebase.Timestamp
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Date
import java.util.Locale

object DateUtil {

    /**
     * Returns a day names array of the week. Uses system's default locale.
     */
    val daysOfWeek: Array<String>
        get() {
            val daysOfWeek = Array(7) { "" }

            for (dayOfWeek in DayOfWeek.entries) {
                daysOfWeek[dayOfWeek.value - 1] = dayOfWeek.getDisplayName(
                    TextStyle.SHORT,
                    Locale.getDefault()
                )
            }

            return daysOfWeek
        }

    /**
     * Returns an end Instant for a specified date.
     * If date is today - returns Instant for the current time,
     * else returns Instant that is the start of the next day.
     */
    fun toNowOrNextDay(date: LocalDate): Instant {
        return if (date.isEqual(LocalDate.now())) {
            ZonedDateTime
                .of(
                    LocalDateTime.now(),
                    ZoneId.systemDefault()
                )
                .toInstant()
        } else {
            date
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
        }
    }

    /**
     * Returns days of the YearMonth
     * Always returns days starting from Monday,
     * therefore sometimes a padding of days belonging to previous month might be present
     * in the list (if the month does not start on Monday)
     *
     * E.g. if trying to obtain days of month March which first day (March 1st)
     * starts on a Friday, the method adds a padding and returns:
     * Feb 26 (Monday), Feb 27 (Tuesday), Feb 28 (Tuesday), Feb 29 (Thursday), March 1 (Friday)
     * and etc.
     */
    fun YearMonth.getDays(): List<LocalDate> {
        val firstDayOfMonth = LocalDate.of(
            year,
            month,
            1
        )

        val firstMondayOfMonth = firstDayOfMonth.with(DayOfWeek.MONDAY)
        val firstDayOfNextMonth = firstDayOfMonth.plusMonths(1)

        return generateSequence(firstMondayOfMonth) { it.plusDays(1) }
            .takeWhile { it.isBefore(firstDayOfNextMonth) }
            .toList()
    }

    /**
     * Returns a display name of the YearMonth in the format "Month Year"
     */
    fun YearMonth.getDisplayName(): String {
        return "${
            month.getDisplayName(
                TextStyle.FULL,
                Locale.getDefault()
            )
        } $year"
    }

    /**
     * Converts LocalDate to Timestamp
     */
    fun LocalDate.toTimestamp(): Timestamp {
        return Timestamp(
            Date.from(
                atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
            )
        )
    }

    fun Timestamp.toLocalDate(): LocalDate {
        return LocalDate.ofInstant(
            toDate()
                .toInstant(),
            ZoneId.systemDefault()
        )
    }

    /**
     * Converts LocalDateTime to epoch second in system's default time zone
     */
    fun LocalDateTime.toEpochSecondTimeZoned(): Long {
        return toEpochSecond(
            ZoneId
                .systemDefault().rules
                .getOffset(this)
        )
    }

    /**
     * Compares current date with specified day of the week and returns the nearest date of that day.
     *
     * @return LocalDate of the nearest day of the week
     */
    fun DayOfWeek.toNearestDate(): LocalDate {
        val today = LocalDate.now()

        return if (today.dayOfWeek == this) {
            today
        } else {
            today.with(TemporalAdjusters.next(this))
        }
    }
}