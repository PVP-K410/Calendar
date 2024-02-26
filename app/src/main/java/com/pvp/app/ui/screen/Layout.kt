package com.pvp.app.ui.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Dehaze
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pvp.app.ui.Route
import com.pvp.app.ui.Router
import com.pvp.app.ui.screen.drawer.DrawerScreen
import com.pvp.app.ui.theme.CalendarTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Header(
    containsPreviousRoute: Boolean,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    route: Route,
    scope: CoroutineScope,
    state: DrawerState
) {
    TopAppBar(
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
                imageVector = Icons.Filled.ArrowBack
            )
        }
    } else {
        IconButton(onClick = {
            scope.launch {
                state.apply {
                    if (isClosed) open() else close()
                }
            }
        }) {
            Icon(
                contentDescription = "Navigation drawer icon",
                imageVector = Icons.Filled.Dehaze
            )
        }
    }
}

@Composable
fun HeaderTitle(
    route: Route
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

@Composable
fun Layout() {
    val controller = rememberNavController()
    val destination = controller.currentBackStackEntryAsState().value?.destination
    val scope = rememberCoroutineScope()
    val screen = Route.routes.find { it.route == destination?.route } ?: Route.Calendar
    val state = rememberDrawerState(initialValue = DrawerValue.Closed)

    CalendarTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModalNavigationDrawer(
                drawerContent = {
                    DrawerScreen(
                        onClick = {
                            controller.navigate(route) {
                                popUpTo(controller.graph.startDestinationId) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }

                            scope.launch {
                                state.close()
                            }
                        },
                        routes = Route.routes,
                        screen = screen
                    )
                },
                drawerState = state
            ) {
                Scaffold(topBar = {
                    Header(
                        containsPreviousRoute = controller.previousBackStackEntry != null &&
                                !Route.routes.contains(screen),
                        onBack = { controller.navigateUp() },
                        route = screen,
                        scope = scope,
                        state = state,
                    )
                }) {
                    Router(
                        controller = controller,
                        modifier = Modifier.padding(it)
                    )
                }
            }
        }
    }
}