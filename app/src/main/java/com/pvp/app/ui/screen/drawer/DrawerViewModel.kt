package com.pvp.app.ui.screen.drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.AuthenticationService
import com.pvp.app.model.SignOutResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrawerViewModel @Inject constructor(
    private val authenticationService: AuthenticationService
) : ViewModel() {

    fun signOut(
        onSignOut: (SignOutResult) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            authenticationService.signOut(onSignOut)
        }
    }
}