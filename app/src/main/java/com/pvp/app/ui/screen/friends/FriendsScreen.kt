@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.pvp.app.ui.common.ButtonWithDialog
import com.pvp.app.ui.common.ProgressIndicatorWithinDialog
import com.pvp.app.ui.common.darken
import com.pvp.app.ui.common.lighten
import com.pvp.app.ui.router.Routes

private enum class SortingType {
    EXPERIENCE,
    POINTS
}

@Composable
fun FriendsScreen(
    controller: NavHostController,
    model: FriendsViewModel = hiltViewModel(),
    modifier: Modifier
) {
    val state by model.stateFriends.collectAsStateWithLifecycle()

    HandleState(
        controller,
        { model.resetFriendsScreenState() },
        state.state
    )

    val context = LocalContext.current
    val friendEmail = remember { mutableStateOf("") }
    val friendObject = state.friendObject
    val friends = state.friends
    val message by model.toastMessage
    val scrollState = rememberScrollState()
    val sortingType = remember { mutableStateOf(SortingType.EXPERIENCE) }
    val sortingTypeTemp = remember { mutableStateOf(sortingType.value) }

    val friendsSorted = remember(
        friends,
        sortingType.value
    ) {
        mutableStateOf(
            when (sortingType.value) {
                SortingType.EXPERIENCE -> friends.sortedByDescending { it.user.experience }
                SortingType.POINTS -> friends.sortedByDescending { it.user.points }
            }
        )
    }

    LaunchedEffect(message) {
        message?.let {
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ButtonWithDialog(
                modifier = Modifier.size(
                    45.dp,
                    35.dp
                ),
                content = {
                    Icon(
                        Icons.Outlined.GroupAdd,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(25.dp)
                    )
                },
                contentPadding = PaddingValues(2.dp),
                dialogTitle = { Text("Add friend") },
                dialogContent = {
                    OutlinedTextField(
                        value = friendEmail.value,
                        onValueChange = { friendEmail.value = it },
                        label = { Text("Friend's email") },
                    )
                },
                confirmButtonContent = {
                    Text(
                        "Add",
                        fontWeight = FontWeight.Bold
                    )
                },
                onConfirm = {
                    model.add(friendEmail.value.trim())

                    friendEmail.value = ""
                },
                onDismiss = { friendEmail.value = "" },
                shape = MaterialTheme.shapes.medium
            )

            BadgedBox(
                badge = {
                    if (friendObject.receivedRequests.isNotEmpty()) {
                        Badge(containerColor = MaterialTheme.colorScheme.errorContainer) {
                            Text(
                                style = MaterialTheme.typography.bodySmall,
                                text = friendObject.receivedRequests.size.toString()
                            )
                        }
                    }
                }
            ) {
                ButtonWithDialog(
                    modifier = Modifier.size(
                        160.dp,
                        35.dp
                    ),
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Requests",
                                color = MaterialTheme.colorScheme.surface,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    contentAlignment = Alignment.Center,
                    contentPadding = PaddingValues(
                        horizontal = 10.dp,
                        vertical = 2.dp
                    ),
                    dialogTitle = { Text("Friend requests") },
                    dialogContent = {
                        val selectedTab = remember { mutableIntStateOf(0) }

                        Column {
                            PrimaryTabRow(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                divider = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.medium),
                                selectedTabIndex = selectedTab.intValue
                            ) {
                                Tab(
                                    selected = selectedTab.intValue == 0,
                                    onClick = { selectedTab.intValue = 0 }
                                ) {
                                    Text(
                                        "Received",
                                        color = MaterialTheme.colorScheme.inverseSurface,
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
                                        color = MaterialTheme.colorScheme.inverseSurface,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = if (selectedTab.intValue == 1) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            when (selectedTab.intValue) {
                                0 -> {
                                    RequestList(
                                        requests = friendObject.receivedRequests,
                                        requestTitle = "received",
                                        acceptAction = { request -> model.accept(request) },
                                        denyAction = { request -> model.deny(request) }
                                    )
                                }

                                1 -> {
                                    RequestList(
                                        requests = friendObject.sentRequests,
                                        requestTitle = "sent",
                                        acceptAction = { },
                                        denyAction = { request -> model.cancel(request) }
                                    )
                                }
                            }
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    showConfirmButton = false
                )
            }

            ButtonWithDialog(
                modifier = Modifier.size(
                    45.dp,
                    35.dp
                ),
                content = {
                    Icon(
                        Icons.Outlined.FilterAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(25.dp)
                    )
                },
                contentPadding = PaddingValues(2.dp),
                dialogTitle = { Text("Sort by") },
                dialogContent = {
                    Column {
                        SortingType
                            .entries
                            .forEach { type ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable(
                                        onClick = {
                                            sortingTypeTemp.value = type
                                        }
                                    )
                                ) {
                                    RadioButton(
                                        selected = sortingTypeTemp.value == type,
                                        onClick = {
                                            sortingTypeTemp.value = type
                                        }
                                    )

                                    Text(
                                        text = type
                                            .name
                                            .toLowerCase()
                                            .capitalize(),
                                        modifier = Modifier.padding(start = 8.dp),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                    }
                },
                onConfirm = { sortingType.value = sortingTypeTemp.value },
                onDismiss = { sortingTypeTemp.value = sortingType.value },
                shape = MaterialTheme.shapes.medium
            )
        }

        Text(
            "All friends - ${friends.size}",
            modifier = Modifier.padding(vertical = 8.dp),
            style = MaterialTheme.typography.titleMedium
        )

        FriendList(
            friends = friendsSorted.value,
            onSelect = { friend -> model.select(friend.user.email) },
            scrollState = scrollState
        )
    }
}

@Composable
private fun FriendList(
    friends: List<FriendEntry>,
    onSelect: (FriendEntry) -> Unit,
    scrollState: ScrollState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 10.dp)
    ) {
        for ((index, friend) in friends.withIndex()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(vertical = 2.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .clickable { onSelect(friend) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                ListItemContent(
                    index,
                    friend
                )
            }
        }
    }
}

@Composable
private fun ListItemContent(
    index: Int,
    friend: FriendEntry
) {
    when {
        index == 0 -> {
            Icon(
                imageVector = Icons.Outlined.EmojiEvents,
                contentDescription = "Top 1",
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(30.dp)
            )
        }

        index <= 2 -> {
            Icon(
                imageVector = Icons.Outlined.WorkspacePremium,
                contentDescription = "Top 2-3",
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(30.dp)
            )
        }

        else -> {
            Spacer(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(30.dp)
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = friend.user.username,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 4.dp)
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest.lighten(0.08f))
        ) {
            Image(
                bitmap = friend.avatar,
                contentDescription = "Friend avatar",
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
            )
        }
    }

    Spacer(modifier = Modifier.width(6.dp))
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
                        .height(40.dp)
                        .padding(vertical = 2.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceContainer.darken(0.25f),
                            MaterialTheme.shapes.medium
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = request,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .weight(1f)
                    )

                    if (requestTitle == "received") {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Accept request",
                            tint = Color.Green.lighten(0.45f),
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
                        tint = Color.Red.lighten(0.45f),
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

@Composable
fun HandleState(
    controller: NavHostController,
    resetState: () -> Unit,
    state: FriendsScreenState
) {
    when (state) {
        is FriendsScreenState.Finished.SelectedFriend -> {
            controller.navigate(Routes.Friend.path) {
                launchSingleTop = true
            }

            resetState()
        }

        is FriendsScreenState.Loading -> ProgressIndicatorWithinDialog()

        else -> {}
    }
}