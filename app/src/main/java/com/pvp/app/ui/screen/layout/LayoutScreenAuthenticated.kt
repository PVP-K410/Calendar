package com.pvp.app.ui.screen.layout


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dehaze
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pvp.app.ui.common.navigateWithPopUp
import com.pvp.app.ui.router.Route
import com.pvp.app.ui.router.Router
import com.pvp.app.ui.screen.drawer.DrawerScreen
import com.pvp.app.ui.theme.CalendarTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LayoutScreenAuthenticated(
    controller: NavHostController,
    scope: CoroutineScope,
    viewModel: LayoutViewModel = hiltViewModel()
) {
    CalendarTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val destination = controller.currentBackStackEntryAsState().value?.destination

            val route = Route.routesAuthenticated
                .find { it.path == destination?.route }
                ?: Route.SignIn

            val stateDrawer = rememberDrawerState(initialValue = DrawerValue.Closed)

            ModalNavigationDrawer(
                drawerContent = {
                    DrawerScreen(
                        modifier = Modifier.fillMaxWidth((2 / 3.0).toFloat()),
                        onClick = {
                            controller.navigateWithPopUp(this.path)

                            scope.launch {
                                stateDrawer.close()
                            }
                        },
                        route = route,
                        routes = Route.routesDrawer,
                    )
                },
                drawerState = stateDrawer
            ) {
                val stateLayout by viewModel.state.collectAsStateWithLifecycle()

                Scaffold(topBar = {
                    Header(
                        controller = controller,
                        route = route,
                        scope = scope,
                        state = stateDrawer,
                        userAvatar = stateLayout.userAvatar!!
                    )
                }) {
                    Router(
                        controller = controller,
                        destinationStart = Route.Calendar,
                        modifier = Modifier.padding(it),
                        routes = Route.routesAuthenticated,
                        scope = scope
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Header(
    controller: NavHostController,
    modifier: Modifier = Modifier,
    route: Route,
    scope: CoroutineScope,
    state: DrawerState,
    userAvatar: ImageBitmap
) {
    TopAppBar(
        actions = {
            IconButton(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .border(
                        BorderStroke(2.dp, MaterialTheme.colorScheme.surfaceContainerHighest),
                        RoundedCornerShape(36.dp)
                    ),
                onClick = {
                    controller.navigateWithPopUp(Route.Profile.path)
                }
            ) {
                Image(
                    contentDescription = "Profile screen icon",
                    painter = BitmapPainter(userAvatar),
                )
            }
        },
        modifier = modifier,
        navigationIcon = {
            HeaderNavigationIcon(
                scope = scope,
                state = state
            )
        },
        title = {
            HeaderTitle(route = route)
        }
    )
}

@Composable
private fun HeaderNavigationIcon(
    scope: CoroutineScope,
    state: DrawerState
) {
    IconButton(onClick = {
        scope.launch {
            state.apply {
                if (isClosed) {
                    open()
                } else {
                    close()
                }
            }
        }
    }) {
        Icon(
            contentDescription = "Navigation drawer icon",
            imageVector = Icons.Outlined.Dehaze
        )
    }
}

@Composable
private fun HeaderTitle(
    route: Route
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        route.icon?.let {
            Icon(
                contentDescription = route.iconDescription,
                imageVector = it
            )

            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            style = MaterialTheme.typography.titleLarge,
            text = stringResource(route.resourceTitleId),
        )
    }
}