package com.pvp.app.ui.screen.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pvp.app.ui.common.backgroundGradientRadial
import com.pvp.app.ui.router.Route
import com.pvp.app.ui.router.Router
import com.pvp.app.ui.theme.CalendarTheme
import kotlinx.coroutines.CoroutineScope

@Composable
fun LayoutScreenUnauthenticated(
    controller: NavHostController,
    isAuthenticated: Boolean,
    isSurveyFilled: Boolean?,
    scope: CoroutineScope
) {
    CalendarTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .backgroundGradientRadial()
                .padding(8.dp)
        ) {
            Router(
                controller = controller,
                destinationStart = if (isAuthenticated && isSurveyFilled == false) {
                    Route.Survey
                } else {
                    Route.SignIn
                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp)
                    .backgroundGradientRadial(),
                routes = Route.routesUnauthenticated,
                scope = scope
            )
        }
    }
}