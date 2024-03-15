package com.pvp.app.ui.screen.profile

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.AuthenticationService
import com.pvp.app.api.UserService
import com.pvp.app.model.Ingredient
import com.pvp.app.model.SignOutResult
import com.pvp.app.model.SportActivity
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
    private val authenticationService: AuthenticationService,
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
            userService
                .getCurrent()
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

    fun updateUserInformation(
        newUsername: String? = null,
        newMass: Int? = null,
        newHeight: Int? = null,
        newActivityFilters: List<String>? = null,
        newIngredientFilters: List<String>? = null
    ) {
        viewModelScope.launch {
            newUsername?.let {
                _state.value.user.username = it
            }

            newMass?.let {
                _state.value.user.mass = it
            }

            newHeight?.let {
                _state.value.user.height = it
            }

            newActivityFilters?.let {
                _state.value.user.activities =
                    newActivityFilters.mapNotNull { SportActivity.fromTitle(it) }
            }

            newIngredientFilters?.let {
                _state.value.user.ingredients =
                    newIngredientFilters.mapNotNull { Ingredient.fromTitle(it) }
            }

            userService.merge(_state.value.user)
        }
    }

    fun signOut(onSignOut: (SignOutResult) -> Unit) {
        viewModelScope.launch {
            authenticationService.signOut(onSignOut)
        }
    }
}

data class ProfileState(
    val isLoading: Boolean,
    val user: User,
    val userAvatar: ImageBitmap
)