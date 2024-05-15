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
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.pvp.app.R
import com.pvp.app.model.Friends
import com.pvp.app.ui.common.ButtonConfirm
import com.pvp.app.ui.common.Experience
import com.pvp.app.ui.common.LocalHorizontalPagerSettled
import com.pvp.app.ui.common.LocalRouteOptionsApplier
import com.pvp.app.ui.common.ProgressIndicatorWithinDialog
import com.pvp.app.ui.common.RouteTitle
import com.pvp.app.ui.common.darken
import com.pvp.app.ui.common.lighten
import com.pvp.app.ui.router.Route
import com.pvp.app.ui.router.Routes
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
private fun ActivityInfo(tasksCompleted: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                MaterialTheme.shapes.medium
            )
    ) {
        InfoHeader(text = "7 days activity")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .height(90.dp)
                .clip(MaterialTheme.shapes.small)
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            ActivityRow(
                icon = ImageVector.vectorResource(R.drawable.steps_icon),
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
private fun AvatarBox(friend: FriendEntry) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(150.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        Image(
            bitmap = friend.avatar,
            contentDescription = "Friend avatar",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
private fun Content(
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

        Spacer(modifier = Modifier.height(12.dp))

        FriendInfo(friends)

        Spacer(modifier = Modifier.height(12.dp))

        OtherInfo(formattedDate)
    }
}

@Composable
private fun FriendInfo(mutualFriends: List<FriendEntry>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                MaterialTheme.shapes.medium
            )
    ) {
        InfoHeader(text = "Mutual friends")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 14.dp,
                    vertical = 4.dp
                )
        ) {
            if (mutualFriends.isEmpty()) {
                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = "No mutual friends"
                )
            } else {
                for (mutualFriend in mutualFriends) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
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
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(
                                    MaterialTheme.colorScheme.surfaceContainerHighest.lighten(
                                        0.08f
                                    )
                                )
                        ) {
                            Image(
                                bitmap = mutualFriend.avatar,
                                contentDescription = "Friend avatar",
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun FriendScreen(
    controller: NavHostController,
    model: FriendsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by model.stateFriend.collectAsStateWithLifecycle()

    RouteOptionsApplier(
        controller,
        state.entry.user.username
    )

    HandleState(state.state)

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
            .padding(16.dp)
            .clip(MaterialTheme.shapes.small)
    ) {
        Header(entry = entry)

        Spacer(modifier = Modifier.size(8.dp))

        Content(
            details = details,
            friends = friends,
            tasks = tasks
        )

        Spacer(modifier = Modifier.size(12.dp))

        Remove {
            model.remove(entry.user.email)

            controller.popBackStack()
        }
    }
}

@Composable
private fun HandleState(state: FriendScreenState) {
    when (state) {
        FriendScreenState.Loading -> ProgressIndicatorWithinDialog()

        else -> {}
    }
}

@Composable
private fun Header(entry: FriendEntry) {
    AvatarBox(entry)

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleLarge,
        text = entry.user.username
    )

    Text(
        style = MaterialTheme.typography.titleSmall,
        text = entry.user.email
    )

    Spacer(modifier = Modifier.height(10.dp))

    Experience(
        experience = entry.user.experience,
        experienceRequired = (entry.user.level + 1) * (entry.user.level + 1) * 13,
        level = entry.user.level,
        paddingStart = 0.dp,
        paddingEnd = 0.dp,
        fontSize = 17,
        fontWeight = FontWeight.Bold,
        height = 30.dp,
        textStyle = MaterialTheme.typography.titleSmall,
        progressTextStyle = MaterialTheme.typography.bodySmall
    )

    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun InfoHeader(text: String) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer.darken(0.2f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(vertical = 4.dp)
    ) {
        Text(
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            text = text
        )
    }

    Spacer(modifier = Modifier.height(3.dp))
}

@Composable
private fun OtherInfo(formattedDate: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                MaterialTheme.shapes.medium
            )
    ) {
        InfoHeader(text = "Other information")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 14.dp,
                    vertical = 4.dp
                )
        ) {
            Text(
                style = MaterialTheme.typography.titleSmall,
                text = "Friends since - $formattedDate"
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
    }
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
private fun RouteOptionsApplier(
    controller: NavHostController,
    username: String
) {
    val settled = LocalHorizontalPagerSettled.current
    var applierRequired by remember(settled) { mutableStateOf(settled) }

    if (applierRequired) {
        LocalRouteOptionsApplier.current {
            if (username.isNotBlank()) {
                Route.Options(title = {
                    RouteTitle(
                        stringResource(
                            R.string.route_friend,
                            username
                        )
                    )
                })
            } else {
                it
            }
        }

        applierRequired = false
    }

    LaunchedEffect(controller.currentDestination) {
        if (controller.currentDestination?.route == Routes.Friend.path) {
            applierRequired = true
        }
    }
}