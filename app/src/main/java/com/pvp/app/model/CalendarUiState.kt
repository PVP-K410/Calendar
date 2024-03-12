package com.pvp.app.model

import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState(
    val yearMonth: YearMonth,
    val dates: List<DateEntry>
) {
    companion object {
        val Init = CalendarUiState(
            yearMonth = YearMonth.now(),
            dates = emptyList()
        )
    }
    data class DateEntry(
        val date: LocalDate,
        val isSelected: Boolean
    ) {
        companion object {
            val Empty = DateEntry(LocalDate.now(), false)
        }
    }
}
