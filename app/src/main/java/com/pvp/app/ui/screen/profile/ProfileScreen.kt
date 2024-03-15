@file:Suppress("LocalVariableName")

package com.pvp.app.ui.screen.profile

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.R
import com.pvp.app.model.Ingredient
import com.pvp.app.model.SportActivity
import com.pvp.app.ui.common.ButtonWithDialog
import com.pvp.app.ui.common.IconButtonWithDialog
import com.pvp.app.ui.common.ProgressIndicator
import com.pvp.app.ui.common.UserInfoItem
import com.pvp.app.ui.common.showToast
import com.pvp.app.ui.common.underline
import com.pvp.app.ui.screen.filters.FiltersDialog
import com.pvp.app.ui.screen.filters.FiltersItem

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
    val allActivities = remember {
        SportActivity
            .entries
            .map { it.title }
    }
    val allIngredients = remember {
        Ingredient
            .entries
            .map { it.title }
    }

    var heightDisplay by remember { mutableIntStateOf(state.value.user.height) }
    var heightEditing by remember { mutableStateOf(heightDisplay.toString()) }

    var massDisplay by remember { mutableIntStateOf(state.value.user.mass) }
    var massEditing by remember { mutableStateOf(massDisplay.toString()) }

    val _ingredients = remember { state.value.user.ingredients }
    val _selectedIngredients = remember { _ingredients.map { it.title } }
    val _unselectedIngredients = remember { allIngredients - _selectedIngredients.toSet() }
    var ingredientsDisplay by remember { mutableStateOf(_selectedIngredients) }
    var ingredientsEditingSelected by remember { mutableStateOf(_selectedIngredients) }
    var ingredientsEditingUnselected by remember { mutableStateOf(_unselectedIngredients) }

    val _activities = remember { state.value.user.activities }
    val _selectedActivities = remember { _activities.map { it.title } }
    val _unselectedActivities = remember { allActivities - _selectedActivities.toSet() }
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

        FiltersItem(
            title = "Sport activities filter:",
            selectedFilters = activitiesDisplay,
            dialogTitle = {
                Text("Editing sport activities filter")
            },
            dialogContent = {
                FiltersDialog(
                    selectedFilters = activitiesEditingSelected,
                    unselectedFilters = activitiesEditingUnselected,
                    onValueChange = { newSelected, newUnselected ->
                        activitiesEditingSelected = newSelected
                        activitiesEditingUnselected = newUnselected
                    }
                )
            },
            onConfirmClick = {
                activitiesDisplay = activitiesEditingSelected

                onUpdateActivities(activitiesEditingSelected)

                context.showToast(message = "Your sport activities have been updated!")
            },
            onDismiss = {
                activitiesEditingSelected = activitiesDisplay
                activitiesEditingUnselected = allActivities - activitiesDisplay.toSet()
            }
        )

        FiltersItem(
            title = "Meal ingredients filter:",
            selectedFilters = ingredientsDisplay,
            dialogTitle = {
                Text("Editing meal ingredients filter")
            },
            dialogContent = {
                FiltersDialog(
                    selectedFilters = ingredientsEditingSelected,
                    unselectedFilters = ingredientsEditingUnselected,
                    onValueChange = { newSelected, newUnselected ->
                        ingredientsEditingSelected = newSelected
                        ingredientsEditingUnselected = newUnselected
                    }
                )
            },
            onConfirmClick = {
                ingredientsDisplay = ingredientsEditingSelected

                onUpdateIngredients(ingredientsEditingSelected)

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
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
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