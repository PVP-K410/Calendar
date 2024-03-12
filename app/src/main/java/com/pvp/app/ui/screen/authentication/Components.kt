package com.pvp.app.ui.screen.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pvp.app.R
import com.pvp.app.ui.common.backgroundGradientRadial

@Composable
fun AuthenticationBox(
    isSignIn: Boolean,
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
) {
    val buttonFirstLabel = if (isSignIn) {
        stringResource(id = R.string.authentication_sign_in)
    } else {
        stringResource(id = R.string.authentication_sign_up)
    }

    val buttonSecondLabel = if (isSignIn) {
        stringResource(id = R.string.authentication_sign_in_to_sign_up)
    } else {
        stringResource(id = R.string.authentication_sign_up_to_sign_in)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .backgroundGradientRadial(),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight(0.3f)
                .fillMaxWidth(0.75f)
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.primaryContainer),
            verticalArrangement = Arrangement.Center,
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                onClick = if (isSignIn) onSignIn else onSignUp
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Login,
                    contentDescription = "Primary action button"
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = buttonFirstLabel,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                onClick = if (isSignIn) onSignUp else onSignIn
            ) {
                Text(
                    text = buttonSecondLabel,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}