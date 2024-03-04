package com.pvp.app.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.pvp.app.model.User
import com.pvp.app.ui.common.navigateTo
import com.pvp.app.ui.router.Route

@Composable
fun ProfileBody(
    modifier: Modifier = Modifier,
    user: State<User?>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Email: ${user.value?.email}",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "Points: ${user.value?.points}",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "Height: ${user.value?.height}",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "Mass: ${user.value?.mass}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ProfileFooter(
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onSignOut
        ) {
            Text(
                text = "Sign Out",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ProfileHeader(
    modifier: Modifier = Modifier,
    user: State<User?>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hey, ${user.value?.username}!",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "It is your profile",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ProfileScreen(
    controller: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user = viewModel.user.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        ProfileHeader(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.15f),
            user = user
        )

        ProfileBody(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f),
            user = user
        )

        ProfileFooter(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.15f),
            onSignOut = {
                viewModel.signOut()

                controller.navigateTo(Route.SignIn.route)
            }
        )
    }
}
