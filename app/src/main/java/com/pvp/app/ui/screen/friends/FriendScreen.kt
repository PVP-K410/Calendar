package com.pvp.app.ui.screen.friends

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.material.icons.outlined.DoNotStep
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.pvp.app.model.Friends
import com.pvp.app.ui.common.ButtonConfirm
import com.pvp.app.ui.common.Experience
import com.pvp.app.ui.common.ProgressIndicator
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun FriendScreen(
    controller: NavHostController,
    model: FriendsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    resolveOptions: () -> Unit
) {
    val state by model.stateFriend.collectAsStateWithLifecycle()

    if (state.loading) {
        ProgressIndicator()

        return
    }

    LaunchedEffect(state.entry.user.username) {
        resolveOptions()
    }

    val details = state.details
    val entry = state.entry
    val friends = state.friendsMutual
    val tasks = state.tasksCompleted
    val stateScroll = rememberScrollState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(stateScroll)
            .then(modifier)
            .padding(8.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp)
    ) {
        Header(entry = entry)

        Spacer(modifier = Modifier.size(8.dp))

        Content(
            details = details,
            friends = friends,
            tasks = tasks
        )

        Spacer(modifier = Modifier.size(8.dp))

        Remove {
            model.remove(entry.user.email)

            controller.popBackStack()
        }
    }
}

@Composable
private fun Header(entry: FriendEntry) {
    AvatarBox(entry)

    Text(
        style = MaterialTheme.typography.titleSmall,
        text = entry.user.username
    )

    Spacer(modifier = Modifier.height(6.dp))

    Experience(
        experience = entry.user.experience,
        experienceRequired = (entry.user.level + 1) * (entry.user.level + 1) * 13,
        level = entry.user.level,
        paddingStart = 0.dp,
        paddingEnd = 0.dp,
        fontSize = 14,
        fontWeight = FontWeight.Normal,
        height = 26.dp,
        textStyle = MaterialTheme.typography.titleSmall,
        progressTextStyle = MaterialTheme.typography.bodySmall
    )

    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun Remove(onRemove: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ButtonConfirm(
            confirmationDescription = { Text("If friend is removed, you will have to request for friendship again") },
            confirmationTitle = { Text("Are you sure you want to remove this friend?") },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
            content = { Text("Remove Friend") },
            modifier = Modifier
                .height(38.dp)
                .border(
                    1.dp,
                    Color.Red,
                    MaterialTheme.shapes.extraLarge
                ),
            onConfirm = onRemove,
            shape = MaterialTheme.shapes.extraLarge
        )
    }
}

@Composable
fun Content(
    details: Friends,
    friends: List<FriendEntry>,
    tasks: Int
) {
    val sinceDateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(details.since),
        ZoneId.systemDefault()
    )

    val formattedDate = sinceDateTime.format(
        DateTimeFormatter.ofPattern(
            "yyyy/MM/dd"
        )
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ActivityInfo(tasks)

        FriendInfo(
            formattedDate,
            friends
        )
    }
}

@Composable
private fun AvatarBox(friend: FriendEntry) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Image(
            bitmap = friend.avatar,
            contentDescription = "Friend avatar",
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
private fun ActivityInfo(tasksCompleted: Int) {
    Column(modifier = Modifier.fillMaxSize()) {
        InfoHeader(text = "7 days activity")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .height(90.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            ActivityRow(
                icon = Icons.Outlined.DoNotStep,
                contentDescription = "steps",
                text = "63819 steps made"
            )

            HorizontalDivider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 14.dp)
            )

            ActivityRow(
                icon = Icons.Outlined.LocalFireDepartment,
                contentDescription = "calories",
                text = "6571 calories burned"
            )

            HorizontalDivider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 14.dp)
            )

            ActivityRow(
                icon = Icons.AutoMirrored.Outlined.FactCheck,
                contentDescription = "tasks",
                text = "$tasksCompleted sport tasks completed"
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
    }
}

@Composable
private fun ActivityRow(
    contentDescription: String,
    icon: ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            contentDescription = contentDescription,
            imageVector = icon,
            modifier = Modifier.size(20.dp)
        )

        Text(
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.titleSmall,
            text = text
        )
    }
}

@Composable
private fun FriendInfo(
    formattedDate: String,
    mutualFriends: List<FriendEntry>
) {
    InfoHeader(text = "Other information")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
            .padding(
                horizontal = 14.dp,
                vertical = 4.dp
            )
    ) {
        Text(
            style = MaterialTheme.typography.titleSmall,
            text = "Friends since - $formattedDate"
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (mutualFriends.isEmpty()) {
            Text(
                style = MaterialTheme.typography.titleSmall,
                text = "No mutual friends"
            )
        } else {
            Text(
                style = MaterialTheme.typography.titleSmall,
                text = "Mutual friends"
            )

            Spacer(modifier = Modifier.height(4.dp))

            for (mutualFriend in mutualFriends) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        style = MaterialTheme.typography.titleSmall,
                        text = mutualFriend.user.username
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Image(
                            bitmap = mutualFriend.avatar,
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

@Composable
private fun InfoHeader(text: String) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.small
            )
            .padding(vertical = 4.dp)
    ) {
        Text(
            style = MaterialTheme.typography.titleSmall,
            text = text
        )
    }

    Spacer(modifier = Modifier.height(3.dp))
}