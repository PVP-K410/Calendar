package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarScreen(modifier: Modifier) {
    val state = rememberPagerState(
        pageCount = { 2 },
        initialPage = 1
    )

    VerticalPager(state = state) { page ->
        when (page) {
            0 -> CalendarMonthlyScreen(modifier = modifier)
            1 -> CalendarWeeklyScreen(modifier = modifier)
        }
    }
}