@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pvp.app.ui.screen.friends

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.DecorationService
import com.pvp.app.api.FriendService
import com.pvp.app.api.UserService
import com.pvp.app.common.FlowUtil.flattenFlow
import com.pvp.app.model.FriendObject
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val decorationService: DecorationService,
    private val friendService: FriendService,
    private val userService: UserService
) : ViewModel() {

    val toastMessage = mutableStateOf<String?>(null)

    val user = userService.user
        .filterNotNull()
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = User()
        )

    val userFriendObject = user
        .filter { it.email.isNotBlank() }
        .flatMapLatest { user ->
            friendService
                .get(user.email)
                .filterNotNull()

        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FriendObject()
        )

    val userFriends = user
        .filter { it.email.isNotBlank() }
        .flatMapLatest { friendService.get(it.email) }
        .filterNotNull()
        .flatMapLatest { friendObject ->
            friendObject.friends
                .map {
                    val user = userService
                        .get(it)
                        .filterNotNull()

                    flowOf(
                        Pair(
                            user,
                            decorationService.getAvatar(user)
                        )
                    )
                }
                .flattenFlow()
        }
        .flatMapLatest { pairs ->
            pairs
                .map { (user, avatar) ->
                    flowOf(
                        FriendEntry(
                            avatar = avatar.first(),
                            user = user.first()
                        )
                    )
                }
                .flattenFlow()
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun addFriend(friendEmail: String) {
        viewModelScope.launch {

            if (friendEmail.isEmpty()) {
                toastMessage.value = "Please enter an email"

                return@launch
            }

            val email = user.value.email

            val friendObject = friendService
                .get(email)
                .firstOrNull()
                ?: return@launch

            val friendUser = userService
                .get(friendEmail)
                .firstOrNull()

            if (friendUser == null) {
                toastMessage.value = "User with email $friendEmail does not exist"

                return@launch
            }

            val toastMessageValue = friendService.addFriend(
                friendObject,
                email,
                friendEmail
            )

            toastMessage.value = toastMessageValue
        }
    }

    fun acceptFriendRequest(friendEmail: String) {
        viewModelScope.launch {
            val email = user.value.email

            val friendObject = friendService
                .get(email)
                .firstOrNull()
                ?: return@launch

            toastMessage.value = friendService.acceptFriendRequest(
                friendObject,
                email,
                friendEmail
            )
        }
    }

    fun denyFriendRequest(friendEmail: String) {
        viewModelScope.launch {
            val email = user.value.email

            val friendObject = friendService
                .get(email)
                .firstOrNull()
                ?: return@launch

            toastMessage.value = friendService.denyFriendRequest(
                friendObject,
                email,
                friendEmail
            )
        }
    }

    fun cancelSentRequest(friendEmail: String) {
        viewModelScope.launch {
            val email = user.value.email

            val friendObject = friendService
                .get(email)
                .firstOrNull()
                ?: return@launch

            toastMessage.value = friendService.cancelSentRequest(
                friendObject,
                email,
                friendEmail
            )
        }
    }
}

data class FriendEntry(
    val avatar: ImageBitmap = ImageBitmap(
        1,
        1
    ),
    val user: User = User()
)