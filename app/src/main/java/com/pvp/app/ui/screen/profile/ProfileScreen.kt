package com.pvp.app.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.R
import com.pvp.app.model.User
import com.pvp.app.ui.common.showToast
import com.pvp.app.ui.common.underline

@Composable
private fun ProfileBody(
    modifier: Modifier = Modifier,
    user: State<User?>,
    onUpdateMass: (Int) -> Unit,
    onUpdateHeight: (Int) -> Unit,
) {
    var heightDisplay by remember { mutableIntStateOf(user.value?.height ?: 0) }
    var heightEditing by remember { mutableIntStateOf(user.value?.height ?: 0) }
    var massDisplay by remember { mutableIntStateOf(user.value?.mass ?: 0) }
    var massEditing by remember { mutableIntStateOf(user.value?.mass ?: 0) }

    LaunchedEffect(user.value) {
        heightDisplay = user.value?.height ?: 0
        heightEditing = user.value?.height ?: 0
        massDisplay = user.value?.mass ?: 0
        massEditing = user.value?.mass ?: 0
    }

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        UserInfoItem(
            label = "Your mass:",
            value = "$massDisplay kg",
            dialogTitle = {
                Text("Editing Mass")
            },
            dialogContent = {
                OutlinedTextField(
                    value = massEditing.toString(),
                    onValueChange = { massEditing = it.toIntOrNull() ?: 0 },
                    label = { Text("Mass") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            },
            onConfirmClick = {
                onUpdateMass(massEditing)
                massDisplay = massEditing
            }
        )

        UserInfoItem(
            label = "Your height:",
            value = "$heightDisplay cm",
            dialogTitle = {
                Text("Editing Height")
            },
            dialogContent = {
                OutlinedTextField(
                    value = heightEditing.toString(),
                    onValueChange = { heightEditing = it.toIntOrNull() ?: 0 },
                    label = { Text("Height") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            },
            onConfirmClick = {
                onUpdateHeight(heightEditing)
                heightDisplay = heightEditing
            }
        )
    }
}

@Composable
private fun ProfileFooter(
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit
) {
    val textSignOut = stringResource(R.string.screen_profile_button_sign_out)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onSignOut
        ) {
            Text(
                text = textSignOut,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    modifier: Modifier = Modifier,
    user: State<User?>,
    onUpdateUsername: (String) -> Unit
) {
    var userNameDisplay by remember { mutableStateOf(user.value?.username ?: "") }
    var userNameEditing by remember { mutableStateOf(user.value?.username ?: "") }

    LaunchedEffect(user.value) {
        userNameDisplay = user.value?.username ?: ""
        userNameEditing = user.value?.username ?: ""
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.underline()
        ) {
            Text(
                text = "Welcome, ${userNameDisplay}!",
                style = MaterialTheme.typography.titleLarge,
                fontStyle = FontStyle.Italic
            )

            IconButtonWithDialog(
                icon = Icons.Default.Edit,
                dialogTitle = {
                    Text("Editing Username")
                },
                dialogContent = {
                    OutlinedTextField(
                        value = userNameEditing,
                        onValueChange = { userNameEditing = it },
                        label = { Text("Username") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                },
                onConfirmClick = {
                    onUpdateUsername(userNameEditing)
                    userNameDisplay = userNameEditing
                }
            )
        }

        Text(
            text = "${user.value?.email}",
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic
        )
    }
}

@Composable
private fun UserInfoItem(
    label: String,
    value: String,
    dialogTitle: @Composable () -> Unit,
    dialogContent: @Composable () -> Unit,
    onConfirmClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), // Apply fillMaxWidth() to Row
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    color = Color.Black,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.underline()
                )

                Text(
                    text = value,
                    color = Color.Black
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                IconButtonWithDialog(
                    icon = Icons.Default.Edit,
                    dialogTitle = dialogTitle,
                    dialogContent = dialogContent,
                    onConfirmClick = onConfirmClick
                )
            }
        }
    }
}

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user = viewModel.user.collectAsStateWithLifecycle()
    val textSignOut = stringResource(R.string.screen_profile_toast_success_sign_out)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        ProfileHeader(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.15f)
                .padding(bottom = 8.dp),
            user = user,
            onUpdateUsername = {
                viewModel.updateUsername(it)
            }
        )

        ProfileBody(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f),
            user = user,
            onUpdateMass = {
                viewModel.updateMass(it)
            },
            onUpdateHeight = {
                viewModel.updateHeight(it)
            }
        )

        val context = LocalContext.current

        ProfileFooter(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.15f),
            onSignOut = {
                viewModel.signOut {
                    context.showToast(
                        isSuccess = it.isSuccess,
                        messageError = it.messageError ?: "Error has occurred while signing out",
                        messageSuccess = textSignOut
                    )
                }
            }
        )
    }
}

@Composable
fun IconButtonWithDialog(
    icon: ImageVector,
    dialogTitle: @Composable () -> Unit,
    dialogContent: @Composable () -> Unit,
    onConfirmClick: () -> Unit
) {
    val showDialog = remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = {
                showDialog.value = true
            },
            modifier = Modifier
                .size(35.dp)
                .padding(5.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        }

        if (showDialog.value) {
            AlertDialog(
                modifier = Modifier.padding(8.dp),
                title = dialogTitle,
                text = dialogContent,
                onDismissRequest = {
                    showDialog.value = false
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog.value = false
                            onConfirmClick()
                        }
                    ) {
                        Text(text = "Edit")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDialog.value = false
                        },
                    ) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
    }
}
