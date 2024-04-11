package com.pvp.app.ui.screen.friends

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val userService: UserService
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
            val email = user.email

            if (email == friendEmail) {
                toastMessage.value = "You are always your very best friend!"

                return@launch
            }

            val friend = userService
                .get(friendEmail)
                .first()

            if (friend == null) {
                toastMessage.value = "User with email $friendEmail does not exist"

                return@launch
            }

            if (friendEmail in user.friends) {
                toastMessage.value = "$friendEmail is already your friend"

                return@launch
            }

            if (friendEmail in user.sentRequests) {
                toastMessage.value = "Friend request already sent to $friendEmail"

                return@launch
            }

            val friendNew = friend.copy(receivedRequests = friend.receivedRequests + email)

            userService.merge(friendNew)

            val userNew = user.copy(sentRequests = user.sentRequests + friendEmail)

            userService.merge(userNew)

            toastMessage.value = "Friend request sent!"
        }
    }

    fun acceptFriendRequest(friendEmail: String) {
        viewModelScope.launch {
            val user = user.value ?: return@launch
            val email = user.email

            val friend = userService
                .get(friendEmail)
                .first()
                ?: return@launch

            val friendNew = friend.copy(
                sentRequests = friend.sentRequests - email,
                friends = friend.friends + email
            )

            userService.merge(friendNew)

            val userNew = user.copy(
                receivedRequests = user.receivedRequests - friendEmail,
                friends = user.friends + friendEmail
            )

            userService.merge(userNew)

            toastMessage.value = "Friend request accepted!"
        }
    }

    fun denyFriendRequest(friendEmail: String) {
        viewModelScope.launch {
            val user = user.value ?: return@launch
            val email = user.email

            val friend = userService
                .get(friendEmail)
                .first()
                ?: return@launch

            val friendNew = friend.copy(sentRequests = friend.sentRequests - email)

            userService.merge(friendNew)

            val userNew = user.copy(receivedRequests = user.receivedRequests - friendEmail)

            userService.merge(userNew)

            toastMessage.value = "Friend request denied!"
        }
    }
}