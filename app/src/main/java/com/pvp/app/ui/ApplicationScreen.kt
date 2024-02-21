package com.pvp.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pvp.app.ui.route.Router
import com.pvp.app.ui.route.Routes
import com.pvp.app.ui.theme.CalendarTheme

@Preview
@Composable
fun ApplicationScreen() {
    val controller = rememberNavController()
    val destination = controller.currentBackStackEntryAsState().value?.destination
    val screen = Routes.routes.find { it.route == destination?.route } ?: Routes.Calendar
    val state = rememberSaveable { (mutableStateOf(true)) }

    CalendarTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            Scaffold(
                topBar = {
                    Header(
                        back = { controller.navigateUp() },
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
    back: () -> Unit,
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
                actions = {

                },
                modifier = modifier,
                navigationIcon = {
                    if (containsPreviousRoute) {
                        IconButton(onClick = back) {
                            Icon(
                                contentDescription = null,
                                imageVector = Icons.Filled.ArrowBack
                            )
                        }
                    }
                },
                title = {
                    Text(
                        style = MaterialTheme.typography.titleLarge,
                        text = stringResource(id = route.routeNameId),
                    )
                }
            )
        }
    }
}