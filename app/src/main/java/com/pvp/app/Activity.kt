package com.pvp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pvp.app.ui.router.Route
import com.pvp.app.ui.screen.calendar.VerticalPagerScreen
import com.pvp.app.ui.screen.layout.LayoutScreenBootstrap
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Activity : ComponentActivity() {

    override fun onCreate(stateApp: Bundle?) {
        super.onCreate(stateApp)

        prepareRoutes()

        setContent {
            LayoutScreenBootstrap()
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