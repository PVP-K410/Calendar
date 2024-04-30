package com.pvp.app.ui.screen.calendar

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.HealthConnectService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.common.DateUtil.toNowOrNextDay
import com.pvp.app.common.TaskUtil.sort
import com.pvp.app.model.Task
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.IsoFields
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class CalendarWeeklyViewModel @Inject constructor(
    private val taskService: TaskService,
    private val userService: UserService,
    private val healthConnectService: HealthConnectService,
    private val client: HealthConnectClient
) : ViewModel() {

    private val _state = MutableStateFlow(CalendarState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val flowUser = userService.user.filterNotNull()

            val flowTasks = flowUser.flatMapLatest { user ->
                user.let { taskService.get(user.email) }
            }

            flowUser
                .combine(flowTasks) { user, tasks ->
                    val now = LocalDateTime.now()

                    CalendarState(
                        tasks = tasks
                            .filter {
                                it.date.year == now.year &&
                                        it.date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) ==
                                        now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
                            }
                            .sort(),
                        user = user
                    )
                }
                .collectLatest { state -> _state.update { state } }
        }
    }

    suspend fun permissionsGranted(): Boolean {
        val granted = client.permissionController.getGrantedPermissions()

        return granted.containsAll(PERMISSIONS)
    }

    suspend fun getDayCaloriesTotal(date: LocalDate): Double {
        val end = toNowOrNextDay(date)

        val start = date
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        return healthConnectService.aggregateTotalCalories(
            start,
            end
        )
    }

    suspend fun getDayHeartRateAverage(date: LocalDate): Long {
        val end = toNowOrNextDay(date)

        val start = date
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        return healthConnectService.getHeartRateAvg(
            start,
            end
        )
    }

    suspend fun getDaySleepDuration(date: LocalDate): Duration {
        val end = date
            .plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        val start = date
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        return healthConnectService.aggregateSleepDuration(
            start,
            end
        )
    }

    suspend fun getDaysSteps(date: LocalDate): Long {
        val end = date
            .plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        val start = date
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        return healthConnectService.aggregateSteps(
            start,
            end
        )
    }
}

data class CalendarState(
    val tasks: List<Task> = listOf(),
    val user: User? = null
)

val PERMISSIONS = setOf(
    HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
    HealthPermission.getReadPermission(DistanceRecord::class),
    HealthPermission.getReadPermission(ExerciseSessionRecord::class),
    HealthPermission.getReadPermission(HeartRateRecord::class),
    HealthPermission.getReadPermission(SleepSessionRecord::class),
    HealthPermission.getReadPermission(StepsRecord::class),
    HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class)
)