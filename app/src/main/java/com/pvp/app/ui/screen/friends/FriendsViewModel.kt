package com.pvp.app.ui.screen.friends

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.FriendService
import com.pvp.app.api.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendService: FriendService,
    userService: UserService
) : ViewModel() {
    val toastMessage = mutableStateOf<String?>(null)

    val user = userService.user.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun addFriend(friendEmail: String) {
        viewModelScope.launch {
            val user = user.value ?: return@launch
            toastMessage.value = friendService.addFriend(
                user,
                friendEmail
            )
        }
    }

    fun acceptFriendRequest(friendEmail: String) {
        viewModelScope.launch {
            val user = user.value ?: return@launch
            toastMessage.value = friendService.acceptFriendRequest(
                user,
                friendEmail
            )
        }
    }

    fun denyFriendRequest(friendEmail: String) {
        viewModelScope.launch {
            val user = user.value ?: return@launch
            toastMessage.value = friendService.denyFriendRequest(
                user,
                friendEmail
            )
        }
    }
}