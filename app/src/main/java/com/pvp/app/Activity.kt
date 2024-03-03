package com.pvp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.rememberNavController
import com.pvp.app.ui.router.RouterUnauthenticated
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Activity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val controller = rememberNavController()
            val scope = rememberCoroutineScope()

            RouterUnauthenticated(
                controller = controller,
                scope = scope
            )
        }
    }
}