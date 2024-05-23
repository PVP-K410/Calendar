package com.pvp.app.ui.screen.calendar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.pvp.app.common.TimeUtil.asString
import java.time.Duration
import java.time.LocalDate
import kotlin.math.min
import kotlin.math.roundToInt

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
        text = "%.2f kCal".format(calories / 1000),
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
        text = if (heartRate > 0) "$heartRate" else "-"
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
        text = if (!duration.equals(Duration.ZERO)) duration.asString() else "- hr - m"
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

    val goal = 10000f // TODO create way for user to set a step goal?
    val progress = steps / goal

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize(fraction = 1f)
            .padding(bottom = 4.dp)
    ) {
        val backgroundArcColor = MaterialTheme.colorScheme.primaryContainer
        val progressArcColor = MaterialTheme.colorScheme.primary

        Canvas(modifier = Modifier.fillMaxSize(fraction = 1f)) {
            val strokeWidth = 6.dp.toPx()

            val radius = min(
                size.width,
                size.height
            ) / 2 - strokeWidth

            val topLeft = Offset(
                (size.width / 2) - radius,
                (size.height / 2) - radius
            )

            val size = Size(
                radius * 2,
                radius * 2
            )

            drawArc(
                color = backgroundArcColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = size,
                style = Stroke(
                    cap = StrokeCap.Round,
                    width = strokeWidth
                )
            )

            drawArc(
                color = progressArcColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = topLeft,
                size = size,
                style = Stroke(
                    cap = StrokeCap.Round,
                    width = strokeWidth
                )
            )
        }

        Text(
            style = MaterialTheme.typography.titleSmall,
            text = steps.toString(),
            textAlign = TextAlign.Center
        )
    }
}