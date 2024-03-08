package com.pvp.app.ui.screen.steps

import androidx.health.connect.client.records.StepsRecord
import android.os.Build
import android.widget.Toast
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
    private val client: HealthConnectClient,
    private val service: HealthConnectService
) : ViewModel() {

    val PERMISSIONS = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class)
    )

    private val _stepsCount = MutableStateFlow(0)
    val stepsCount = _stepsCount.asStateFlow()

    suspend fun permissionsGranted(): Boolean {
        val granted = client.permissionController.getGrantedPermissions()

        return granted.containsAll(PERMISSIONS)
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun updateTodaysSteps() {
        viewModelScope.launch {
            val end = Instant.now()
            val start = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()

            try {
                val records: List<StepsRecord> = service.readActivityData(
                    record = StepsRecord::class,
                    start = start,
                    end = end
                )

                _stepsCount.value = records.sumOf { it.count.toInt() }
            } catch (e: Exception) {
                // Exception should be handled by showing a Toast
                e.printStackTrace()
            }
        }
    }
}