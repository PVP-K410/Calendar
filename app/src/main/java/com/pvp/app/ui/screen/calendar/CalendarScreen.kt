package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarScreen() {
    val state = rememberPagerState(
        pageCount = { 2 },
        initialPage = 1
    )

    VerticalPager(
        state = state,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        when (page) {
            0 -> CalendarMonthlyScreen()
            1 -> CalendarWeeklyScreen()
        }
    }
}