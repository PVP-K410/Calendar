package com.pvp.app.ui.screen.profile

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.Configuration
import com.pvp.app.api.ExperienceService
import com.pvp.app.api.UserService
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val configuration: Configuration,
    private val experienceService: ExperienceService,
    private val userService: UserService
) : ViewModel() {

    private val _state = MutableStateFlow(
        ProfileState(
            isLoading = true,
            user = User(),
            userAvatar = ImageBitmap(1, 1)
        )
    )
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            userService.user
                .map { user ->
                    _state.update {
                        ProfileState(
                            isLoading = false,
                            user = user ?: User(),
                            userAvatar = userService.resolveAvatar(user?.email ?: "")
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    fun update(
        function: (User) -> Unit
    ) {
        viewModelScope.launch {
            val user = _state.value.user.copy()

            function(user)

            userService.merge(_state.value.user)
        }
    }

    fun <T> fromConfiguration(function: (Configuration) -> T): T {
        return function(configuration)
    }

    /**
     * @return total experience required to level up
     */
    fun getExperienceRequired(): Int {
        (1..10)
            .plus(50)
            .plus(70)
            .plus(100)
            .forEach {
                val exp = experienceService.experienceOf(it)

                println("$it -> $exp exp")
                println("      ^ -> ${experienceService.levelOf(exp)} level")
            }

        return experienceService.experienceOf(_state.value.user.level + 1)
    }
}

data class ProfileState(
    val isLoading: Boolean,
    val user: User,
    val userAvatar: ImageBitmap
)