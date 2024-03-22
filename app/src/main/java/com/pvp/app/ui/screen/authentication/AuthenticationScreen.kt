package com.pvp.app.ui.screen.authentication

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.R
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.common.ProgressIndicator
import com.pvp.app.ui.common.backgroundGradientHorizontal
import com.pvp.app.ui.common.backgroundGradientLinear
import com.pvp.app.ui.common.backgroundGradientVertical
import com.pvp.app.ui.common.darken
import com.pvp.app.ui.common.lighten
import com.pvp.app.ui.common.showToast
import com.pvp.app.ui.theme.BackgroundUnauthenticated
import com.pvp.app.ui.theme.ButtonTransparent

private val COLORS_GET_STARTED = listOf(
    Color("#d5dd59".toColorInt())
        .darken(.15f),
    Color("#fe5370".toColorInt())
)
    .map { it.lighten(0.75f) }

@Composable
private fun randomizedColors() = listOf(
    Color("#c0f781".toColorInt()),
    Color("#70e4df".toColorInt()),
    Color("#f355ee".toColorInt()),
    Color("#d6db46".toColorInt()),
    Color("#aa98ff".toColorInt()),
    Color("#4380ef".toColorInt())
)
    .shuffled()
    .take(3)
    .map { it.lighten(0.075f) }

@Composable
private fun Authentication(
    authenticate: (isOneTap: Boolean, request: Intent) -> Unit,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    isEnabled: Boolean,
    onClick: (
        launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
        launcherOneTap: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
    ) -> Unit,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
) {
    val textBegin = stringResource(R.string.screen_authentication_button_begin)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                authenticate(false, result.data ?: return@rememberLauncherForActivityResult)
            }
        }
    )

    val launcherOneTap = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                authenticate(true, result.data ?: return@rememberLauncherForActivityResult)
            }
        }
    )

    Column(
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement
    ) {
        Button(
            colors = ButtonTransparent.copy(contentColor = Color.Black),
            contentPadding = PaddingValues(),
            enabled = isEnabled,
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .backgroundGradientHorizontal(
                    colors = COLORS_GET_STARTED,
                    shape = MaterialTheme.shapes.large
                ),
            onClick = {
                onClick(
                    launcher,
                    launcherOneTap
                )
            },
            shape = MaterialTheme.shapes.large
        ) {
            Image(
                contentDescription = "Google authentication button logo",
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.google)
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                style = MaterialTheme.typography.bodyLarge.plus(TextStyle(fontWeight = FontWeight.Bold)),
                text = textBegin,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Composable
fun AuthenticationScreen(
    viewModel: AuthenticationViewModel = hiltViewModel()
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .backgroundGradientVertical(BackgroundUnauthenticated),
        verticalArrangement = Arrangement.Center
    ) {
        val context = LocalContext.current
        val state by viewModel.state.collectAsStateWithLifecycle()
        val textError = stringResource(R.string.screen_authentication_toast_error)

        FeatureCard.cards.forEachIndexed { index, card ->
            FeatureCardBlock(
                radius = card.radius,
                text = card.text,
                textAlign = card.textAlign
            )

            if (index != FeatureCard.cards.size - 1) {
                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        Spacer(modifier = Modifier.size(24.dp))

        when (state.isLoading) {
            true -> {
                ProgressIndicator(modifier = Modifier.size(64.dp))
            }

            else -> {
                Icon(
                    contentDescription = "Authenticate button",
                    imageVector = Icons.Outlined.ArrowDownward,
                    modifier = Modifier.size(36.dp),
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.size(24.dp))

        Authentication(
            authenticate = { isOneTap, request ->
                viewModel.authenticate(
                    request,
                    isOneTap
                ) {
                    if (!it.isSuccess) {
                        context.showToast(message = textError)
                    }
                }
            },
            isEnabled = !state.isLoading,
            onClick = { launcher, launcherOneTap ->
                viewModel.beginSignInRequest(
                    launcher = { launcher.launch(it) },
                    launcherOneTap = { launcherOneTap.launch(it) }
                )
            }
        )
    }
}

@Composable
private fun FeatureCardBlock(
    radius: Float,
    text: String,
    textAlign: TextAlign
) {
    Text(
        color = Color.Black.lighten(0.2f),
        fontSize = 36.sp,
        modifier = Modifier
            .rotate(radius)
            .clip(MaterialTheme.shapes.extraLarge)
            .backgroundGradientLinear(randomizedColors())
            .padding(4.dp),
        style = MaterialTheme.typography.labelLarge,
        text = text,
        textAlign = textAlign
    )
}