package com.pvp.app.ui.screen.steps

import androidx.health.connect.client.records.StepsRecord
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.HealthConnectService
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
    private val healthConnectClient: HealthConnectClient,
    private val healthConnectService: HealthConnectService
) : ViewModel() {

    val PERMISSIONS = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class)
    )

    private val _stepsCount = MutableStateFlow(0)
    val stepsCount = _stepsCount.asStateFlow()
    val permissionsLauncher = PermissionController.createRequestPermissionResultContract()

    suspend fun permissionsGranted(PERMISSIONS: Set<String>): Boolean {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        return granted.containsAll(PERMISSIONS)
    }
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun updateTodaysSteps() : Int {
        var count = 0
        viewModelScope.launch {
            val endTime = Instant.now()
            val startTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()

            try {
                val stepsRecords: List<StepsRecord> = healthConnectService.readActivityData(
                    recordClass = StepsRecord::class,
                    startTime = startTime,
                    endTime = endTime
                )

                val totalSteps = stepsRecords.sumOf { it.count.toInt() }
                _stepsCount.value = totalSteps
            } catch (e: Exception) {

                e.printStackTrace()
            }
        }

        return count
    }
}