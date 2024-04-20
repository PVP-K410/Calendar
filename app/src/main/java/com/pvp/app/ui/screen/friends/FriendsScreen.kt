package com.pvp.app.ui.screen.friends

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.model.FriendObject
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.common.ButtonConfirm
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
    model: FriendsViewModel = hiltViewModel(),
    modifier: Modifier
) {
    val context = LocalContext.current
    val toastMessage by model.toastMessage
    val friendObject by model.userFriendObject.collectAsStateWithLifecycle()
    val friendEmail = remember { mutableStateOf("") }
    val sortingType = remember { mutableStateOf(SortingType.EXPERIENCE) }
    val friends by model.userFriends.collectAsState()
    val friendsSorted = remember { mutableStateOf(friends) }
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
        friends
    ) {
        friendsSorted.value = when (sortingType.value) {
            SortingType.EXPERIENCE -> friends.sortedByDescending { it.user.experience }
            SortingType.POINTS -> friends.sortedByDescending { it.user.points }
        }
    }

    Column(
        modifier = modifier
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
                dialogTitle = { Text("Add friend") },
                dialogContent = {
                    OutlinedTextField(
                        value = friendEmail.value,
                        onValueChange = { friendEmail.value = it },
                        label = { Text("Friend's email") },
                    )
                },
                confirmButtonContent = { Text("Add") },
                onConfirm = {
                    model.addFriend(friendEmail.value.trim())
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Requests",
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
            "All friends - ${friends.size}",
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            style = MaterialTheme.typography.titleMedium
        )

        FriendList(
            friends = friendsSorted.value,
            friendObject = friendObject,
            model = model,
            scrollState = scrollState
        )
    }
}

@Composable
private fun FriendList(
    friends: List<FriendEntry>,
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
            CustomButtonWithDialog(
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
                    ListItemContent(
                        index,
                        friend
                    )
                },
                contentPadding = PaddingValues(2.dp),
                dialogTitle = { Text("${friend.user.username} information") },
                dialogContent = {
                    DialogContent(
                        friend,
                        friendObject,
                        model
                    )
                },
                confirmButtonContent = { Text("Remove") },
                shape = MaterialTheme.shapes.small,
                confirmationTitle = { Text("Are you sure you want to delete this friend?") },
                confirmationOnConfirm = { model.removeFriend(friend.user.email) },
                confirmationDescription = { Text("If the friend is deleted, it cannot be recovered") }
            )
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
                .size(30.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Image(
                bitmap = friend.avatar,
                contentDescription = "Friend avatar",
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
            )
        }
    }

    Spacer(modifier = Modifier.width(6.dp))
}

@Composable
private fun DialogContent(
    friend: FriendEntry,
    friendObject: FriendObject,
    model: FriendsViewModel
) {
    val tasksCompleted by model.tasksCompleted.collectAsState()
    val mutualFriends by model.mutualFriends.collectAsState()
    val friendInfo = friendObject.friends.find { it.email == friend.user.email }

    if (friendInfo != null) {
        val sinceDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(friendInfo.since),
            ZoneId.systemDefault()
        )

        val formattedDate = sinceDateTime.format(
            DateTimeFormatter.ofPattern(
                "yyyy/MM/dd"
            )
        )

        model.tasksCompleted(friend.user.email)

        model.getMutualFriends(friend.user.email)

        Column(
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth()
                .height(300.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AvatarBox(friend)

            Spacer(modifier = Modifier.height(6.dp))

            Experience(
                experience = friend.user.experience,
                experienceRequired = (friend.user.level + 1) * (friend.user.level + 1) * 13,
                level = friend.user.level,
                paddingStart = 0.dp,
                paddingEnd = 0.dp,
                fontSize = 14,
                fontWeight = FontWeight.Normal,
                height = 26.dp,
                textStyle = MaterialTheme.typography.titleSmall,
                progressTextStyle = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(6.dp))

            ActivityInfo(tasksCompleted)

            FriendInfo(formattedDate, mutualFriends)
        }
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
                text = "$tasksCompleted tasks completed"
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
    }
}

@Composable
private fun ActivityRow(
    icon: ImageVector,
    contentDescription: String,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun FriendInfo(
    formattedDate: String,
    mutualFriends: List<FriendEntry>
) {
    InfoHeader(text = "Friend information")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
            .padding(
                vertical = 4.dp,
                horizontal = 14.dp
            )
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = mutualFriend.user.username,
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
            text = text,
            style = MaterialTheme.typography.titleSmall
        )
    }

    Spacer(modifier = Modifier.height(3.dp))
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

@Composable
private fun CustomButtonWithDialog(
    modifier: Modifier = Modifier,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    content: @Composable RowScope.() -> Unit = { Text("Open Dialog") },
    contentAlignment: Alignment = Alignment.TopStart,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    confirmButtonContent: @Composable RowScope.() -> Unit = { Text("Confirm") },
    dismissButtonContent: @Composable RowScope.() -> Unit = { Text("Dismiss") },
    dialogTitle: @Composable () -> Unit = { Text("Dialog Title") },
    dialogContent: @Composable () -> Unit = { Text("Dialog Content") },
    onDismiss: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.extraSmall,
    confirmationTitle: @Composable () -> Unit = { Text("Confirm to proceed") },
    confirmationDescription: @Composable () -> Unit = { },
    confirmationOnConfirm: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        contentAlignment = contentAlignment,
        modifier = modifier
    ) {
        Button(
            border = border,
            colors = colors,
            content = content,
            contentPadding = contentPadding,
            onClick = { showDialog = true },
            shape = shape
        )
    }

    CustomDialog(
        buttonContentConfirm = confirmButtonContent,
        buttonContentDismiss = dismissButtonContent,
        content = dialogContent,
        onDismiss = {
            onDismiss()

            showDialog = false
        },
        show = showDialog,
        title = dialogTitle,
        confirmationDescription = confirmationDescription,
        confirmationTitle = confirmationTitle,
        confirmationOnConfirm = confirmationOnConfirm
    )
}

@Composable
private fun CustomDialog(
    content: @Composable () -> Unit,
    title: @Composable () -> Unit,
    buttonContentConfirm: @Composable RowScope.() -> Unit,
    buttonContentDismiss: @Composable RowScope.() -> Unit,
    onDismiss: () -> Unit,
    show: Boolean,
    confirmationTitle: @Composable () -> Unit = { Text("Confirm to proceed") },
    confirmationDescription: @Composable () -> Unit = { },
    confirmationOnConfirm: () -> Unit
) {
    if (!show) {
        return
    }

    AlertDialog(
        confirmButton = {
            Box(contentAlignment = Alignment.BottomEnd) {
                ButtonConfirm(
                    border = BorderStroke(
                        1.dp,
                        Color.Red
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    content = buttonContentConfirm,
                    confirmationButtonContent = buttonContentConfirm,
                    confirmationDescription = confirmationDescription,
                    confirmationTitle = confirmationTitle,
                    onConfirm = confirmationOnConfirm
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        dismissButton = {
            Box(contentAlignment = Alignment.BottomEnd) {
                OutlinedButton(
                    content = buttonContentDismiss,
                    onClick = onDismiss,
                    shape = MaterialTheme.shapes.extraLarge
                )
            }
        },
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraSmall,
        text = content,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        title = title,
        titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
