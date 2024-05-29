package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.model.Task
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters

@Composable
fun CalendarWeeklyScreen(
    model: CalendarWeeklyViewModel = hiltViewModel(),
    modifier: Modifier
) {
    val state by model.state.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) { Week(tasks = state.tasks) }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Week(
    modifier: Modifier = Modifier,
    tasks: List<Task>
) {
    val days = (1..7).map {
        DayOfWeek
            .of(it)
            .getDisplayName(
                TextStyle.FULL,
                java.util.Locale.getDefault()
            )
            .capitalize()
    }

    val today = LocalDate.now()
    val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val dates = (0..6).map { startOfWeek.plusDays(it.toLong()) }
    var stateShowSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val statePager = rememberPagerState(
        initialPage = dates.indexOf(today),
        pageCount = { days.size }
    )

    val date = dates[statePager.currentPage]
    var stateShowCards by remember { mutableStateOf(false) }
    val tasksFiltered = tasks.filter { it.date == date }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            contentPadding = PaddingValues(
                90.dp,
                0.dp
            ),
            modifier = modifier.height(LocalConfiguration.current.screenHeightDp.dp / 4),
            state = statePager
        ) { page ->
            DayCard(
                date = dates[page],
                day = days[page],
                onClick = {
                    if (date != dates[page]) {
                        scope.launch {
                            statePager.scrollToPage(page)
                        }
                    } else {
                        if (tasksFiltered.isEmpty()) {
                            stateShowSheet = true
                        } else {
                            stateShowCards = !stateShowCards
                        }
                    }
                },
                page = page,
                pageIndex = statePager.currentPage
            )
        }

        if (!stateShowCards || tasksFiltered.isEmpty()) {
            if (!date.isEqual(LocalDate.MIN) && !date.isAfter(LocalDate.now())) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .shadow(
                            elevation = 55.dp,
                            shape = MaterialTheme.shapes.medium
                        )
                ) {
                    AnalysisOfDay(
                        date = date,
                        tasks = tasksFiltered
                    )
                }
            }
        } else {
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                TasksOfDay(tasksFiltered)
            }
        }

        TaskCreateSheet(
            date = date,
            isOpen = stateShowSheet,
            onClose = { stateShowSheet = false }
        )
    }
}