package com.pvp.app.ui.screen.steps

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.HealthConnectService
import com.pvp.app.common.toSportActivities
import com.pvp.app.model.SportActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class StepViewModel @Inject constructor(
    private val client: HealthConnectClient,
    private val service: HealthConnectService
) : ViewModel() {

    private val _stepsCount = MutableStateFlow(0L)
    val stepsCount = _stepsCount.asStateFlow()

    private val _activities = MutableStateFlow<List<SportActivity>>(emptyList())
    val activities = _activities.asStateFlow()

    suspend fun permissionsGranted(): Boolean {
        val granted = client.permissionController.getGrantedPermissions()

        return granted.containsAll(PERMISSIONS)
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun updateTodaysSteps() {
        viewModelScope.launch {
            val end = LocalDate
                .now()
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()

            val start = LocalDate
                .now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()

            _stepsCount.value = service.aggregateSteps(
                start = start,
                end = end
            )
        }
    }

    fun getExercises() {
        viewModelScope.launch {
            val end = Instant.now()

            val start = LocalDate
                .now()
                .minusDays(29)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()

            val records = service.readActivityData(
                record = ExerciseSessionRecord::class,
                start = start,
                end = end
            )

            _activities.value = records.toSportActivities()
        }
    }
}

val PERMISSIONS = setOf(
    HealthPermission.getReadPermission(StepsRecord::class),
    HealthPermission.getReadPermission(ExerciseSessionRecord::class),
)