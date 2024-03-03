package com.pvp.app.ui.screen.authentication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Dehaze
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pvp.app.ui.router.RouteAuthenticated
import com.pvp.app.ui.router.RouteUnauthenticated
import com.pvp.app.ui.router.RouterAuthenticated
import com.pvp.app.ui.screen.drawer.DrawerScreen
import com.pvp.app.ui.theme.CalendarTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AuthenticatedScreen(
    controller: NavHostController,
    scope: CoroutineScope,
    viewModel: AuthenticationViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = Unit) {
        if (!viewModel.isAuthenticated()) {
            controller.navigate(RouteUnauthenticated.Unauthenticated.route)
        }
    }

    CalendarTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val controllerAuthenticated = rememberNavController()

            val destination = controllerAuthenticated.currentBackStackEntryAsState()
                .value?.destination

            val screen = RouteAuthenticated.routes.find { it.route == destination?.route }
                ?: RouteAuthenticated.Calendar

            val stateDrawer = rememberDrawerState(initialValue = DrawerValue.Closed)

            ModalNavigationDrawer(
                drawerContent = {
                    DrawerScreen(
                        onClick = {
                            controllerAuthenticated.navigate(route) {
                                popUpTo(controllerAuthenticated.graph.startDestinationId) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }

                            scope.launch {
                                stateDrawer.close()
                            }
                        },
                        routes = RouteAuthenticated.routes,
                        screen = screen
                    )
                },
                drawerState = stateDrawer
            ) {
                Scaffold(topBar = {
                    Header(
                        containsPreviousRoute = controllerAuthenticated.previousBackStackEntry != null &&
                                !RouteAuthenticated.routes.contains(screen),
                        onBack = { controllerAuthenticated.navigateUp() },
                        route = screen,
                        scope = scope,
                        state = stateDrawer,
                    )
                }) {
                    RouterAuthenticated(
                        controllerUnauthenticated = controller,
                        controllerAuthenticated = controllerAuthenticated,
                        modifier = Modifier.padding(it),
                        scope = scope
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Header(
    containsPreviousRoute: Boolean,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    route: RouteAuthenticated,
    scope: CoroutineScope,
    state: DrawerState
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
                    /* TODO: Open profile screen */
                }
            ) {
                Icon(
                    contentDescription = "Profile screen icon",
                    imageVector = Icons.Outlined.Person
                )
            }

        },
        modifier = modifier,
        navigationIcon = {
            HeaderNavigationIcon(
                containsPreviousRoute = containsPreviousRoute,
                onBack = onBack,
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
fun HeaderNavigationIcon(
    containsPreviousRoute: Boolean,
    onBack: () -> Unit,
    scope: CoroutineScope,
    state: DrawerState
) {
    if (containsPreviousRoute) {
        IconButton(onClick = onBack) {
            Icon(
                contentDescription = "Arrow back icon",
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack
            )
        }
    } else {
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
}

@Composable
fun HeaderTitle(
    route: RouteAuthenticated
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            contentDescription = route.iconDescription,
            imageVector = route.icon
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            style = MaterialTheme.typography.titleLarge,
            text = stringResource(route.resourceTitleId),
        )
    }
}