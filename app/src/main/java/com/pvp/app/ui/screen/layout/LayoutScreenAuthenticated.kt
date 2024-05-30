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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Dehaze
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pvp.app.model.Streak
import com.pvp.app.ui.common.LocalHorizontalPagerSettled
import com.pvp.app.ui.common.LocalRouteOptions
import com.pvp.app.ui.common.LocalRouteOptionsApplier
import com.pvp.app.ui.common.lighten
import com.pvp.app.ui.common.navigateWithPopUp
import com.pvp.app.ui.router.Route
import com.pvp.app.ui.router.Router
import com.pvp.app.ui.router.Routes
import com.pvp.app.ui.screen.drawer.DrawerScreen
import com.pvp.app.ui.screen.profile.ProfileScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Should execute logic for pressing left-corner's navigation button in the header
 */
private fun navigate(
    controller: NavHostController,
    scope: CoroutineScope,
    showBack: Boolean,
    state: DrawerState
) {
    if (showBack) {
        controller.popBackStack()

        return
    }

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

/**
 * @return Pair of [Route] object and [NavDestination] object.
 */
@Composable
private fun rememberRoute(controller: NavHostController): Pair<Route, NavDestination?> {
    val destination = controller.currentBackStackEntryAsState().value?.destination

    return remember(destination) {
        Pair(
            Routes.authenticated.find {
                it.path == destination?.route ||
                        it.path == destination?.parent?.route
            } ?: Routes.Dashboard,
            destination
        )
    }
}

@Composable
private fun showBackNavigation(
    destination: NavDestination,
    page: Int
): Boolean {
    return remember(
        destination,
        page
    ) {
        page == 0 &&
                destination.route !in Routes.drawer.map { it.path } &&
                destination.parent?.startDestinationRoute != destination.route
    }
}

/**
 * Switches the screen to the given page, where 0 is main content and 1 is profile content.
 * Edited animation: **tween(500)** for duration.
 */
private fun switchPage(
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
private fun switchPage(
    controller: NavHostController,
    path: String,
    scope: CoroutineScope,
    stateDrawer: DrawerState,
    statePager: PagerState
) {
    controller.navigateWithPopUp(path)

    scope.launch { stateDrawer.close() }

    switchPage(
        0,
        scope,
        statePager
    )
}

@Composable
private fun Content(
    controller: NavHostController,
    paddingValues: PaddingValues,
    state: PagerState
) {
    val padding = remember(paddingValues) {
        paddingValues.calculateTopPadding()
    }

    val modifier = Modifier
        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        .offset(y = padding)
        .padding(bottom = padding)

    HorizontalPager(state = state) {
        when (it) {
            0 -> Router(
                controller = controller,
                start = Routes.Dashboard,
                routeModifier = modifier,
                routes = Routes.authenticated
            )

            1 -> ProfileScreen(modifier = modifier)
        }
    }
}

@Composable
private fun Header(
    avatar: ImageBitmap,
    colorAvatarBorder: Color = MaterialTheme.colorScheme.surfaceContainerHighest.lighten(0.08f),
    colors: TopAppBarColors = TopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary,
        scrolledContainerColor = MaterialTheme.colorScheme.onPrimary,
        navigationIconContentColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.surface,
        actionIconContentColor = MaterialTheme.colorScheme.surfaceContainerHighest.lighten(0.08f)
    ),
    onClickNavigation: () -> Unit,
    onClickProfile: () -> Unit,
    showBackNavigation: Boolean,
    title: @Composable () -> Unit
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
                    painter = BitmapPainter(avatar)
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
                if (showBackNavigation) {
                    Icon(
                        contentDescription = "Navigate back icon",
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack
                    )
                } else {
                    Icon(
                        contentDescription = "Navigation drawer icon",
                        imageVector = Icons.Outlined.Dehaze
                    )
                }
            }
        },
        title = {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) { title() }
        }
    )
}

@Composable
fun LayoutScreenAuthenticated(
    controller: NavHostController,
    viewModel: LayoutViewModel = hiltViewModel()
) {
    var options by remember { mutableStateOf(Route.Options.None) }
    val route = rememberRoute(controller)
    val scope = rememberCoroutineScope()
    val stateDrawer = rememberDrawerState(DrawerValue.Closed)
    val stateLayout by viewModel.state.collectAsStateWithLifecycle()

    val statePager = rememberPagerState(
        initialPage = 0,
        pageCount = { 2 }
    )

    var isRewardDialogOpen by remember { mutableStateOf(false) }
    val toggleRewardDialog = remember { { isRewardDialogOpen = !isRewardDialogOpen } }

    if (stateLayout.needsStreakReward) {
        LaunchedEffect(null) {
            viewModel.giveReward()

            isRewardDialogOpen = true
        }
    }

    val reward = viewModel.reward.collectAsStateWithLifecycle().value

    RewardDialog(
        isOpen = isRewardDialogOpen,
        onClose = toggleRewardDialog,
        reward = reward,
        streak = stateLayout.user?.streak ?: Streak()
    )

    ModalNavigationDrawer(
        drawerContent = {
            DrawerScreen(
                {
                    switchPage(
                        controller,
                        path,
                        scope,
                        stateDrawer,
                        statePager
                    )
                },
                if (statePager.currentPage == 1) Routes.None else route.first,
                Routes.drawer
            )
        },
        drawerState = stateDrawer,
        gesturesEnabled = stateDrawer.isOpen
    ) {
        Scaffold(
            topBar = {
                val showBack = route.second != null && showBackNavigation(
                    route.second!!,
                    statePager.currentPage
                )

                Header(
                    avatar = stateLayout.avatar,
                    onClickNavigation = {
                        navigate(
                            controller,
                            scope,
                            showBack,
                            stateDrawer
                        )
                    },
                    onClickProfile = {
                        if (statePager.currentPage == 1) {
                            return@Header
                        }

                        switchPage(
                            1,
                            scope,
                            statePager
                        )
                    },
                    showBackNavigation = showBack,
                    title = { options.title?.invoke() }
                )
            }
        ) { padding ->
            CompositionLocalProvider(
                LocalHorizontalPagerSettled provides !statePager.isScrollInProgress,
                LocalRouteOptions provides options,
                LocalRouteOptionsApplier provides { options = it(options) }
            ) {
                Content(
                    controller = controller,
                    paddingValues = padding,
                    state = statePager
                )
            }
        }
    }
}