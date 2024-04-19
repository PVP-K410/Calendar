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
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material.icons.outlined.DoNotStep
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.pvp.app.model.FriendObject
import com.pvp.app.model.User
import com.pvp.app.ui.common.ButtonWithDialog
import com.pvp.app.ui.common.Experience
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private enum class SortingType {
    EXPERIENCE,
    POINTS
}

@Composable
fun FriendsScreen(
    model: FriendsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val toastMessage by model.toastMessage
    val friendObject by model.userFriendObject.collectAsStateWithLifecycle()
    val friendEmail = remember { mutableStateOf("") }
    val sortingType = remember { mutableStateOf(SortingType.EXPERIENCE) }
    val sortedFriends = remember { mutableStateOf(emptyList<User>()) }
    val friendsData by model.userFriends.collectAsState()
    val scrollState = rememberScrollState()
    val tempSortingType = remember { mutableStateOf(sortingType.value) }

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

    LaunchedEffect(
        sortingType.value,
        friendsData
    ) {
        sortedFriends.value = sortFriends(
            friendsData,
            sortingType.value
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
            ButtonWithDialog(
                modifier = Modifier.size(
                    45.dp,
                    35.dp
                ),
                content = {
                    Icon(
                        Icons.Outlined.GroupAdd,
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                },
                contentPadding = PaddingValues(2.dp),
                dialogTitle = { Text("Add Friend") },
                dialogContent = {
                    OutlinedTextField(
                        value = friendEmail.value,
                        onValueChange = { friendEmail.value = it },
                        label = { Text("Friend's email") },
                    )
                },
                confirmButtonContent = { Text("Add") },
                onConfirm = {
                    model.addFriend(friendEmail.value)
                    friendEmail.value = ""
                },
                onDismiss = { friendEmail.value = "" },
                shape = MaterialTheme.shapes.small
            )

            ButtonWithDialog(
                modifier = Modifier.size(
                    160.dp,
                    35.dp
                ),
                content = {
                    Text(
                        "Requests",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                contentAlignment = Alignment.Center,
                contentPadding = PaddingValues(
                    horizontal = 45.dp,
                    vertical = 2.dp
                ),
                dialogTitle = { Text("Friend Requests") },
                dialogContent = {
                    val selectedTab = remember { mutableIntStateOf(0) }

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
                                    requests = friendObject.receivedRequests,
                                    requestTitle = "received",
                                    acceptAction = { request -> model.acceptFriendRequest(request) },
                                    denyAction = { request -> model.denyFriendRequest(request) }
                                )
                            }

                            1 -> {
                                RequestList(
                                    requests = friendObject.sentRequests,
                                    requestTitle = "sent",
                                    acceptAction = { },
                                    denyAction = { request -> model.cancelSentRequest(request) }
                                )
                            }
                        }
                    }
                },
                shape = MaterialTheme.shapes.small,
                showConfirmButton = false
            )

            ButtonWithDialog(
                modifier = Modifier.size(
                    45.dp,
                    35.dp
                ),
                content = {
                    Icon(
                        Icons.Outlined.FilterAlt,
                        contentDescription = null,
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
                                            tempSortingType.value = type
                                        }
                                    )
                                ) {
                                    RadioButton(
                                        selected = tempSortingType.value == type,
                                        onClick = {
                                            tempSortingType.value = type
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
                onConfirm = { sortingType.value = tempSortingType.value },
                onDismiss = { tempSortingType.value = sortingType.value },
                shape = MaterialTheme.shapes.small
            )
        }

        Text(
            "All friends - ${friendsData.size}",
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            style = MaterialTheme.typography.titleMedium
        )

        FriendList(
            friends = sortedFriends.value,
            friendObject = friendObject,
            model = model,
            scrollState = scrollState
        )
    }
}

private fun sortFriends(
    friends: List<User>,
    sortingType: SortingType
): List<User> {
    return when (sortingType) {
        SortingType.EXPERIENCE -> friends.sortedByDescending { it.experience }
        SortingType.POINTS -> friends.sortedByDescending { it.points }
    }
}

@Composable
private fun FriendList(
    friends: List<User>,
    friendObject: FriendObject,
    model: FriendsViewModel,
    scrollState: ScrollState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 10.dp)
    ) {
        for ((index, friend) in friends.withIndex()) {
            val avatar = model.getFriendAvatar(friend.email)

            ButtonWithDialog(
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
                content = {
                    when {
                        index == 0 -> {
                            Icon(
                                imageVector = Icons.Outlined.EmojiEvents,
                                contentDescription = "Top 1",
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(24.dp)
                            )
                        }

                        index <= 2 -> {
                            Icon(
                                imageVector = Icons.Outlined.WorkspacePremium,
                                contentDescription = "Top 2-3",
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(24.dp)
                            )
                        }

                        else -> {
                            Spacer(
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(24.dp)
                            )
                        }
                    }

                    Text(
                        text = friend.username,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(start = 4.dp)
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
                },
                contentPadding = PaddingValues(2.dp),
                dialogTitle = { Text("${friend.username} information") },
                dialogContent = {
                    val tasksCompleted by model.tasksCompleted.collectAsState()
                    val mutualFriends by model.mutualFriends.collectAsState()

                    val friendInfo = friendObject.friends.find { it.email == friend.email }
                    val sinceDateTime = LocalDateTime.ofInstant(
                        friendInfo?.let { Instant.ofEpochMilli(it.since) },
                        ZoneId.systemDefault()
                    )
                    val formattedDate = sinceDateTime.format(
                        DateTimeFormatter.ofPattern(
                            "yyyy/MM/dd"
                        )
                    )

                    model.tasksCompleted(friend.email)
                    model.getMutualFriends(friend.email)

                    Column(
                        modifier = Modifier
                            .padding(2.dp)
                            .fillMaxWidth()
                            .height(300.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Image(
                                bitmap = avatar,
                                contentDescription = "Friend avatar",
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Experience(
                            experience = friend.experience,
                            experienceRequired = (friend.level + 1) * (friend.level + 1) * 13,
                            level = friend.level,
                            paddingStart = 0.dp,
                            paddingEnd = 0.dp,
                            fontSize = 14,
                            fontWeight = FontWeight.Normal,
                            height = 26.dp,
                            textStyle = MaterialTheme.typography.titleSmall,
                            progressTextStyle = MaterialTheme.typography.bodySmall
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.surface,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "7 days activity",
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .height(90.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(vertical = 4.dp),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.DoNotStep,
                                        contentDescription = "steps",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "63819 steps made",
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }

                                HorizontalDivider(
                                    color = Color.Gray,
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(horizontal = 14.dp)
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.LocalFireDepartment,
                                        contentDescription = "calories",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "6571 calories burned",
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }

                                HorizontalDivider(
                                    color = Color.Gray,
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(horizontal = 14.dp)
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.FactCheck,
                                        contentDescription = "tasks",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "$tasksCompleted tasks completed",
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.surface,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Friend information",
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(MaterialTheme.shapes.small)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(vertical = 4.dp, horizontal = 14.dp)
                            ) {
                                Text(
                                    text = "Friends since - $formattedDate",
                                    style = MaterialTheme.typography.titleSmall
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                if (mutualFriends.isEmpty()) {
                                    Text(
                                        text = "No mutual friends",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                } else {
                                    Text(
                                        text = "Mutual friends",
                                        style = MaterialTheme.typography.titleSmall
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    for (mutualFriend in mutualFriends) {
                                        val mutualAvatar = model.getFriendAvatar(mutualFriend.email)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = mutualFriend.username,
                                                style = MaterialTheme.typography.titleSmall,
                                            )

                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                            ) {
                                                Image(
                                                    bitmap = mutualAvatar,
                                                    contentDescription = "Friend avatar",
                                                    modifier = Modifier
                                                        .size(18.dp)
                                                        .clip(CircleShape)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButtonContent = { Text("Remove") },
                onConfirm = { model.removeFriend(friend.email) },
                shape = MaterialTheme.shapes.small
            )
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
