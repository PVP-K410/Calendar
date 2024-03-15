package com.pvp.app.ui.screen.survey

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.R
import com.pvp.app.model.Survey
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.common.showToast

@Composable
fun SurveyScreen(
    viewModel: SurveyViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val textContinue = stringResource(R.string.action_continue)
    val textError = stringResource(R.string.form_survey_toast_error)
    val textSubmit = stringResource(R.string.action_submit)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        var handler by remember { mutableStateOf({}) }
        var success by remember { mutableStateOf(true) }

        SurveyInput(
            handler = { onSubmit ->
                handler = onSubmit
            },
            modifier = Modifier.weight(0.9f),
            viewModel = viewModel
        )

        Row(
            modifier = Modifier.weight(0.1f)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(0.8f),
                onClick = {
                    try {
                        handler()

                        viewModel.next()
                    } catch (e: Exception) {
                        success = false

                        context.showToast(message = textError)
                    }
                }
            ) {
                if (!success) {
                    Icon(
                        imageVector = Icons.Outlined.ErrorOutline,
                        contentDescription = "Submission was not successful indicator"
                    )

                    Spacer(modifier = Modifier.padding(8.dp))
                }

                Text(
                    style = MaterialTheme.typography.labelMedium,
                    text = if (viewModel.hasNext()) textContinue else textSubmit,
                )
            }
        }
    }
}

@Composable
fun SurveyInput(
    handler: (onSubmit: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SurveyViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state.current) {
        Survey.BODY_MASS_INDEX -> {
            BodyMassIndexSurvey(
                modifier = modifier,
                handler = { height, mass ->
                    handler {
                        viewModel.updateBodyMassIndex(
                            height = height,
                            mass = mass
                        )
                    }
                }
            )
        }

        Survey.FILTER_ACTIVITIES -> {
            FilterActivitiesSurvey(
                modifier = modifier,
                handler = { filters ->
                    handler {
                        viewModel.updateUserFilters(
                            filters = filters
                        )
                    }
                }
            )
        }

        else -> {}
    }
}