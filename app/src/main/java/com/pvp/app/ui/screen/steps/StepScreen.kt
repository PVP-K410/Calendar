package com.pvp.app.ui.screen.steps

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Alignment

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun StepCounter(
    viewModel: StepViewModel = hiltViewModel()
){
    val permissionsLauncher =
        rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
            viewModel.updateTodaysSteps()
        }


    LaunchedEffect(Unit) {
        if(viewModel.permissionsGranted(viewModel.PERMISSIONS)){
            viewModel.updateTodaysSteps()
        }else{
            permissionsLauncher.launch(viewModel.PERMISSIONS)
        }
    }

    val stepsCount = viewModel.stepsCount.collectAsStateWithLifecycle()


    Text(
        stepsCount.value.toString(),
        textAlign = TextAlign.Center,
        fontSize = 36.sp
    )
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun StepScreen(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Step count today",
            textAlign = TextAlign.Center,
            fontSize = 36.sp,
        )

        Spacer(modifier = Modifier.height((16.dp)))

        StepCounter()
    }
}