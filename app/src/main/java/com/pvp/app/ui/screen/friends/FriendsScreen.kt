package com.pvp.app.ui.screen.friends

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = friendEmail.value,
            onValueChange = { newValue -> friendEmail.value = newValue },
            label = { Text("Friend's Email") }
        )

        Button(onClick = { model.addFriend(friendEmail.value) }) {
            Text("Add Friend")
        }

        Text(
            "Friends",
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )

        for (friend in friends) {
            Text(friend)
        }

        Text(
            "Received requests",
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )

        for (request in receivedRequests) {
            Row {
                Text(request)

                Button(onClick = { model.acceptFriendRequest(request) }) {
                    Text("Accept")
                }

                Button(onClick = { model.denyFriendRequest(request) }) {
                    Text("Deny")
                }
            }
        }

        Text(
            "Sent requests",
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )

        for (request in sentRequests) {
            Text(request)
        }
    }
}
