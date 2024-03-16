package com.pvp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.pvp.app.ui.router.Route
import com.pvp.app.ui.screen.layout.LayoutScreenBootstrap
import com.pvp.app.ui.theme.CalendarTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Activity : ComponentActivity() {

    override fun onCreate(stateApp: Bundle?) {
        super.onCreate(stateApp)

        prepareRoutes()

        setContent {
            CalendarTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LayoutScreenBootstrap()
                }
            }
        }
    }

    /**
     * Initialize route collections to avoid lazy initialization problems
     */
    private fun prepareRoutes() {
        run {
            Route.routesAuthenticated
            Route.routesDrawer
            Route.routesUnauthenticated
        }
    }
}