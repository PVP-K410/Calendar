@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pvp.app.ui.screen.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.ActivityService
import com.pvp.app.api.UserService
import com.pvp.app.common.DateUtil.toTimestamp
import com.pvp.app.model.ActivityEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val activityService: ActivityService,
    private val userService: UserService
) : ViewModel() {

    private val _state = MutableStateFlow(StatisticsState())
    val state = _state.asStateFlow()

    init {
        collectStateChanges()
    }

    private fun collectStateChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            val userFlow = userService.user.filterNotNull()

            suspend fun entriesByDate(
                date: Pair<LocalDate, LocalDate>,
                email: String
            ): Flow<List<ActivityEntry>> {
                return activityService
                    .get(
                        date,
                        email
                    )
                    .mapLatest { entries ->
                        entries
                            .sortedBy { it.date }
                            .distinctBy { it.date }
                    }
                    .mapLatest {
                        val entries = it.toMutableList()
                        val start = date.first
                        val end = date.second
                        var current = start

                        while (current <= end) {
                            val currentTimestamp = current.toTimestamp()

                            if (entries.none { entry -> entry.date == currentTimestamp }) {
                                entries.add(ActivityEntry(date = currentTimestamp))
                            }

                            current = current.plusDays(1)
                        }

                        entries
                    }
            }

            val valuesWeekFlow = userFlow.flatMapLatest { user ->
                val now = LocalDate.now()

                entriesByDate(
                    now.with(DayOfWeek.MONDAY) to now,
                    user.email
                )
            }

            val valuesMonthFlow = userFlow.flatMapLatest { user ->
                val now = LocalDate.now()

                entriesByDate(
                    now.withDayOfMonth(1) to now,
                    user.email
                )
            }

            val values7dFlow = userFlow.flatMapLatest { user ->
                val now = LocalDate.now()

                entriesByDate(
                    now.minusDays(6) to now,
                    user.email
                )
            }

            val values30dFlow = userFlow.flatMapLatest { user ->
                val now = LocalDate.now()

                entriesByDate(
                    now.minusDays(29) to now,
                    user.email
                )
            }

            combine(
                valuesWeekFlow,
                valuesMonthFlow,
                values7dFlow,
                values30dFlow
            ) { valuesWeek, valuesMonth, values7d, values30d ->
                StatisticsState(
                    isLoading = false,
                    valuesWeek = valuesWeek,
                    valuesMonth = valuesMonth,
                    values7d = values7d,
                    values30d = values30d
                )
            }
                .collectLatest { state -> _state.update { state } }
        }
    }
}

data class StatisticsState(
    val isLoading: Boolean = true,
    val valuesWeek: List<ActivityEntry> = listOf(),
    val valuesMonth: List<ActivityEntry> = listOf(),
    val values7d: List<ActivityEntry> = listOf(),
    val values30d: List<ActivityEntry> = listOf()
)