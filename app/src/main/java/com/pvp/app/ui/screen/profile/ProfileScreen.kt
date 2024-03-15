package com.pvp.app.ui.screen.profile

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.R
import com.pvp.app.model.Ingredient
import com.pvp.app.model.SportActivity
import com.pvp.app.ui.common.ProgressIndicator
import com.pvp.app.ui.common.showToast
import com.pvp.app.ui.common.underline

@Composable
private fun ProfileBody(
    modifier: Modifier = Modifier,
    state: State<ProfileState>,
    context: Context = LocalContext.current,
    onUpdateMass: (Int) -> Unit,
    onUpdateHeight: (Int) -> Unit,
    onUpdateIngredients: (List<String>) -> Unit,
    onUpdateActivities: (List<String>) -> Unit,
) {
    val allActivities = SportActivity
        .entries
        .map { it.title }

    val allIngredients = Ingredient
        .entries
        .map { it.title }

    var heightDisplay by remember { mutableIntStateOf(state.value.user.height) }
    var heightEditing by remember { mutableStateOf(heightDisplay.toString()) }

    var massDisplay by remember { mutableIntStateOf(state.value.user.mass) }
    var massEditing by remember { mutableStateOf(massDisplay.toString()) }

    val _ingredients by remember { mutableStateOf(state.value.user.ingredients) }
    val _selectedIngredients = _ingredients.map { it.title }
    val _unselectedIngredients = allIngredients - _selectedIngredients.toSet()
    var ingredientsDisplay by remember { mutableStateOf(_selectedIngredients) }
    var ingredientsEditingSelected by remember { mutableStateOf(_selectedIngredients) }
    var ingredientsEditingUnselected by remember { mutableStateOf(_unselectedIngredients) }

    val _activities by remember { mutableStateOf(state.value.user.activities) }
    val _selectedActivities = _activities.map { it.title }
    val _unselectedActivities = allActivities - _selectedActivities.toSet()
    var activitiesDisplay by remember { mutableStateOf(_selectedActivities) }
    var activitiesEditingSelected by remember { mutableStateOf(_selectedActivities) }
    var activitiesEditingUnselected by remember { mutableStateOf(_unselectedActivities) }


    Column(
        modifier = modifier
            .padding(
                start = 30.dp,
                end = 30.dp
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        UserInfoItem(
            label = "Your mass:",
            value = "$massDisplay kg",
            dialogTitle = { Text("Editing Mass") },
            dialogContent = {
                OutlinedTextField(
                    value = massEditing,
                    onValueChange = { massEditing = it },
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
                val newMass = massEditing.toIntOrNull() ?: 0

                if (newMass in 2..700) {
                    massDisplay = newMass

                    onUpdateMass(newMass)
                    context.showToast(message = "Your mass has been updated!")
                } else {
                    massEditing = massDisplay.toString()

                    context.showToast(message = "Please enter a valid mass!")
                }
            },
            onDismiss = {
                massEditing = massDisplay.toString()
            }
        )

        UserInfoItem(
            label = "Your height:",
            value = "$heightDisplay cm",
            dialogTitle = { Text("Editing Height") },
            dialogContent = {
                OutlinedTextField(
                    value = heightEditing,
                    onValueChange = { heightEditing = it },
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
                val newHeight = heightEditing.toIntOrNull() ?: 0

                if (newHeight in 40..300) {
                    heightDisplay = newHeight

                    onUpdateHeight(newHeight)
                    context.showToast(message = "Your height has been updated!")
                } else {
                    massEditing = massDisplay.toString()

                    context.showToast(message = "Please enter a valid height!")
                }
            },
            onDismiss = {
                heightEditing = heightDisplay.toString()
            }
        )

        UserFiltersItem(
            title = "Sport activities filter:",
            filters = activitiesDisplay,
            dialogTitle = {
                Text("Editing sport activities filter")
            },
            dialogContent = {
                UserFiltersDialog(
                    selected = activitiesEditingSelected,
                    unselected = activitiesEditingUnselected,
                    onValueChange = { newSelected, newUnselected ->
                        activitiesEditingSelected = newSelected
                        activitiesEditingUnselected = newUnselected
                    }
                )
            },
            onConfirmClick = {
                onUpdateActivities(activitiesEditingSelected)
                activitiesDisplay = activitiesEditingSelected

                context.showToast(message = "Your sport activities have been updated!")
            },
            onDismiss = {
                activitiesEditingSelected = activitiesDisplay
                activitiesEditingUnselected = allActivities - activitiesDisplay.toSet()
            }
        )

        UserFiltersItem(
            title = "Meal ingredients filter:",
            filters = ingredientsDisplay,
            dialogTitle = {
                Text("Editing meal ingredients filter")
            },
            dialogContent = {
                UserFiltersDialog(
                    selected = ingredientsEditingSelected,
                    unselected = ingredientsEditingUnselected,
                    onValueChange = { newSelected, newUnselected ->
                        ingredientsEditingSelected = newSelected
                        ingredientsEditingUnselected = newUnselected
                    }
                )
            },
            onConfirmClick = {
                onUpdateIngredients(ingredientsEditingSelected)
                ingredientsDisplay = ingredientsEditingSelected

                context.showToast(message = "Your sport activities have been updated!")
            },
            onDismiss = {
                ingredientsEditingSelected = ingredientsDisplay
                ingredientsEditingUnselected = allIngredients - ingredientsDisplay.toSet()
            }
        )
    }
}

@Composable
private fun ProfileFooter(
    onSignOut: () -> Unit
) {
    val textSignOut = stringResource(R.string.screen_profile_button_sign_out)


    ButtonWithDialog(
        modifier = Modifier
            .padding(
                top = 20.dp,
                bottom = 10.dp,
                end = 10.dp
            )
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd,
        mainButtonContent = {
            Text(textSignOut)
        },
        dismissButtonContent = {
            Text("Cancel")
        },
        confirmButtonContent = {
            Text("Sign Out")
        },
        dialogTitle = {
            Row {
                Icon(
                    imageVector = Icons.Default.DoorFront,
                    contentDescription = "Sign Out Icon",
                    modifier = Modifier
                        .padding(
                            end = 6.dp,
                            top = 4.dp
                        )
                        .size(24.dp)
                )
                Text(text = "Sign Out?")
            }
        },
        dialogContent = {
            Text(text = "Are you sure you want to sign out?")
        },
        onConfirmClick = {
            onSignOut()
        }
    )

}

@Composable
private fun ProfileHeader(
    state: State<ProfileState>,
    context: Context = LocalContext.current,
    onUpdateUsername: (String) -> Unit
) {
    var userNameDisplay by remember { mutableStateOf(state.value.user.username) }
    var userNameEditing by remember { mutableStateOf(userNameDisplay) }
    val emailDisplay by remember { mutableStateOf(state.value.user.email) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 30.dp,
                end = 30.dp,
                bottom = 15.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            contentDescription = "Profile screen icon",
            modifier = Modifier
                .size(
                    width = 200.dp,
                    height = 200.dp
                ),
            painter = BitmapPainter(state.value.userAvatar),
            alignment = Alignment.TopCenter,
        )

        Row(
            modifier = Modifier
                .padding(top = 15.dp)
                .underline()
        ) {
            Text(
                text = "Welcome, $userNameDisplay",
                style = MaterialTheme.typography.titleLarge,
                fontStyle = FontStyle.Italic,
                fontSize = 30.sp
            )

            IconButtonWithDialog(
                modifier = Modifier
                    .padding(
                        start = 5.dp,
                        top = 4.dp
                    ),
                icon = Icons.Default.Edit,
                iconSize = 30.dp,
                confirmButtonContent = {
                    Text("Edit")
                },
                dismissButtonContent = {
                    Text("Cancel")
                },
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
                    if (userNameEditing.isNotEmpty()) {
                        userNameDisplay = userNameEditing

                        onUpdateUsername(userNameEditing)
                        context.showToast(message = "Your username has been updated!")
                    } else if (userNameEditing.length > 30) {
                        userNameEditing = userNameDisplay

                        context.showToast(message = "Username cannot be longer than 30 characters")
                    } else {
                        userNameEditing = userNameDisplay

                        context.showToast(message = "Username cannot be empty!")
                    }
                },
                onDismiss = {
                    userNameEditing = userNameDisplay
                }
            )
        }

        Text(
            modifier = Modifier.padding(bottom = 20.dp),
            text = emailDisplay,
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
    onConfirmClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.underline()
                )

                Text(
                    text = value,
                    fontStyle = FontStyle.Italic
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                IconButtonWithDialog(
                    icon = Icons.Default.Edit,
                    iconSize = 30.dp,
                    confirmButtonContent = {
                        Text("Edit")
                    },
                    dismissButtonContent = {
                        Text("Cancel")
                    },
                    dialogTitle = dialogTitle,
                    dialogContent = dialogContent,
                    onConfirmClick = onConfirmClick,
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
private fun UserFiltersItem(
    title: String,
    filters: List<String>,
    dialogTitle: @Composable () -> Unit,
    dialogContent: @Composable () -> Unit,
    onConfirmClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.underline()
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButtonWithDialog(
                        icon = Icons.Default.Edit,
                        iconSize = 30.dp,
                        confirmButtonContent = {
                            Text("Edit")
                        },
                        dismissButtonContent = {
                            Text("Cancel")
                        },
                        dialogTitle = dialogTitle,
                        dialogContent = dialogContent,
                        onConfirmClick = onConfirmClick,
                        onDismiss = onDismiss
                    )
                }
            }

            UserFiltersBox(
                filters = filters
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserFiltersBox(
    title: String? = null,
    filters: List<String>,
    onClick: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = TextStyle(fontSize = 18.sp),
                    modifier = Modifier.padding(8.dp)
                )
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                filters
                    .sorted()
                    .forEach { filter ->
                        Card(
                            modifier = Modifier
                                .padding(
                                    end = 3.dp,
                                    bottom = 3.dp
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceDim,
                            ),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.background
                            ),
                            onClick = {
                                onClick(filter)
                            }
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(8.dp),
                                text = filter,
                                style = TextStyle(fontSize = 14.sp),
                            )
                        }
                    }
            }
        }
    }
}

