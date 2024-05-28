package com.pvp.app.ui.screen.survey

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.R
import com.pvp.app.model.Survey
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.common.LocalShowSnackbar
import com.pvp.app.ui.common.ProgressIndicator

@Composable
fun SurveyScreen(
    viewModel: SurveyViewModel = hiltViewModel()
) {
    val textContinue = stringResource(R.string.action_continue)
    val textError = stringResource(R.string.form_survey_toast_error)
    val textSubmit = stringResource(R.string.action_submit)

    val showSnackbar = LocalShowSnackbar.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        val state by viewModel.state.collectAsStateWithLifecycle()

        if (state.surveys.isEmpty()) {
            ProgressIndicator()

            return
        }

        var handler by remember { mutableStateOf({}) }
        var success by remember { mutableStateOf(true) }

        Column(
            modifier = Modifier.weight(0.9f)
        ) {
            SurveyInput(
                handler = { onSubmit ->
                    handler = onSubmit
                },
                viewModel = viewModel
            )
        }

        Row(
            modifier = Modifier.weight(0.1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(0.8f),
                onClick = {
                    try {
                        handler()
                    } catch (e: Exception) {
                        success = false

                        showSnackbar(textError)
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
                    text = if (state.surveys.size > 1) textContinue else textSubmit,
                )
            }
        }
    }
}

@Composable
fun SurveyInput(
    handler: (onSubmit: () -> Unit) -> Unit,
    viewModel: SurveyViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state.current) {
        Survey.BODY_MASS_INDEX -> {
            BodyMassIndexSurvey(
                handler = { height, mass ->
                    handler {
                        viewModel.updateBodyMassIndex(
                            height = height,
                            mass = mass
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }

        Survey.FILTER_ACTIVITIES -> {
            FilterSurvey(
                filters = state.user.activities.map { it.title() },
                handler = { filters ->
                    handler {
                        viewModel.updateUserFilters(
                            filters = filters,
                            isActivities = true
                        )
                    }
                },
                isActivities = true,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                title = "activities"
            )
        }

        Survey.FILTER_INGREDIENTS -> {
            FilterSurvey(
                filters = state.user.ingredients.map { it.title() },
                handler = { filters ->
                    handler {
                        viewModel.updateUserFilters(
                            filters = filters,
                            isActivities = false
                        )
                    }
                },
                isActivities = false,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                title = "ingredients"
            )
        }

        else -> {}
    }
}