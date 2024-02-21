package com.pvp.app.ui.view.vm

import androidx.lifecycle.ViewModel
import com.pvp.app.api.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {

    suspend fun bootstrap() {
        // FIXME: Temporary user creation. Once actual login process is implemented, this will be removed
        userService.getOrResolveCurrent().collect {
            if (it == null) {
                userService.merge(
                    userService.create("fake@email@gmail@com", 0, 0, "current")
                )
            }
        }
    }
}