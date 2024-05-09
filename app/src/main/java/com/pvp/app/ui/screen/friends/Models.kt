package com.pvp.app.ui.screen.friends

import androidx.compose.ui.graphics.ImageBitmap
import com.pvp.app.model.FriendObject
import com.pvp.app.model.Friends
import com.pvp.app.model.User

data class FriendEntry(
    val avatar: ImageBitmap = ImageBitmap(
        1,
        1
    ),
    val user: User = User()
)

data class FriendState(
    val calories: Double = 0.0,
    val details: Friends = Friends(),
    val entry: FriendEntry = FriendEntry(),
    val friendsMutual: List<FriendEntry> = emptyList(),
    val state: FriendScreenState = FriendScreenState.Loading,
    val steps: Long = 0L,
    val tasksCompleted: Int = 0
)

sealed class FriendScreenState {

    data object Finished : FriendScreenState()

    data object Loading : FriendScreenState()

    data object NoOperation : FriendScreenState()
}

data class FriendsState(
    val friendObject: FriendObject = FriendObject(),
    val friends: List<FriendEntry> = emptyList(),
    val state: FriendsScreenState = FriendsScreenState.Loading,
    val user: User = User()
)

sealed class FriendsScreenState {

    data object Loading : FriendsScreenState()

    data object NoOperation : FriendsScreenState()

    sealed class Finished : FriendsScreenState() {

        data object SelectedFriend : Finished()

        companion object : Finished()
    }
}