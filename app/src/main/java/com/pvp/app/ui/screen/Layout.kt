package com.pvp.app.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pvp.app.ui.Router
import com.pvp.app.ui.Routes
import com.pvp.app.ui.theme.CalendarTheme

@Composable
fun Layout() {
    val controller = rememberNavController()
    val destination = controller.currentBackStackEntryAsState().value?.destination
    val screen = Routes.routes.find { it.route == destination?.route } ?: Routes.Calendar
    val state = rememberSaveable { mutableStateOf(true) }

    CalendarTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            Scaffold(
                topBar = {
                    Header(
                        backHandler = { controller.navigateUp() },
                        containsPreviousRoute = controller.previousBackStackEntry != null &&
                                !Routes.routes.contains(screen),
                        route = screen,
                        state = state.value
                    )
                }
            ) {
                Router(
                    controller = controller,
                    modifier = Modifier.padding(it)
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Header(
    backHandler: () -> Unit,
    containsPreviousRoute: Boolean,
    modifier: Modifier = Modifier,
    route: Routes,
    state: Boolean
) {
    Surface {
        AnimatedVisibility(
            enter = EnterTransition.None,
            exit = ExitTransition.None,
            visible = state
        ) {
            TopAppBar(
                modifier = modifier,
                navigationIcon = {
                    if (containsPreviousRoute) {
                        IconButton(onClick = backHandler) {
                            Icon(
                                contentDescription = null,
                                imageVector = Icons.Filled.ArrowBack
                            )
                        }
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            contentDescription = null,
                            imageVector = route.icon
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            style = MaterialTheme.typography.titleLarge,
                            text = stringResource(id = route.routeNameId)
                        )
                    }
                }
            )
        }
    }
}