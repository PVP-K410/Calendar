@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pvp.app.ui.screen.statistics

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.ActivityService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.common.DateUtil.toTimestamp
import com.pvp.app.model.ActivityEntry
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val activityService: ActivityService,
    private val userService: UserService,
    private val taskService: TaskService
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

            suspend fun tasksByDate(
                date: Pair<LocalDate, LocalDate>,
                email: String
            ): Flow<List<Task>> {
                return taskService
                    .get(email)
                    .mapLatest {
                        it.sortedBy { task -> task.date }
                    }
                    .mapLatest {
                        it.filter { task ->
                            task.date in date.first..date.second
                        }
                    }
            }

            val uniqueActivities30dFlow = userFlow.flatMapLatest { user ->
                val now = LocalDate.now()

                tasksByDate(
                    now.minusDays(29) to now,
                    user.email
                )
                    .map { entries ->
                        entries
                            .mapNotNull { task ->
                                (task as? SportTask)?.activity?.title
                            }
                            .distinct()
                    }
            }

            val top3FrequentActivitiesFlow = userFlow.flatMapLatest { user ->
                val now = LocalDate.now()

                tasksByDate(
                    LocalDate.MIN to now,
                    user.email
                )
                    .map { entries ->
                        val activities = entries.mapNotNull { task ->
                            (task as? SportTask)?.activity
                        }

                        activities
                            .groupingBy { it }
                            .eachCount()
                            .toList()
                            .sortedByDescending { it.second }
                            .take(3)
                            .map { it.first.title }
                    }
            }

            val averagePointsFlow = userFlow.flatMapLatest { user ->
                val now = LocalDate.now()

                tasksByDate(
                    LocalDate.MIN to now,
                    user.email
                )
                    .map { entries ->
                        if (entries.isNotEmpty()) {
                            val totalPoints = entries.sumOf { it.points.value }
                            val averagePoints = totalPoints.toDouble() / entries.size

                            averagePoints
                        } else {
                            0.0
                        }
                    }
            }

            val averageTasksCompleted7dFlow = userFlow.flatMapLatest { user ->
                val now = LocalDate.now()

                tasksByDate(
                    now.minusDays(6) to now,
                    user.email
                )
                    .map { tasks ->
                        val tasksByDate = tasks.groupBy { it.date }
                        val tasksCountByDate = tasksByDate.mapValues { it.value.size }
                        val averageTasksCount = tasksCountByDate.values.average()

                        averageTasksCount
                    }
            }

            val averageTasksCompleted30dFlow = userFlow.flatMapLatest { user ->
                val now = LocalDate.now()

                tasksByDate(
                    now.minusDays(29) to now,
                    user.email
                )
                    .map { tasks ->
                        val tasksByDate = tasks.groupBy { it.date }
                        val tasksCountByDate = tasksByDate.mapValues { it.value.size }
                        val averageTasksCount = tasksCountByDate.values.average()

                        averageTasksCount
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

            val valuesFlow = combine(
                valuesWeekFlow,
                valuesMonthFlow,
                values7dFlow,
                values30dFlow
            ) { valuesWeek, valuesMonth, values7d, values30d ->
                StatisticsState(
                    valuesWeek = valuesWeek,
                    valuesMonth = valuesMonth,
                    values7d = values7d,
                    values30d = values30d
                )
            }

            val averagesFlow = combine(
                averageTasksCompleted7dFlow,
                averageTasksCompleted30dFlow,
                averagePointsFlow,
                uniqueActivities30dFlow,
                top3FrequentActivitiesFlow
            ) { averageTasksCompleted7d, averageTasksCompleted30d, averagePoints, uniqueActivities30d, top3FrequentActivities ->
                StatisticsState(
                    averageTasksCompleted7d = averageTasksCompleted7d,
                    averageTasksCompleted30d = averageTasksCompleted30d,
                    averagePoints = averagePoints,
                    uniqueActivities30d = uniqueActivities30d,
                    top3FrequentActivities = top3FrequentActivities
                )
            }

            combine(
                valuesFlow,
                averagesFlow
            ) { partialState1, partialState2 ->
                StatisticsState(
                    isLoading = false,
                    valuesWeek = partialState1.valuesWeek,
                    valuesMonth = partialState1.valuesMonth,
                    values7d = partialState1.values7d,
                    values30d = partialState1.values30d,
                    averageTasksCompleted7d = partialState2.averageTasksCompleted7d,
                    averageTasksCompleted30d = partialState2.averageTasksCompleted30d,
                    averagePoints = partialState2.averagePoints,
                    uniqueActivities30d = partialState2.uniqueActivities30d,
                    top3FrequentActivities = partialState2.top3FrequentActivities
                )
            }
                .collectLatest { state ->
                    _state.update { state }
                }
        }
    }
}

data class StatisticsState(
    val isLoading: Boolean = true,
    val valuesWeek: List<ActivityEntry> = listOf(),
    val valuesMonth: List<ActivityEntry> = listOf(),
    val values7d: List<ActivityEntry> = listOf(),
    val values30d: List<ActivityEntry> = listOf(),
    val averageTasksCompleted7d: Double = 0.0,
    val averageTasksCompleted30d: Double = 0.0,
    val averagePoints: Double = 0.0,
    val uniqueActivities30d: List<@Composable () -> String> = listOf(),
    val top3FrequentActivities: List<@Composable () -> String> = listOf()
)