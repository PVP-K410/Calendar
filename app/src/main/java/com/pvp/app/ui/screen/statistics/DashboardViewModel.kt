package com.pvp.app.ui.screen.statistics

import android.app.NotificationManager
import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.app.NotificationManagerCompat
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
import androidx.navigation.NavHostController
import com.pvp.app.api.Configuration
import com.pvp.app.api.DecorationService
import com.pvp.app.api.FriendService
import com.pvp.app.api.GoalService
import com.pvp.app.api.HealthConnectService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.common.DateUtil
import com.pvp.app.model.Friends
import com.pvp.app.model.Goal
import com.pvp.app.model.Task
import com.pvp.app.model.User
import com.pvp.app.ui.router.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val client: HealthConnectClient,
    private val decorationService: DecorationService,
    private val friendsService: FriendService,
    private val goalService: GoalService,
    private val healthConnectService: HealthConnectService,
    private val userService: UserService,
    private val taskService: TaskService
) : ViewModel() {

    private val _state: MutableStateFlow<DashboardState> = MutableStateFlow(DashboardState())
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
            val avatarFlow = decorationService.getAvatar(userFlow)

            combine(
                userFlow,
                friendFlow,
                goalFlow,
                taskFlow,
                avatarFlow
            ) { user, friends, goals, tasks, avatar ->
                DashboardState(
                    avatar = avatar,
                    decorationCount = user.decorationsOwned.size,
                    friendCount = friends?.friends?.size ?: 0,
                    goals = goals.filter {
                        it.startDate <= LocalDate.now() && it.endDate >= LocalDate.now()
                    },
                    isLoading = false,
                    isHealthConnectEnabled = client.permissionController
                        .getGrantedPermissions()
                        .containsAll(PERMISSIONS),
                    isNotificationEnabled = isNotificationEnabled(context),
                    tasks = tasks.filter { it.date == LocalDate.now() },
                    user = user
                )
            }
                .collectLatest { state -> _state.update { state } }
        }
    }

    suspend fun getCalories(): Double {
        val today = LocalDate.now()
        val end = DateUtil.toNowOrNextDay(today)

        val start = today
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        return healthConnectService.aggregateTotalCalories(
            start,
            end
        )
    }

    suspend fun getSteps(): Long {
        val today = LocalDate.now()

        val end = today
            .plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        val start = today
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        return healthConnectService.aggregateSteps(
            start,
            end
        )
    }

    suspend fun getHeartRate(): Long {
        val today = LocalDate.now()
        val end = DateUtil.toNowOrNextDay(today)

        val start = today
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        return healthConnectService.getHeartRateAvg(
            start,
            end
        )
    }

    private fun isNotificationEnabled(context: Context): Boolean {
        val enabled = NotificationManagerCompat
            .from(context)
            .areNotificationsEnabled()

        if (!enabled) {
            return false
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        for (channel in manager.notificationChannels) {
            if (channel.importance == NotificationManager.IMPORTANCE_NONE) {
                return false
            }
        }

        return true
    }
}

data class DashboardState(
    val avatar: ImageBitmap = ImageBitmap(
        1,
        1
    ),
    val decorationCount: Int = 0,
    val friendCount: Int = 0,
    val goals: List<Goal> = emptyList(),
    val isLoading: Boolean = true,
    val isHealthConnectEnabled: Boolean = false,
    val isNotificationEnabled: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val user: User = User()
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