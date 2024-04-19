@file:OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)

package com.pvp.app.ui.screen.layout

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pvp.app.R
import com.pvp.app.model.Decoration
import com.pvp.app.model.Reward
import com.pvp.app.ui.common.AsyncImage
import com.pvp.app.ui.common.lighten
import com.pvp.app.ui.common.navigateWithPopUp
import com.pvp.app.ui.router.Route
import com.pvp.app.ui.router.Router
import com.pvp.app.ui.screen.calendar.TaskCreateDialog
import com.pvp.app.ui.screen.drawer.DrawerScreen
import com.pvp.app.ui.screen.profile.ProfileScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
private fun resolveTitle(
    route: Route,
    statePager: PagerState
): String {
    return when (statePager.currentPage) {
        1 -> stringResource(R.string.route_profile)
        else -> stringResource(route.resourceTitleId)
    }
}

private fun supportsTaskCreation(
    route: Route,
    state: PagerState
): Boolean {
    return when {
        state.currentPage == 1 -> false
        else -> when (route) {
            Route.Calendar -> true
            else -> false
        }
    }
}

/**
 * Switches the screen to the given page, where 0 is main content and 1 is profile content.
 * Edited animation: **tween(500)** for duration.
 */
private fun switchScreen(
    page: Int,
    scope: CoroutineScope,
    state: PagerState
) {
    require(page in 0..1) {
        "Page must be 0 or 1"
    }

    scope.launch {
        state.animateScrollToPage(
            page,
            animationSpec = tween(500)
        )
    }
}

/**
 * Switches the screen to the given path. Used by the drawer screen.
 */
private fun switchScreen(
    controller: NavHostController,
    path: String,
    scope: CoroutineScope,
    stateDrawer: DrawerState,
    statePager: PagerState
) {
    controller.navigateWithPopUp(path)

    scope.launch {
        stateDrawer.close()
    }

    switchScreen(
        0,
        scope,
        statePager
    )
}

private fun toggleNavigationDrawer(
    scope: CoroutineScope,
    state: DrawerState
) {
    scope.launch {
        state.apply {
            if (isOpen) {
                close()
            } else {
                open()
            }
        }
    }
}

@Composable
private fun Content(
    controller: NavHostController,
    modifier: Modifier = Modifier,
    scope: CoroutineScope,
    state: PagerState
) {
    HorizontalPager(
        modifier = modifier,
        state = state
    ) {
        when (it) {
            0 -> Router(
                controller = controller,
                destinationStart = Route.Calendar,
                routes = Route.routesAuthenticated,
                scope = scope
            )

            1 -> ProfileScreen()
        }
    }
}

@Composable
fun FloatingActionButton(
    onClick: () -> Unit,
) {
    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.primary,
        onClick = onClick,
        shape = CircleShape
    ) {
        Icon(
            contentDescription = "Add task",
            imageVector = Icons.Outlined.Add
        )
    }
}

@Composable
private fun Header(
    colorAvatarBorder: Color = MaterialTheme.colorScheme.primaryContainer,
    colors: TopAppBarColors = TopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary,
        scrolledContainerColor = MaterialTheme.colorScheme.onPrimary,
        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
        titleContentColor = MaterialTheme.colorScheme.onPrimary,
        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
    ),
    onClickNavigation: () -> Unit,
    onClickProfile: () -> Unit,
    title: String,
    userAvatar: ImageBitmap
) {
    CenterAlignedTopAppBar(
        actions = {
            IconButton(onClick = onClickProfile) {
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
        modifier = Modifier
            .height(64.dp)
            .padding(8.dp)
            .clip(shape = MaterialTheme.shapes.extraLarge),
        navigationIcon = {
            IconButton(onClick = onClickNavigation) {
                Icon(
                    contentDescription = "Navigation drawer icon",
                    imageVector = Icons.Outlined.Dehaze
                )
            }
        },
        title = {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.titleLarge,
                    text = title,
                )
            }
        }
    )
}

