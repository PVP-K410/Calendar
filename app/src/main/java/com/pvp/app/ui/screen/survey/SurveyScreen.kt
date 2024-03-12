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
        val form by viewModel.form.collectAsStateWithLifecycle()
        var success by remember { mutableStateOf(true) }

        val callback = form.content(
            Modifier.weight(0.9f),
            viewModel
        )

        Row(
            modifier = Modifier.weight(0.1f)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(0.8f),
                onClick = {
                    success = callback()

                    if (success) {
                        viewModel.next()
                    } else {
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
                    text = if (form.hasNext()) textContinue else textSubmit,
                )
            }
        }
    }
}