package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalPagerScreen(currentPage: Int) {
    val pagerState = rememberPagerState(pageCount = { 2 }, initialPage = currentPage)

    VerticalPager(state = pagerState, modifier = Modifier.fillMaxSize()) {page ->
        when (page) {
            0 -> MonthlyCalendarScreen()
            1 -> CalendarScreen()
        }
    }
}