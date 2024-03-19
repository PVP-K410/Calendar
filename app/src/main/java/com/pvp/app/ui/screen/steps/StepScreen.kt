package com.pvp.app.ui.screen.steps

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.common.getOccurences

@Composable
fun ActivityList(
    model: StepViewModel = hiltViewModel()
) {
    // Required for checking whether user has permissions before entering the window,
    // as users can revoke permissions at any time
    val permissionContract = PermissionController.createRequestPermissionResultContract()

    val launcher =
        rememberLauncherForActivityResult(permissionContract) {
            model.getExercises()
        }

    LaunchedEffect(Unit) {
        if (model.permissionsGranted()) {
            model.getExercises()
        } else {
            launcher.launch(PERMISSIONS)
        }
    }

    val activities = model.activities
        .collectAsStateWithLifecycle().value.getOccurences()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Your favorite activities:",
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height((16.dp)))

        activities.forEachIndexed { index, activity ->
            Text(
                text = "${index + 1}. ${activity.first.title} (${activity.second} occurences)",
                textAlign = TextAlign.Start,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun StepCounter(
    model: StepViewModel = hiltViewModel()
) {

    // Required for checking whether user has permissions before entering the window,
    // as users can revoke permissions at any time
    val permissionContract = PermissionController.createRequestPermissionResultContract()

    val launcher =
        rememberLauncherForActivityResult(permissionContract) {
            model.updateTodaysSteps()
        }

    LaunchedEffect(Unit) {
        if (model.permissionsGranted()) {
            model.updateTodaysSteps()
        } else {
            launcher.launch(PERMISSIONS)
        }
    }

    val steps = model.stepsCount.collectAsStateWithLifecycle()

    Text(
        steps.value.toString(),
        textAlign = TextAlign.Center,
        fontSize = 36.sp
    )
}

/**
 * Simple screen for testing the API
 * Later step counter will be moved to daily calendar view
 */
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun StepScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Step count today",
            textAlign = TextAlign.Center,
            fontSize = 36.sp,
        )

        Spacer(modifier = Modifier.height((16.dp)))

        StepCounter()

        Spacer(modifier = Modifier.height((16.dp)))

        HorizontalDivider()

        Spacer(modifier = Modifier.height((16.dp)))

        ActivityList()
    }
}