package com.pvp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.pvp.app.api.UserService
import com.pvp.app.model.User
import com.pvp.app.ui.screen.Layout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class Activity : ComponentActivity() {

    @Inject
    lateinit var userService: UserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bootstrap()

        setContent {
            Layout()
        }
    }

    private fun bootstrap() {
        lifecycleScope.launch {
            // FIXME: Temporary user creation. Once actual login process is implemented, this will be refactored
            userService.getCurrent().collect {
                if (it == null) {
                    userService.merge(
                        User("fake@email@gmail@com", 0, 0, 0, "current")
                    )
                }
            }
        }
    }
}