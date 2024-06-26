package com.pvp.app.ui.screen.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.R
import com.pvp.app.ui.common.ButtonConfirm
import com.pvp.app.ui.common.LocalShowSnackbar
import com.pvp.app.ui.common.RouteTitle
import com.pvp.app.ui.router.Route
import com.pvp.app.ui.screen.feedback.FeedbackCreationDialog

@Composable
private fun Body(
    modifier: Modifier = Modifier,
    onClick: Route.() -> Unit,
    routes: List<Route>,
    route: Route
) {
    LazyColumn(modifier = modifier) {
        items(routes) {
            BodyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        color = if (route.path == it.path) {
                            MaterialTheme.colorScheme.surfaceContainerHighest
                        } else {
                            Color.Transparent
                        },
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable(
                        enabled = route.path != it.path,
                        onClick = { onClick.invoke(it) }
                    )
                    .padding(8.dp),
                route = it
            )
        }
    }
}

@Composable
private fun BodyRow(
    modifier: Modifier = Modifier,
    route: Route
) {
    val options = remember(route) {
        when (route) {
            is Route.Node -> {
                route.options
            }

            is Route.Root -> {
                route.start.options
            }
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (options.icon != null) {
            options.icon.invoke()

            Spacer(modifier = Modifier.width(16.dp))
        }

        (options.title ?: { RouteTitle(title = "Title not defined") })()
    }
}

@Composable
private fun Footer(
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit
) {
    val localeButton = stringResource(R.string.drawer_button_sign_out)
    val localeConfirmation = stringResource(R.string.drawer_button_sign_out_confirmation)
    var isFeedbackDialogOpen by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.drawer_button_feedback),
                modifier = Modifier
                    .clickable { isFeedbackDialogOpen = true },
                style = MaterialTheme.typography.bodyLarge
            )

            ButtonConfirm(
                content = {
                    Text(
                        text = localeButton,
                        color = MaterialTheme.colorScheme.surface
                    )
                },
                contentAlignment = Alignment.BottomEnd,
                confirmationButtonContent = { Text(text = localeButton) },
                confirmationTitle = { Text(text = localeConfirmation) },
                onConfirm = { onSignOut() },
                shape = MaterialTheme.shapes.extraLarge
            )
        }
    }

    FeedbackCreationDialog(
        isOpen = isFeedbackDialogOpen,
        onClose = { isFeedbackDialogOpen = false }
    )
}

@Composable
private fun Header(modifier: Modifier = Modifier) {
    val localeApplication = stringResource(R.string.application_name)
    val localeMotto = stringResource(R.string.drawer_application_motto)

    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                style = MaterialTheme.typography.displaySmall,
                text = localeApplication
            )

            Text(
                style = MaterialTheme.typography.titleLarge,
                text = localeMotto
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
    val showSnackbar = LocalShowSnackbar.current

    ModalDrawerSheet(drawerShape = RectangleShape) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            val textSignOut = stringResource(R.string.drawer_button_sign_out_error)

            Header(
                Modifier
                    .fillMaxWidth()
                    .weight(0.1f)
            )

            Spacer(modifier = Modifier.size(8.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.size(16.dp))

            Body(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f),
                onClick = onClick,
                route = route,
                routes = routes
            )

            Footer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f),
                onSignOut = {
                    viewModel.signOut {
                        if (!it.isSuccess) {
                            showSnackbar(textSignOut)
                        }
                    }
                }
            )
        }
    }
}