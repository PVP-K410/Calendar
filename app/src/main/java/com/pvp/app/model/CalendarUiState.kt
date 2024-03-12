package com.pvp.app.model

import java.time.YearMonth

data class CalendarUiState(
    val month: YearMonth,
    val dates: List<Date>
) {
    companion object {
        val Init = CalendarUiState(
            month = YearMonth.now(),
            dates = emptyList()
        )
    }
    data class Date(
        val dayOfMonth: String,
        val isSelected: Boolean
    ) {
        companion object {
            val Empty = Date("", false)
        }
    }
}
