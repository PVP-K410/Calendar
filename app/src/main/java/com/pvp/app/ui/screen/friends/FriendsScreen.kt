package com.pvp.app.ui.screen.friends

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.model.User

enum class SortingType {
    EXPERIENCE,
    POINTS
}

@Composable
fun FriendsScreen(
    model: FriendsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val toastMessage by model.toastMessage
    val friendObject by model.friendObject.collectAsStateWithLifecycle()
    val friends = friendObject?.friends ?: emptyList()
    val isRequestSent by model.isRequestSent.collectAsState(false)
    val receivedRequests = friendObject?.receivedRequests ?: emptyList()
    val sentRequests = friendObject?.sentRequests ?: emptyList()
    val friendEmail = remember { mutableStateOf("") }
    val showAddFriend = remember { mutableStateOf(false) }
    val showRequests = remember { mutableStateOf(false) }
    val showSorting = remember { mutableStateOf(false) }
    val sortingType = remember { mutableStateOf(SortingType.EXPERIENCE) }
    val sortedFriends = remember { mutableStateOf(emptyList<String>()) }
    val friendsData = remember { mutableStateOf<List<User>>(emptyList()) }
    val selectedTab = remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast
                .makeText(
                    context,
                    it,
                    Toast.LENGTH_SHORT
                )
                .show()

            model.toastMessage.value = null
        }
    }

    LaunchedEffect(isRequestSent) {
        if (isRequestSent) {
            showAddFriend.value = false
            friendEmail.value = ""
        }
    }

    LaunchedEffect(
        friends,
        sortingType.value
    ) {
        sortedFriends.value = sortFriends(
            friends,
            sortingType.value,
            model
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            )
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        TopRowWithButtons(
            showAddFriend,
            showRequests,
            showSorting
        )

        Text(
            "All friends - ${friends.size}",
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            style = MaterialTheme.typography.titleMedium
        )

        FriendList(
            friends = sortedFriends.value,
            model = model,
            scrollState = scrollState
        )
    }

    if (showAddFriend.value) {
        AlertDialog(
            onDismissRequest = {
                showAddFriend.value = false
                friendEmail.value = ""
            },
            confirmButton = {
                Button(onClick = {
                    model.addFriend(friendEmail.value)
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showAddFriend.value = false
                    friendEmail.value = ""
                }) {
                    Text("Cancel")
                }
            },
            text = {
                OutlinedTextField(
                    value = friendEmail.value,
                    onValueChange = { friendEmail.value = it },
                    label = { Text("Friend's email") },
                )
            },
        )
    }

    if (showRequests.value) {
        AlertDialog(
            onDismissRequest = {
                showRequests.value = false
                selectedTab.intValue = 0
            },
            confirmButton = {
                Button(onClick = {
                    showRequests.value = false
                    selectedTab.intValue = 0
                }) {
                    Text("Back")
                }
            },
            text = {
                Column {
                    TabRow(selectedTabIndex = selectedTab.intValue) {
                        Tab(
                            selected = selectedTab.intValue == 0,
                            onClick = { selectedTab.intValue = 0 }
                        ) {
                            Text(
                                "Received",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (selectedTab.intValue == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        Tab(
                            selected = selectedTab.intValue == 1,
                            onClick = { selectedTab.intValue = 1 }
                        ) {
                            Text(
                                "Sent",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (selectedTab.intValue == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    when (selectedTab.intValue) {
                        0 -> {
                            RequestList(
                                requests = receivedRequests,
                                requestTitle = "received",
                                acceptAction = { request -> model.acceptFriendRequest(request) },
                                denyAction = { request -> model.denyFriendRequest(request) }
                            )
                        }

                        1 -> {
                            RequestList(
                                requests = sentRequests,
                                requestTitle = "sent",
                                acceptAction = { },
                                denyAction = { request -> model.cancelSentRequest(request) }
                            )
                        }
                    }
                }
            },
        )
    }

    if (showSorting.value) {
        AlertDialog(
            onDismissRequest = { showSorting.value = false },
            title = { Text("Sort by") },
            text = {
                Column {
                    SortingType
                        .entries
                        .forEach { type ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(
                                onClick = {
                                    sortingType.value = type
                                    showSorting.value = false
                                }
                            )
                        ) {
                            RadioButton(
                                selected = sortingType.value == type,
                                onClick = {
                                    sortingType.value = type
                                    showSorting.value = false
                                }
                            )

                            Text(
                                text = type
                                    .name
                                    .toLowerCase()
                                    .capitalize(),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = { }
        )
    }
}


private suspend fun sortFriends(
    friends: List<String>,
    sortingType: SortingType,
    model: FriendsViewModel
): List<String> {
    val users = friends.map { email -> model.getFriendData(email) }

    val sortedUsers = when (sortingType) {
        SortingType.EXPERIENCE -> users.sortedByDescending { it!!.experience }
        SortingType.POINTS -> users.sortedBy { it!!.points }
    }

    return sortedUsers.map { it!!.email }
}

@Composable
private fun TopRowWithButtons(
    showAddFriend: MutableState<Boolean>,
    showRequests: MutableState<Boolean>,
    showSorting: MutableState<Boolean>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 4.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RowButton(
            onClick = { showAddFriend.value = true },
            modifier = Modifier.size(
                width = 45.dp,
                height = 35.dp
            ),
            content = ButtonContent.Icon(Icons.Outlined.GroupAdd)
        )

        RowButton(
            onClick = { showRequests.value = true },
            modifier = Modifier.size(
                width = 160.dp,
                height = 35.dp
            ),
            content = ButtonContent.Text("Requests")
        )

        RowButton(
            onClick = { showSorting.value = true },
            modifier = Modifier.size(
                width = 45.dp,
                height = 35.dp
            ),
            content = ButtonContent.Icon(Icons.Outlined.FilterAlt)
        )
    }
}

private sealed class ButtonContent {
    data class Icon(val imageVector: ImageVector) : ButtonContent()
    data class Text(val text: String) : ButtonContent()
}

@Composable
private fun RowButton(
    onClick: () -> Unit,
    modifier: Modifier,
    content: ButtonContent
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(4.dp)
    ) {
        when (content) {
            is ButtonContent.Icon -> Icon(
                imageVector = content.imageVector,
                contentDescription = null,
                modifier = Modifier.size(25.dp)
            )

            is ButtonContent.Text -> Text(
                text = content.text,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun FriendList(
    friends: List<String>,
    model: FriendsViewModel,
    scrollState: ScrollState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 10.dp)
    ) {
        for (friend in friends) {
            val avatar = model.getFriendAvatar(friend)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(
                        horizontal = 16.dp,
                        vertical = 2.dp
                    )
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.shapes.small
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = friend,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(start = 25.dp)
                        .weight(1f)
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Image(
                        bitmap = avatar,
                        contentDescription = "Friend avatar",
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))
            }
        }
    }
}

@Composable
private fun RequestList(
    requests: List<String>,
    requestTitle: String,
    acceptAction: (String) -> Unit,
    denyAction: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        if (requests.isEmpty()) {
            Text(
                "No $requestTitle requests",
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            for (request in requests) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .padding(vertical = 2.dp)
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.shapes.small
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = request,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(start = 2.dp)
                            .weight(1f)
                    )

                    if (requestTitle == "received") {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Accept request",
                            tint = Color.Green,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .clickable { acceptAction(request) }
                        )

                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Icon(
                        imageVector = Icons.Outlined.DoNotDisturbOn,
                        contentDescription = "Deny request",
                        tint = Color.Red,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .clickable { denyAction(request) }
                    )
                }
            }
        }
    }
}
