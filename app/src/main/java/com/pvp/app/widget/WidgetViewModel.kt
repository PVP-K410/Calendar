package com.pvp.app.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.FriendService
import com.pvp.app.api.GoalService
import com.pvp.app.api.HealthConnectService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.common.DateUtil
import com.pvp.app.model.Goal
import com.pvp.app.model.Task
import com.pvp.app.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class WidgetViewModel @Inject constructor(
    private val friendsService: FriendService,
    private val goalService: GoalService,
    private val healthConnectService: HealthConnectService,
    private val userService: UserService,
    private val taskService: TaskService
) : ViewModel() {

    private val _state: MutableStateFlow<WidgetState> = MutableStateFlow(WidgetState())
    val state = _state.asStateFlow()

    init {
        collectStateChanges()
    }

    private fun collectStateChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            val userFlow = userService.user.filterNotNull()
            val friendFlow = friendsService.get(userFlow.first().email)
            val goalFlow = goalService.get(userFlow.first().email)
            val taskFlow = taskService.get(userFlow.first().email)
            val caloriesFlow = getCaloriesFlow()
            val stepsFlow = getStepsFlow()
            val heartRateFlow = getHeartRateFlow()

            val partialFlow1 = combine(
                userFlow,
                friendFlow,
                goalFlow,
                taskFlow
            ) { user, friends, goals, tasks ->
                WidgetState(
                    decorationCount = user.decorationsOwned.size,
                    friendCount = friends?.friends?.size ?: 0,
                    goals = goals.filter {
                        it.startDate <= LocalDate.now() && it.endDate >= LocalDate.now()
                    },
                    tasks = tasks.filter { it.date == LocalDate.now() },
                    user = user
                )
            }

            val partialFlow2 = combine(
                caloriesFlow,
                stepsFlow,
                heartRateFlow
            ) { calories, steps, heartRate ->
                WidgetState(
                    calories = calories,
                    heartRate = heartRate.toDouble(),
                    steps = steps.toDouble()
                )
            }

            combine(
                partialFlow1,
                partialFlow2
            ) { state1, state2 ->
                state1.copy(
                    calories = state2.calories,
                    heartRate = state2.heartRate,
                    steps = state2.steps,
                    isLoading = false
                )
            }.collect {
                _state.value = it
            }
        }
    }

    private fun getCaloriesFlow(): Flow<Double> = flow {
        val today = LocalDate.now()
        val end = DateUtil.toNowOrNextDay(today)
        val start = today
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        val result = healthConnectService.aggregateTotalCalories(
            start,
            end
        )

        emit(result)
    }

    private fun getStepsFlow(): Flow<Long> = flow {
        val today = LocalDate.now()

        val end = today
            .plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        val start = today
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        val result = healthConnectService.aggregateSteps(
            start,
            end
        )

        emit(result)
    }

    private fun getHeartRateFlow(): Flow<Long> = flow {
        val today = LocalDate.now()
        val end = DateUtil.toNowOrNextDay(today)
        val start = today
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        val result = healthConnectService.getHeartRateAvg(
            start,
            end
        )

        emit(result)
    }
}

data class WidgetState(
    val decorationCount: Int = 0,
    val friendCount: Int = 0,
    val goals: List<Goal> = emptyList(),
    val isLoading: Boolean = true,
    val tasks: List<Task> = emptyList(),
    val user: User = User(),
    val calories: Double = 0.0,
    val heartRate: Double = 0.0,
    val steps: Double = 0.0,
)