package com.pvp.app.ui.screen.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.R
import com.pvp.app.ui.common.ButtonConfirm
import com.pvp.app.ui.common.showToast
import com.pvp.app.ui.router.Route

@Composable
private fun DrawerBody(
    modifier: Modifier = Modifier,
    onClick: Route.() -> Unit,
    routes: List<Route>,
    route: Route
) {
    LazyColumn(modifier = modifier) {
        items(routes) {
            DrawerBodyRow(
                modifier = Modifier
                    .height(48.dp)
                    .background(
                        color = if (route.path == it.path) {
                            MaterialTheme.colorScheme.surfaceContainerHighest
                        } else {
                            Color.Transparent
                        },
                        shape = MaterialTheme.shapes.extraSmall
                    )
                    .padding(8.dp)
                    .clickable(
                        enabled = route.path != it.path,
                        onClick = { onClick.invoke(it) },
                        onClickLabel = "Navigate to ${stringResource(it.resourceTitleId)}",
                        role = Role.Button
                    ),
                route = it
            )
        }
    }
}

@Composable
private fun DrawerBodyRow(
    modifier: Modifier = Modifier,
    route: Route
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        route.icon?.let {
            Icon(
                contentDescription = route.iconDescription,
                imageVector = it
            )

            Spacer(modifier = Modifier.width(16.dp))
        }

        Text(
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
            text = stringResource(id = route.resourceTitleId)
        )
    }
}

@Composable
private fun DrawerFooter(
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit
) {
    val signOut = stringResource(R.string.screen_profile_button_sign_out)

    Column(modifier = modifier) {
        ButtonConfirm(
            modifier = Modifier
                .padding(
                    top = 20.dp,
                    bottom = 10.dp,
                    end = 10.dp
                )
                .fillMaxSize(),
            buttonContent = { Text(text = signOut) },
            buttonContentAlignment = Alignment.BottomEnd,
            confirmationButtonContent = { Text(text = signOut) },
            confirmationTitle = { Text(text = "Are you sure you want to sign out?") },
            onConfirm = { onSignOut() }
        )
    }
}

@Composable
private fun DrawerHeader(
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                style = MaterialTheme.typography.displaySmall,
                text = "Calendar"
            )

            Text(
                style = MaterialTheme.typography.titleLarge,
                text = "Schedule Your Day"
            )
        }
    }
}

@Composable
fun DrawerScreen(
    onClick: Route.() -> Unit,
    route: Route,
    routes: List<Route>,
    viewModel: DrawerViewModel = hiltViewModel()
) {
    ModalDrawerSheet(drawerShape = RectangleShape) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            val textSignOut = stringResource(R.string.screen_profile_toast_error)

            DrawerHeader(
                Modifier
                    .fillMaxWidth()
                    .weight(0.1f)
            )

            Spacer(modifier = Modifier.size(4.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.size(4.dp))

            DrawerBody(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f),
                onClick = onClick,
                route = route,
                routes = routes
            )

            val context = LocalContext.current

            DrawerFooter(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f),
                onSignOut = {
                    viewModel.signOut {
                        if (!it.isSuccess) {
                            context.showToast(message = textSignOut)
                        }
                    }
                }
            )
        }
    }
}
