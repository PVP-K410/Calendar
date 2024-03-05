package com.pvp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.pvp.app.api.AuthenticationService
import com.pvp.app.api.UserService
import com.pvp.app.model.User
import com.pvp.app.ui.common.ProgressIndicator
import com.pvp.app.ui.router.Route
import com.pvp.app.ui.screen.layout.LayoutScreenAuthenticated
import com.pvp.app.ui.screen.layout.LayoutScreenUnauthenticated
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class Activity : ComponentActivity() {

    @Inject
    lateinit var authenticationService: AuthenticationService

    @Inject
    lateinit var userService: UserService

    override fun onCreate(stateApp: Bundle?) {
        super.onCreate(stateApp)

        prepareRoutes()

        setContent {
            val userFirebase by authenticationService.user.collectAsStateWithLifecycle(null)
            var userApp by remember { mutableStateOf<User?>(null) }
            var isLoading by remember { mutableStateOf(userFirebase == null) }

            LaunchedEffect(userFirebase) {
                if (userFirebase != null) {
                    isLoading = true

                    userApp = userService
                        .getCurrent()
                        .first()

                    isLoading = false
                }
            }

            if (userFirebase == null) {
                LayoutScreenUnauthenticated(
                    controller = rememberNavController(),
                    scope = rememberCoroutineScope()
                )

                return@setContent
            }

            if (isLoading) {
                ProgressIndicator()

                return@setContent
            }

            LayoutScreenAuthenticated(
                controller = rememberNavController(),
                scope = rememberCoroutineScope(),
                user = userApp
            )
        }
    }

    /**
     * Initialize routes collections to avoid lazy initialization problems
     */
    private fun prepareRoutes() {
        run {
            Route.routesAuthenticated
            Route.routesDrawer
            Route.routesUnauthenticated
        }
    }
}