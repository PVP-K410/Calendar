package com.pvp.app.ui.screen.friends

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.FriendService
import com.pvp.app.api.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendService: FriendService,
    private val userService: UserService
) : ViewModel() {
    val toastMessage = mutableStateOf<String?>(null)

    val user = userService.user.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val friendObject = user.flatMapLatest { user ->
        user?.email?.let { userEmail ->
            friendService.get(userEmail)
        } ?: flowOf(null)
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    init {
        viewModelScope.launch {
            user.collect { user ->
                user?.email?.let { email ->
                    friendService.createFriendObject(email)
                }
            }
        }
    }

    fun createFriendObject(email: String) {
        viewModelScope.launch {
            friendService.createFriendObject(email)
        }
    }

    fun addFriend(friendEmail: String) {
        viewModelScope.launch {
            val userEmail = user.value?.email ?: return@launch
            val friendObject = friendService.get(userEmail).firstOrNull() ?: return@launch
            val friendUser = userService.get(friendEmail).firstOrNull()

            if (friendUser == null) {
                toastMessage.value = "User with email $friendEmail does not exist"
                return@launch
            }

            friendService.createFriendObject(friendEmail)

            toastMessage.value = friendService.addFriend(
                friendObject,
                userEmail,
                friendEmail
            )
        }
    }

    fun acceptFriendRequest(friendEmail: String) {
        viewModelScope.launch {
            val userEmail = user.value?.email ?: return@launch
            val friendObject = friendService.get(userEmail).firstOrNull() ?: return@launch

            toastMessage.value = friendService.acceptFriendRequest(
                friendObject,
                userEmail,
                friendEmail
            )
        }
    }

    fun denyFriendRequest(friendEmail: String) {
        viewModelScope.launch {
            val userEmail = user.value?.email ?: return@launch
            val friendObject = friendService.get(userEmail).firstOrNull() ?: return@launch

            toastMessage.value = friendService.denyFriendRequest(
                friendObject,
                userEmail,
                friendEmail
            )
        }
    }

    fun cancelSentRequest(friendEmail: String) {
        viewModelScope.launch {
            val userEmail = user.value?.email ?: return@launch
            val friendObject = friendService.get(userEmail).firstOrNull() ?: return@launch

            toastMessage.value = friendService.cancelSentRequest(
                friendObject,
                userEmail,
                friendEmail
            )
        }
    }

    fun getFriendAvatar(friendEmail: String): ImageBitmap {
        var avatar: ImageBitmap? = null

        viewModelScope.launch {
            avatar = userService.resolveAvatar(friendEmail)
        }

        return avatar ?: ImageBitmap(0, 0)
    }
}