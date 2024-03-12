package com.pvp.app.ui.screen.survey

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DetailsSurveyScreen(
    viewModel: DetailsSurveyViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        BodyMassIndexSurvey(
            onSubmit = { mass, height ->
                viewModel.updateBodyMassIndex(
                    mass,
                    height
                )
            }
        )
    }
}