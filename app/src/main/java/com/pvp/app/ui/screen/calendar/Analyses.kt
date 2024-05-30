package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Nightlight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.pvp.app.R
import com.pvp.app.common.TimeUtil.asString
import com.pvp.app.ui.common.StepsGoal
import java.time.Duration
import java.time.LocalDate

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CalorieCounter(
    date: LocalDate,
    model: CalendarWeeklyViewModel = hiltViewModel()
) {
    var calories by remember { mutableDoubleStateOf(0.0) }

    val permissionState = rememberPermissionState(
        permission = HealthPermission.getReadPermission(
            TotalCaloriesBurnedRecord::class
        )
    )

    LaunchedEffect(
        date,
        permissionState.status
    ) {
        calories = model.getDayCaloriesTotal(date)
    }

    Icon(
        imageVector = Icons.Outlined.LocalFireDepartment,
        contentDescription = "Calories",
        modifier = Modifier.size(26.dp)
    )

    Text(
        text = "%.2f ".format(calories / 1000) + stringResource(R.string.measurement_kcal),
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(start = 8.dp)
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HeartRateCounterAverage(
    date: LocalDate,
    model: CalendarWeeklyViewModel = hiltViewModel()
) {
    var heartRate by remember { mutableLongStateOf(0L) }

    val permissionState = rememberPermissionState(
        permission = HealthPermission.getReadPermission(
            TotalCaloriesBurnedRecord::class
        )
    )

    LaunchedEffect(
        date,
        permissionState.status
    ) {
        heartRate = model.getDayHeartRateAverage(date)
    }

    Icon(
        contentDescription = null,
        imageVector = Icons.Outlined.MonitorHeart,
        modifier = Modifier.size(26.dp)
    )

    Text(
        modifier = Modifier.padding(start = 8.dp),
        style = MaterialTheme.typography.titleSmall,
        text = if (heartRate > 0) "$heartRate bpm" else "-"
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SleepDurationCounter(
    date: LocalDate,
    model: CalendarWeeklyViewModel = hiltViewModel()
) {
    var duration by remember { mutableStateOf(Duration.ZERO) }

    val permissionState = rememberPermissionState(
        permission = HealthPermission.getReadPermission(
            SleepSessionRecord::class
        )
    )

    LaunchedEffect(
        date,
        permissionState.status
    ) {
        duration = model.getDaySleepDuration(date)
    }

    Icon(
        contentDescription = null,
        imageVector = Icons.Outlined.Nightlight,
        modifier = Modifier.size(26.dp)
    )

    Text(
        modifier = Modifier.padding(start = 8.dp),
        style = MaterialTheme.typography.titleSmall,
        text = if (!duration.equals(Duration.ZERO)) duration.asString() else "-"
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StepCounter(
    date: LocalDate,
    model: CalendarWeeklyViewModel = hiltViewModel()
) {
    var steps by remember { mutableLongStateOf(0L) }

    val permissionState = rememberPermissionState(
        permission = HealthPermission.getReadPermission(
            StepsRecord::class
        )
    )

    LaunchedEffect(
        date,
        permissionState.status
    ) {
        steps = model.getDaysSteps(date)
    }

    val goal = model.state.collectAsStateWithLifecycle().value.user?.stepsPerDayGoal ?: 0

    StepsGoal(
        current = steps.toInt(),
        modifier = Modifier.size(96.dp),
        target = goal
    )
}