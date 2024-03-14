package com.pvp.app.ui.screen.calendar

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.HealthConnectService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.model.Task
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.IsoFields
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel @Inject constructor(
    private val taskService: TaskService,
    private val userService: UserService,
    private val healtConnectService: HealthConnectService,
    private val client: HealthConnectClient

) : ViewModel() {

    private val _state = MutableStateFlow(CalendarState())
    val state = _state.asStateFlow()

    private val _stepsCount = MutableStateFlow(0L)
    val stepsCount = _stepsCount.asStateFlow()

    init {
        viewModelScope.launch {
            val flowUser = userService.getCurrent()
            val flowTasks = flowUser.flatMapLatest { user ->
                user
                    ?.let { taskService.get(user.email) }
                    ?: flowOf(listOf())
            }

            combine(flowUser, flowTasks) { user, tasks ->
                val now = LocalDateTime.now()

                _state.update {
                    CalendarState(
                        tasksMonth = tasks.filter {
                            it.scheduledAt.year == now.year &&
                                    it.scheduledAt.monthValue == now.monthValue
                        },
                        tasksWeek = tasks.filter {
                            it.scheduledAt.year == now.year &&
                                    it.scheduledAt.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) ==
                                    now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
                        },
                        user = user
                    )
                }
            }
                .launchIn(viewModelScope)
        }
    }

    suspend fun permissionsGranted(): Boolean {
        val granted = client.permissionController.getGrantedPermissions()

        return granted.containsAll(PERMISSIONS)
    }

    fun getDaysSteps(date: LocalDate) {
        viewModelScope.launch {
            val end = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
            val start = date.atStartOfDay(ZoneId.systemDefault()).toInstant()

            _stepsCount.value = healtConnectService.aggregateSteps(start, end)
        }
    }
}

data class CalendarState(
    val tasksMonth: List<Task> = listOf(),
    val tasksWeek: List<Task> = listOf(),
    val user: User? = null
)

val PERMISSIONS = setOf(
    HealthPermission.getReadPermission(StepsRecord::class),
    HealthPermission.getReadPermission(ExerciseSessionRecord::class)
)