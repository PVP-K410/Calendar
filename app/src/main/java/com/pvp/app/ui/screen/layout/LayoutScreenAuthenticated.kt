package com.pvp.app.ui.screen.layout


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Dehaze
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pvp.app.ui.common.navigateWithPopUp
import com.pvp.app.ui.router.Route
import com.pvp.app.ui.router.Router
import com.pvp.app.ui.screen.calendar.CreateTaskDialog
import com.pvp.app.ui.screen.drawer.DrawerScreen
import com.pvp.app.ui.theme.CalendarTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Header(
    colorAvatarBorder: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    controller: NavHostController,
    modifier: Modifier = Modifier,
    route: Route,
    scope: CoroutineScope,
    state: DrawerState,
    userAvatar: ImageBitmap
) {
    CenterAlignedTopAppBar(
        actions = {
            IconButton(onClick = { controller.navigateWithPopUp(Route.Profile.path) }) {
                Image(
                    contentDescription = "Profile screen icon",
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = colors.actionIconContentColor)
                        .border(
                            width = 1.dp,
                            color = colorAvatarBorder,
                            shape = RoundedCornerShape(32.dp)
                        ),
                    painter = BitmapPainter(userAvatar)
                )
            }
        },
        colors = colors,
        modifier = modifier,
        navigationIcon = {
            HeaderNavigationIcon(
                scope = scope,
                state = state
            )
        },
        title = {
            HeaderTitle(
                modifier = Modifier.fillMaxHeight(),
                route = route
            )
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
    modifier: Modifier = Modifier,
    route: Route
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            style = MaterialTheme.typography.titleLarge,
            fontSize = 24.sp,
            text = stringResource(route.resourceTitleId),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
                        onClick = {
                            controller.navigateWithPopUp(path)

                            scope.launch {
                                stateDrawer.close()
                            }
                        },
                        route = route,
                        routes = Route.routesDrawer
                    )
                },
                drawerState = stateDrawer
            ) {
                var isOpen by remember { mutableStateOf(false) }
                val stateLayout by viewModel.state.collectAsStateWithLifecycle()
                val toggleDialog = remember { { isOpen = !isOpen } }

                Scaffold(
                    floatingActionButton = {
                        if (!supportsTaskCreation(route)) {
                            return@Scaffold
                        }

                        FloatingActionButton(
                            containerColor = MaterialTheme.colorScheme.primary,
                            onClick = toggleDialog,
                            shape = CircleShape
                        ) {
                            Icon(
                                contentDescription = "Add task",
                                imageVector = Icons.Outlined.Add
                            )
                        }
                    },
                    floatingActionButtonPosition = FabPosition.End,
                    topBar = {
                        Header(
                            colorAvatarBorder = MaterialTheme.colorScheme.primaryContainer,
                            colors = TopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                scrolledContainerColor = MaterialTheme.colorScheme.onPrimary,
                                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            controller = controller,
                            modifier = Modifier
                                .height(64.dp)
                                .padding(8.dp)
                                .clip(shape = MaterialTheme.shapes.extraLarge),
                            route = route,
                            scope = scope,
                            state = stateDrawer,
                            userAvatar = stateLayout.userAvatar!!
                        )
                    }
                ) {
                    Router(
                        controller = controller,
                        destinationStart = Route.Calendar,
                        modifier = Modifier.padding(it),
                        routes = Route.routesAuthenticated,
                        scope = scope
                    )

                    if (!supportsTaskCreation(route)) {
                        return@Scaffold
                    }

                    CreateTaskDialog(
                        onClose = toggleDialog,
                        isOpen = isOpen,
                        shouldCloseOnSubmit = true
                    )
                }
            }
        }
    }
}

private fun supportsTaskCreation(route: Route): Boolean {
    return when (route) {
        Route.Calendar, Route.MonthlyCalendar -> true
        else -> false
    }
}