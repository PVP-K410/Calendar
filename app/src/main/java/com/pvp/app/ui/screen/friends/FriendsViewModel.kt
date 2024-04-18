@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pvp.app.ui.screen.friends

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.FriendService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.common.FlowUtil.flattenFlow
import com.pvp.app.model.FriendObject
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendService: FriendService,
    private val userService: UserService,
    private val taskService: TaskService
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
                    userService
                        .get(it)
                        .filterNotNull()
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

    fun getFriendAvatar(friendEmail: String): ImageBitmap {
        var avatar: ImageBitmap? = null

        viewModelScope.launch {
            avatar = userService.resolveAvatar(friendEmail)
        }

        return avatar!!
    }

    val mutualFriends = MutableStateFlow<List<User>>(emptyList())

    fun getMutualFriends(friendEmail: String) {
        viewModelScope.launch {
            val friendObject = friendService
                .get(friendEmail)
                .firstOrNull()

            val mutualFriendsList =
                friendObject?.friends?.intersect(userFriends.value.map { it.email }.toSet())

            if (mutualFriendsList != null) {
                val mutualFriendsUsers = mutualFriendsList.mapNotNull {
                    val user = userService.get(it).firstOrNull()
                    user
                }
                mutualFriends.value = mutualFriendsUsers
            } else {
                mutualFriends.value = emptyList()
            }
        }
    }

    val tasksCompleted = MutableStateFlow(0)

    fun tasksCompleted(friendEmail: String) {
        viewModelScope.launch {
            val tasks = taskService.get(friendEmail).firstOrNull() ?: emptyList()
            tasksCompleted.value = tasks.count {
                it.isCompleted && it.date in LocalDate.now().minusDays(7)..LocalDate.now()
            }
        }
    }


}
