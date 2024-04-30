@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pvp.app.ui.screen.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.common.DateUtil.getDays
import com.pvp.app.common.TaskUtil.sort
import com.pvp.app.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarMonthlyViewModel @Inject constructor(
    private val taskService: TaskService,
    private val userService: UserService
) : ViewModel() {

    private val _state = MutableStateFlow(CalendarUiState.Init)
    val state: StateFlow<CalendarUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val flowUser = userService.user.filterNotNull()

            val flowTasks = flowUser.flatMapLatest { user ->
                user.let { taskService.get(user.email) }
            }

            flowUser
                .combine(flowTasks) { user, tasks ->
                    _state.value.copy(
                        tasks = tasks.sort(),
                        dates = getDates(
                            YearMonth.now(),
                            tasks
                        )
                    )
                }
                .collectLatest { state ->
                    _state.update { stateOld ->
                        stateOld.copy(
                            tasks = state.tasks,
                            dates = state.dates
                        )
                    }
                }
        }
    }

    fun changeMonth(nextMonth: YearMonth) {
        _state.update { currentState ->
            currentState.copy(
                yearMonth = nextMonth,
                dates = getDates(
                    nextMonth,
                    currentState.tasks
                )
            )
        }
    }

    private fun getDates(
        yearMonth: YearMonth,
        tasks: List<Task>
    ): List<CalendarUiState.DateEntry> {
        return yearMonth
            .getDays()
            .map { date ->
                if (date.month == yearMonth.month) {
                    CalendarUiState.DateEntry(
                        date = date,
                        isHighlighted = date.isEqual(LocalDate.now()),
                        tasks = getTasksOfDate(
                            date,
                            tasks
                        )
                    )
                } else {
                    // List at front is padded with empty data, that helps to ensure that
                    // the first day of the month is at the correct week day
                    // e.g. friday instead of monday
                    CalendarUiState.DateEntry.Empty
                }
            }
    }

    private fun getTasksOfDate(
        date: LocalDate,
        tasks: List<Task>
    ): List<Task> {
        return tasks.filter { task ->
            task.date.year == date.year &&
                    task.date.month == date.month &&
                    task.date.dayOfMonth == date.dayOfMonth
        }
    }
}

data class CalendarUiState(
    val yearMonth: YearMonth,
    val dates: List<DateEntry>,
    val tasks: List<Task>
) {

    companion object {

        val Init = CalendarUiState(
            yearMonth = YearMonth.now(),
            dates = emptyList(),
            tasks = emptyList()
        )
    }

    data class DateEntry(
        val date: LocalDate,
        val isHighlighted: Boolean,
        var tasks: List<Task>
    ) {

        companion object {

            val Empty = DateEntry(
                LocalDate.MIN,
                false,
                emptyList()
            )
        }
    }
}