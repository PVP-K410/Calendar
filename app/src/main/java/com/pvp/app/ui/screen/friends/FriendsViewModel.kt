@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pvp.app.ui.screen.friends

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.DecorationService
import com.pvp.app.api.FriendService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.common.FlowUtil.firstOr
import com.pvp.app.common.FlowUtil.flattenFlow
import com.pvp.app.model.FriendObject
import com.pvp.app.model.Friends
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val decorationService: DecorationService,
    private val friendService: FriendService,
    private val userService: UserService,
    private val taskService: TaskService
) : ViewModel() {

    val toastMessage = mutableStateOf<String?>(null)

    private val _stateFriend = MutableStateFlow(FriendState())

    /**
     * State of the selected current user's friend. This is used to load and store
     * [FriendState.entry] details.
     *
     * It is only updated when a friend is selected via [select] method.
     */
    val stateFriend = _stateFriend.asStateFlow()

    private val _stateFriends = MutableStateFlow(FriendsState())

    /**
     * State of the current user's friends
     */
    val stateFriends = _stateFriends.asStateFlow()

    init {
        collectStateChanges()
    }

    /**
     * Listens to changes in the user and friend objects and updates the state accordingly.
     */
    private fun collectStateChanges() {
        val user = userService.user.filterNotNull()

        val friendObject = user
            .filter { it.email.isNotBlank() }
            .flatMapLatest { user ->
                friendService
                    .get(user.email)
                    .filterNotNull()

            }

        val friends = user
            .filter { it.email.isNotBlank() }
            .flatMapLatest { friendService.get(it.email) }
            .filterNotNull()
            .flatMapLatest { friendObject ->
                friendObject.friends
                    .map { friend ->
                        val user = userService
                            .get(friend.email)
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

        viewModelScope.launch {
            combine(
                friendObject,
                friends,
                user
            ) { friendObject, friends, user ->
                FriendsState(
                    friendObject = friendObject,
                    friends = friends,
                    loading = false,
                    user = user
                )
            }
                .collectLatest { state -> _stateFriends.update { state } }
        }
    }

    /**
     * Sends a friend request to a user.
     */
    fun add(friendEmail: String) {
        viewModelScope.launch {
            if (friendEmail.isEmpty()) {
                toastMessage.value = "Please enter an email"

                return@launch
            }

            val email = _stateFriends.first().user.email

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

    /**
     * Accepts a friend request from a user.
     */
    fun accept(friendEmail: String) {
        viewModelScope.launch {
            val email = _stateFriends.first().user.email

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

    /**
     * Denies a friend request from a user.
     */
    fun deny(friendEmail: String) {
        viewModelScope.launch {
            val email = _stateFriends.first().user.email

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

    /**
     * Cancels a friend request sent to a user.
     */
    fun cancel(friendEmail: String) {
        viewModelScope.launch {
            val email = _stateFriends.first().user.email

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

    /**
     * Updates the [stateFriend] state with the selected friend's details.
     */
    fun select(friendEmail: String) {
        _stateFriend.update { it.copy(loading = true) }

        viewModelScope.launch {
            val `object` = friendService
                .get(friendEmail)
                .firstOrNull()

            if (`object` == null) {
                _stateFriend.update { it.copy(friendsMutual = emptyList()) }

                return@launch
            }

            val tasks = taskService
                .get(friendEmail)
                .firstOr(emptyList())
                .count {
                    it.isCompleted &&
                            it.date in
                            LocalDate
                                .now()
                                .minusDays(7)..
                            LocalDate.now()
                }

            val state = _stateFriends.first()

            val emails = `object`.friends
                .map { it.email }
                .intersect(
                    state.friends
                        .map { it.user.email }
                        .toSet()
                )

            val friends = emails.mapNotNull {
                val user = userService
                    .get(it)
                    .firstOrNull()

                user?.let {
                    FriendEntry(
                        avatar = decorationService.getAvatar(user),
                        user = user
                    )
                }
            }

            _stateFriend.value = FriendState(
                details = state.friendObject.friends.first { friend -> friend.email == friendEmail },
                entry = state.friends
                    .find { friend -> friend.user.email == friendEmail }
                    ?: FriendEntry(),
                friendsMutual = friends,
                loading = false,
                tasksCompleted = tasks
            )
        }
    }

    /**
     * Removes a friend from the current user's friend list.
     */
    fun remove(friendEmail: String) {
        viewModelScope.launch {
            val state = _stateFriends.first()

            friendService.removeFriend(
                state.friendObject,
                state.user.email,
                friendEmail
            )

            toastMessage.value = "Friend removed successfully!"
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

data class FriendState(
    val details: Friends = Friends(),
    val entry: FriendEntry = FriendEntry(),
    val friendsMutual: List<FriendEntry> = emptyList(),
    val loading: Boolean = true,
    val tasksCompleted: Int = 0
)

data class FriendsState(
    val friendObject: FriendObject = FriendObject(),
    val friends: List<FriendEntry> = emptyList(),
    val loading: Boolean = true,
    val user: User = User()
)