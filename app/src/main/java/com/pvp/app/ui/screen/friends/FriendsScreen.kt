package com.pvp.app.ui.screen.friends

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FriendsScreen(
    model: FriendsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val toastMessage by model.toastMessage
    val user by model.user.collectAsStateWithLifecycle()
    val friends = user?.friends ?: emptyList()
    val receivedRequests = user?.receivedRequests ?: emptyList()
    val sentRequests = user?.sentRequests ?: emptyList()
    val friendEmail = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val showRequests = remember { mutableStateOf(false) }
    val selectedTab = remember { mutableStateOf(0) }
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
            Button(
                onClick = { showDialog.value = true },
                modifier = Modifier.size(
                    width = 45.dp,
                    height = 35.dp
                ),
                shape = MaterialTheme.shapes.small,
                contentPadding = PaddingValues(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.GroupAdd,
                    contentDescription = "Add friend",
                    modifier = Modifier.size(25.dp)
                )
            }

            if (showDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog.value = false
                        friendEmail.value = ""
                    },
                    confirmButton = {
                        Button(onClick = {
                            model.addFriend(friendEmail.value)
                            showDialog.value = false
                            friendEmail.value = ""
                        }) {
                            Text("Add")
                        }
                    },
                    dismissButton = {
                        Button(onClick = {
                            showDialog.value = false
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


            Button(
                onClick = { showRequests.value = true },
                modifier = Modifier.size(
                    width = 160.dp,
                    height = 35.dp
                ),
                shape = MaterialTheme.shapes.small,
                contentPadding = PaddingValues(4.dp)
            ) {
                Text(
                    text = "Requests",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (showRequests.value) {
                AlertDialog(
                    onDismissRequest = {
                        showRequests.value = false
                        selectedTab.value = 0
                    },
                    confirmButton = {
                        Button(onClick = {
                            showRequests.value = false
                            selectedTab.value = 0
                        }) {
                            Text("Back")
                        }
                    },
                    text = {
                        Column {
                            TabRow(selectedTabIndex = selectedTab.value) {
                                Tab(
                                    selected = selectedTab.value == 0,
                                    onClick = { selectedTab.value = 0 }
                                ) {
                                    Text(
                                        "Received",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Tab(
                                    selected = selectedTab.value == 1,
                                    onClick = { selectedTab.value = 1 }
                                ) {
                                    Text(
                                        "Sent",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }

                            when (selectedTab.value) {
                                0 -> {
                                    for (request in receivedRequests) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(30.dp)
                                                .padding(
                                                    vertical = 2.dp
                                                )
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

                                            Icon(
                                                imageVector = Icons.Outlined.CheckCircle,
                                                contentDescription = "Accept request",
                                                tint = Color.Green,
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .clickable { model.acceptFriendRequest(request) }
                                            )

                                            Spacer(modifier = Modifier.width(6.dp))

                                            Icon(
                                                imageVector = Icons.Outlined.DoNotDisturbOn,
                                                contentDescription = "Deny request",
                                                tint = Color.Red,
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .clickable { model.denyFriendRequest(request) }
                                            )
                                        }
                                    }
                                }

                                1 -> {
                                    for (request in sentRequests) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(30.dp)
                                                .padding(
                                                    vertical = 2.dp
                                                )
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
                                            Icon(
                                                imageVector = Icons.Outlined.DoNotDisturbOn,
                                                contentDescription = "Deny request",
                                                tint = Color.Red,
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .clickable { /* TODO */ }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    },
                )
            }

            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.size(
                    width = 45.dp,
                    height = 35.dp
                ),
                shape = MaterialTheme.shapes.small,
                contentPadding = PaddingValues(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.FilterAlt,
                    contentDescription = "Filter",
                    modifier = Modifier.size(25.dp)
                )
            }
        }

        Text(
            "All friends - ${friends.size}",
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 6.dp)
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
                            .background(MaterialTheme.colorScheme.primary)
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
}
