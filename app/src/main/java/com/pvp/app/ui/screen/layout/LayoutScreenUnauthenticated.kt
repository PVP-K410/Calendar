package com.pvp.app.ui.screen.layout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.pvp.app.ui.router.Route
import com.pvp.app.ui.router.Router
import com.pvp.app.ui.theme.CalendarTheme
import kotlinx.coroutines.CoroutineScope

@Composable
fun LayoutScreenUnauthenticated(
    controller: NavHostController,
    scope: CoroutineScope
) {
    CalendarTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Router(
                controller = controller,
                destinationStart = Route.SignIn,
                routes = Route.routesUnauthenticated,
                scope = scope
            )
        }
    }
}