@Composable
fun UserFiltersDialog(
    selected: List<String>,
    unselected: List<String>,
    onValueChange: (List<String>, List<String>) -> Unit
) {
    var selectedFilters by remember { mutableStateOf(selected) }
    var unselectedFilters by remember { mutableStateOf(unselected) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        UserFiltersBox(
            title = "Active filters:",
            filters = selectedFilters,
            onClick = { filter ->
                selectedFilters = selectedFilters.minus(filter)
                unselectedFilters = unselectedFilters.plus(filter)

                onValueChange(selectedFilters, unselectedFilters)
            }
        )

        UserFiltersBox(
            title = "Remaining filters:",
            filters = unselectedFilters,
            onClick = { filter ->
                unselectedFilters = unselectedFilters.minus(filter)
                selectedFilters = selectedFilters.plus(filter)

                onValueChange(selectedFilters, unselectedFilters)
            }
        )
    }
}

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    val textSignOut = stringResource(R.string.screen_profile_toast_success_sign_out)
    val context = LocalContext.current

    if (state.value.isLoading) {
        ProgressIndicator()

        return
    }

    Box(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ProfileHeader(
                    state = state,
                    onUpdateUsername = {
                        viewModel.updateUserInformation(newUsername = it)
                    }
                )
            }

            ProfileBody(
                modifier = Modifier.fillMaxWidth(),
                state = state,
                onUpdateMass = {
                    viewModel.updateUserInformation(newMass = it)
                },
                onUpdateHeight = {
                    viewModel.updateUserInformation(newHeight = it)
                },
                onUpdateActivities = {
                    viewModel.updateUserInformation(newActivityFilters = it)
                },
                onUpdateIngredients = {
                    viewModel.updateUserInformation(newIngredientFilters = it)
                }
            )

            ProfileFooter(
                onSignOut = {
                    viewModel.signOut {
                        context.showToast(
                            isSuccess = it.isSuccess,
                            messageError = it.messageError
                                ?: "Error has occurred while signing out",
                            messageSuccess = textSignOut
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun IconButtonWithDialog(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Edit,
    iconSize: Dp = 20.dp,
    confirmButtonContent: @Composable RowScope.() -> Unit = { Text("Confirm") },
    dismissButtonContent: @Composable RowScope.() -> Unit = { Text("Dismiss") },
    dialogTitle: @Composable () -> Unit = { Text("Dialog Title") },
    dialogContent: @Composable () -> Unit = { Text("Dialog Content") },
    onConfirmClick: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        IconButton(
            onClick = {
                showDialog = true
            },
            modifier = Modifier.size(iconSize)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            title = dialogTitle,
            text = dialogContent,
            onDismissRequest = {
                onDismiss()

                showDialog = false
            },
            confirmButton = {
                Box(
                    contentAlignment = Alignment.BottomStart
                ) {
                    Button(
                        content = confirmButtonContent,
                        onClick = {
                            onConfirmClick()

                            showDialog = false
                        }
                    )
                }

            },
            dismissButton = {
                Box(
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Button(
                        content = dismissButtonContent,
                        onClick = {
                            onDismiss()

                            showDialog = false
                        }
                    )
                }
            }
        )
    }
}

@Composable
fun ButtonWithDialog(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    mainButtonContent: @Composable RowScope.() -> Unit = { Text("Open Dialog") },
    confirmButtonContent: @Composable RowScope.() -> Unit = { Text("Confirm") },
    dismissButtonContent: @Composable RowScope.() -> Unit = { Text("Dismiss") },
    dialogTitle: @Composable () -> Unit = { Text("Dialog Title") },
    dialogContent: @Composable () -> Unit = { Text("Dialog Content") },
    onConfirmClick: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier,
        contentAlignment = contentAlignment
    ) {
        Button(
            content = mainButtonContent,
            onClick = {
                showDialog = true
            }
        )
    }

    if (showDialog) {
        AlertDialog(
            title = dialogTitle,
            text = dialogContent,
            onDismissRequest = {
                onDismiss()

                showDialog = false
            },
            confirmButton = {
                Box(
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Button(
                        content = confirmButtonContent,
                        onClick = {
                            onConfirmClick()

                            showDialog = false
                        }
                    )
                }
            },
            dismissButton = {
                Box(
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Button(
                        content = dismissButtonContent,
                        onClick = {
                            onDismiss()

                            showDialog = false
                        },
                    )
                }
            }
        )
    }
}
