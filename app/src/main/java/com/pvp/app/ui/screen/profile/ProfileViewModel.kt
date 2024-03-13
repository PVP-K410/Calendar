package com.pvp.app.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.AuthenticationService
import com.pvp.app.api.UserService
import com.pvp.app.model.SignOutResult
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: UserService
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    init {
        viewModelScope.launch {
            _user.value = userService
                .getCurrent()
                .first()
        }
    }

    fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            _user.value?.let {
                it.username = newUsername

                userService.merge(it)
            }
        }
    }

    fun updateMass(newMass: Int) {
        viewModelScope.launch {
            _user.value?.let {
                it.mass = newMass

                userService.merge(it)
            }
        }
    }

    fun updateHeight(newHeight: Int) {
        viewModelScope.launch {
            _user.value?.let {
                it.height = newHeight

                userService.merge(it)
            }
        }
    }

    fun signOut(onSignOut: (SignOutResult) -> Unit) {
        viewModelScope.launch {
            authenticationService.signOut(onSignOut)
        }
    }
}