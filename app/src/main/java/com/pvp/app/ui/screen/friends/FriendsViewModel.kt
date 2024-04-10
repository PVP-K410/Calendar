package com.pvp.app.ui.screen.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.UserService
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {

    var toastCallback: ToastCallback? = null

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()


    interface ToastCallback {
        fun showToast(message: String)
    }

    init {
        viewModelScope.launch {
            userService.user.collect { user ->
                _user.value = user
            }
        }
    }

    private suspend fun updateUser() {
        val currentUser = userService.user.first()
        _user.emit(currentUser)
    }

    fun addFriend(friendEmail: String) {
        viewModelScope.launch {
            val currentUser = userService.user.first()

            currentUser?.let {
                val currentUserEmail = it.email
                val friend = userService.get(friendEmail).first()

                if (friendEmail in it.sentRequests) {
                    toastCallback?.showToast("Friend request already sent to $friendEmail")
                } else if (friend == null) {
                    toastCallback?.showToast("User with email $friendEmail does not exist")
                } else {
                    friend?.let {
                        val updatedFriend = it.copy(receivedRequests = it.receivedRequests + currentUserEmail)
                        userService.merge(updatedFriend)

                        val updatedCurrentUser = currentUser.copy(sentRequests = currentUser.sentRequests + friendEmail)
                        userService.merge(updatedCurrentUser)

                        updateUser()

                        toastCallback?.showToast("Friend request sent!")
                    }
                }
            }
        }
    }

    fun acceptFriendRequest(friendEmail: String) {
        viewModelScope.launch {
            val currentUser = userService.user.first()

            currentUser?.let {
                val currentUserEmail = it.email
                val friend = userService.get(friendEmail).first()

                friend?.let {
                    val updatedFriend = it.copy(
                        sentRequests = it.sentRequests - currentUserEmail,
                        friends = it.friends + currentUserEmail
                    )
                    userService.merge(updatedFriend)

                    val updatedCurrentUser = currentUser.copy(
                        receivedRequests = currentUser.receivedRequests - friendEmail,
                        friends = currentUser.friends + friendEmail
                    )
                    userService.merge(updatedCurrentUser)

                    updateUser()

                    toastCallback?.showToast("Friend request accepted!")
                }
            }
        }
    }

    fun denyFriendRequest(friendEmail: String) {
        viewModelScope.launch {
            val currentUser = userService.user.first()

            currentUser?.let {
                val currentUserEmail = it.email
                val friend = userService.get(friendEmail).first()

                friend?.let {
                    val updatedFriend = it.copy(sentRequests = it.sentRequests - currentUserEmail)
                    userService.merge(updatedFriend)

                    val updatedCurrentUser = currentUser.copy(receivedRequests = currentUser.receivedRequests - friendEmail)
                    userService.merge(updatedCurrentUser)

                    updateUser()

                    toastCallback?.showToast("Friend request denied!")
                }
            }
        }
    }
}