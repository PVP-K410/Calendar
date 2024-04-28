package com.pvp.app.ui.screen.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.Configuration
import com.pvp.app.api.GoalService
import com.pvp.app.api.UserService
import com.pvp.app.model.Goal
import com.pvp.app.model.SportActivity
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class GoalViewModel @Inject constructor(
    configuration: Configuration,
    private val goalService: GoalService,
    private val userService: UserService
) : ViewModel() {

    private val _state = MutableStateFlow(GoalState())
    val state = _state.asStateFlow()

    val rangeKilometers = configuration.rangeKilometers

    init {
        viewModelScope.launch {
            val flowUser = userService.user

            val flowGoals = flowUser.flatMapLatest { user ->
                user
                    ?.let { goalService.get(user.email) }
                    ?: flowOf(listOf())
            }

            flowUser
                .combine(flowGoals) { user, goals ->
                    user ?: return@combine _state.value

                    val now = LocalDate.now()
                    val weekStartDate = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    val weekEndDate = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

                    GoalState(
                        allGoals = goals,
                        currentGoals = filterGoals(
                            goals = goals,
                            start = weekStartDate,
                            end = weekEndDate,
                            monthly = false
                        ),
                        user = user,
                        monthStartDate = now.withDayOfMonth(1),
                        monthEndDate = now
                            .plusMonths(1)
                            .withDayOfMonth(1),
                        monthly = false,
                        weekStartDate = weekStartDate,
                        weekEndDate = weekEndDate
                    )
                }
                .collect { state -> _state.update { state } }
        }
    }

    fun create(
        activity: SportActivity,
        goal: Double,
        monthly: Boolean,
        steps: Boolean
    ) {
        val startDate = LocalDate.now()

        val endDate = when (monthly) {
            true -> startDate.plusMonths(1)
            false -> startDate.plusDays(7)
        }

        viewModelScope.launch {
            state
                .first()
                .let { state ->
                    goalService.create(
                        activity = activity,
                        goal = goal,
                        startDate = startDate,
                        endDate = endDate,
                        monthly = monthly,
                        steps = steps,
                        email = state.user!!.email
                    )
                }
        }
    }

    fun changeMonthly() {
        _state.update { currentState ->
            currentState.copy(
                monthly = !currentState.monthly
            )
        }

        filterGoals()
    }

    private fun filterGoals(
        goals: List<Goal>,
        start: LocalDate,
        end: LocalDate,
        monthly: Boolean
    ): List<Goal> {
        return goals.filter { goal ->
            goal.startDate.isBefore(end.plusDays(1))
                    && goal.endDate.isAfter(start.minusDays(1))
                    && goal.monthly == monthly
        }
    }

    private fun filterGoals() {
        val (start, end) = when (state.value.monthly) {
            true -> {
                Pair(
                    state.value.monthStartDate,
                    state.value.monthEndDate
                )
            }

            false -> {
                Pair(
                    state.value.weekStartDate,
                    state.value.weekEndDate
                )
            }
        }

        _state.update { currentState ->
            currentState.copy(
                currentGoals = filterGoals(
                    goals = currentState.allGoals,
                    start = start,
                    end = end,
                    monthly = currentState.monthly
                )
            )
        }
    }

    fun next() {
        _state.update { currentState ->
            currentState.copy(
                monthStartDate = currentState.monthStartDate.plusMonths(1),
                monthEndDate = currentState.monthEndDate.plusMonths(1),
                weekStartDate = currentState.weekStartDate.plusDays(7),
                weekEndDate = currentState.weekEndDate.plusDays(7)
            )
        }

        filterGoals()
    }

    fun previous() {
        _state.update { currentState ->
            currentState.copy(
                monthStartDate = currentState.monthStartDate.minusMonths(1),
                monthEndDate = currentState.monthEndDate.minusMonths(1),
                weekStartDate = currentState.weekStartDate.minusDays(7),
                weekEndDate = currentState.weekEndDate.minusDays(7)
            )
        }

        filterGoals()
    }
}

data class GoalState(
    val allGoals: List<Goal> = listOf(),
    val currentGoals: List<Goal> = listOf(),
    var monthly: Boolean = false,
    var monthStartDate: LocalDate = LocalDate.now(),
    var monthEndDate: LocalDate = LocalDate.now(),
    val user: User? = null,
    var weekStartDate: LocalDate = LocalDate.now(),
    var weekEndDate: LocalDate = LocalDate.now()
)