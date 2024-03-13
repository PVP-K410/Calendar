package com.pvp.app.ui.screen.calendar

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

object DateUtil {

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
    val firstDayOfMonth = LocalDate.of(year, month, 1)
    val firstMondayOfMonth = firstDayOfMonth.with(DayOfWeek.MONDAY)
    val firstDayOfNextMonth = firstDayOfMonth.plusMonths(1)

    return generateSequence(firstMondayOfMonth) { it.plusDays(1) }
        .takeWhile { it.isBefore(firstDayOfNextMonth) }
        .toList()
}

fun YearMonth.getDisplayName(): String {
    return "${month.getDisplayName(TextStyle.FULL, Locale.getDefault())} $year"
}