@Composable
fun LayoutScreenAuthenticated(
    controller: NavHostController,
    scope: CoroutineScope,
    viewModel: LayoutViewModel = hiltViewModel()
) {
    val destination = controller.currentBackStackEntryAsState().value?.destination

    val route = Route.routesAuthenticated
        .find { it.path == destination?.route }
        ?: Route.Calendar

    val stateDrawer = rememberDrawerState(DrawerValue.Closed)

    val statePager = rememberPagerState(
        initialPage = 0,
        pageCount = { 2 }
    )

    var isRewardDialogOpen by remember { mutableStateOf(false) }
    val toggleRewardDialog = remember { { isRewardDialogOpen = !isRewardDialogOpen } }

    if (viewModel.state.collectAsStateWithLifecycle().value.needsStreakReward) {
        LaunchedEffect(null) {
            viewModel.giveReward()

            isRewardDialogOpen = true
        }
    }

    val reward = viewModel.reward.collectAsStateWithLifecycle().value

    RewardDialog(
        isOpen = isRewardDialogOpen,
        onClose = toggleRewardDialog,
        reward = reward
    )

    ModalNavigationDrawer(
        drawerContent = {
            DrawerScreen(
                {
                    switchScreen(
                        controller,
                        path,
                        scope,
                        stateDrawer,
                        statePager
                    )
                },
                if (statePager.currentPage == 1) Route.None else route,
                Route.routesDrawer
            )
        },
        drawerState = stateDrawer
    ) {
        var isTaskDialogOpen by remember { mutableStateOf(false) }
        val toggleTaskDialog = remember { { isTaskDialogOpen = !isTaskDialogOpen } }

        Scaffold(
            floatingActionButton = {
                if (
                    supportsTaskCreation(
                        route,
                        statePager
                    )
                ) {
                    FloatingActionButton(toggleTaskDialog)
                }
            },
            floatingActionButtonPosition = FabPosition.End,
            topBar = {
                val stateLayout by viewModel.state.collectAsStateWithLifecycle()

                Header(
                    onClickNavigation = {
                        toggleNavigationDrawer(
                            scope,
                            stateDrawer
                        )
                    },
                    onClickProfile = {
                        if (statePager.currentPage == 1) {
                            return@Header
                        }

                        switchScreen(
                            1,
                            scope,
                            statePager
                        )
                    },
                    title = resolveTitle(
                        route,
                        statePager
                    ),
                    userAvatar = stateLayout.avatar
                )
            }
        ) {
            Content(
                controller = controller,
                modifier = Modifier.padding(it),
                scope = scope,
                state = statePager
            )

            if (
                supportsTaskCreation(
                    route,
                    statePager
                )
            ) {
                TaskCreateDialog(
                    onClose = toggleTaskDialog,
                    isOpen = isTaskDialogOpen,
                    shouldCloseOnSubmit = true
                )
            }
        }
    }
}

@Composable
private fun RewardDialog(
    isOpen: Boolean,
    onClose: () -> Unit,
    reward: Reward
) {
    if (!isOpen) {
        return
    }

    Dialog(onDismissRequest = onClose) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    style = MaterialTheme.typography.headlineSmall,
                    text = "You've earned a reward!"
                )

                IconButton(onClick = onClose) {
                    Icon(
                        contentDescription = "Reward dialog close button",
                        imageVector = Icons.Filled.Close
                    )
                }
            }

            if (reward.points > 0) {
                Text(
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.titleMedium,
                    text = "${reward.points} points!"
                )
            }

            if (reward.experience > 0) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    text = "${reward.experience} experience!"
                )
            }

            reward.decoration?.let {
                DecorationCard(decoration = it)
            }
        }
    }
}

@Composable
private fun DecorationCard(decoration: Decoration) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.titleMedium,
            text = "${decoration.name} decoration!"
        )

        AsyncImage(
            contentDescription = "Decoration ${decoration.name} image",
            modifier = Modifier
                .size(96.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .background(
                    color = MaterialTheme.colorScheme.inverseOnSurface.lighten(),
                    shape = MaterialTheme.shapes.extraSmall
                ),
            url = decoration.imageRepresentativeUrl
        )
    }
}
