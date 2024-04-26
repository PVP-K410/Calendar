@file:Suppress("LocalVariableName")

package com.pvp.app.ui.screen.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.model.Ingredient
import com.pvp.app.model.SportActivity
import com.pvp.app.ui.common.ButtonConfirm
import com.pvp.app.ui.common.EditableInfoItem
import com.pvp.app.ui.common.Experience
import com.pvp.app.ui.common.IconButtonWithDialog
import com.pvp.app.ui.common.ProgressIndicatorWithinDialog
import com.pvp.app.ui.common.showToast
import kotlinx.coroutines.launch

private val ACTIVITIES = SportActivity.entries.map { it.title }
private val INGREDIENTS = Ingredient.entries.map { it.title }

@Composable
private fun AccountDeleteButton(
    viewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val username by remember { mutableStateOf(viewModel.state.value.user.username) }
    var input by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ButtonConfirm(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(
                    top = 30.dp,
                    bottom = 20.dp
                ),
            border = BorderStroke(
                1.dp,
                Color.Red
            ),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
            contentAlignment = Alignment.BottomCenter,
            shape = MaterialTheme.shapes.extraLarge,
            content = {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        contentDescription = null,
                        imageVector = Icons.Outlined.Delete
                    )

                    Text(
                        textAlign = TextAlign.Center,
                        text = "Delete Account"
                    )
                }
            },
            confirmationButtonContent = { Text("Delete Account") },
            confirmationTitle = { Text("Are you sure you want to delete your account?") },
            confirmationDescription = {
                Column {
                    Text("Your account will be permanently deleted and recovery will not be possible.")

                    Spacer(modifier = Modifier.height(30.dp))

                    Text("To confirm, please enter your username \"$username\" in the box below:")

                    Spacer(modifier = Modifier.height(15.dp))

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        onValueChange = { newText ->
                            input = newText
                        },
                        value = input
                    )
                }
            },
            onConfirm = {
                if (input == username) {
                    coroutineScope.launch {
                        val result = viewModel.deleteAccount()

                        if (result.isSuccess) {
                            context.showToast(message = "Account deleted successfully")
                        } else {
                            context.showToast(message = "An error occurred while deleting the account")
                        }
                    }
                } else {
                    input = ""

                    context.showToast(message = "Incorrect username")
                }
            }
        )
    }
}

@Composable
private fun Initials(
    onUsernameChange: (String) -> Unit,
    model: ProfileViewModel = hiltViewModel(),
    state: ProfileState
) {
    val context = LocalContext.current
    val email by remember { mutableStateOf(state.user.email) }
    var userName by remember { mutableStateOf(state.user.username) }
    var userNameEdit by remember { mutableStateOf(userName) }
    val usernameInterval = remember { model.fromConfiguration { it.intervalUsernameLength } }
    val lengthMin = usernameInterval.first
    val lengthMax = usernameInterval.second

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = 15.dp,
                end = 30.dp,
                start = 30.dp
            ),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            alignment = Alignment.TopCenter,
            contentDescription = "Profile screen icon",
            modifier = Modifier.size(
                height = 200.dp,
                width = 200.dp
            ),
            painter = BitmapPainter(state.avatar)
        )

        Username(
            onChange = { userNameEdit = it },
            onDismiss = { userNameEdit = userName },
            onSave = {
                userNameEdit = userNameEdit.trim()

                if (userNameEdit.length in lengthMin..lengthMax) {
                    userName = userNameEdit

                    onUsernameChange(userNameEdit)

                    context.showToast(message = "Your username has been updated")
                } else {
                    userNameEdit = userName

                    context.showToast(message = "Username must be between $lengthMin and $lengthMax characters long")
                }
            },
            username = userName,
            usernameEdit = userNameEdit
        )

        Text(
            style = MaterialTheme.typography.bodyMedium,
            text = email
        )
    }
}

@Composable
private fun BoxScope.Points(
    points: Int
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(
                end = 30.dp,
                top = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 4.dp),
            text = "$points"
        )

        Icon(
            contentDescription = "Points indicator icon",
            imageVector = Icons.Outlined.Stars
        )
    }
}

@Composable
fun ProfileScreen(
    modifier: Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        ProgressIndicatorWithinDialog()
    }

    Box(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .then(modifier)
    ) {
        Points(points = state.user.points)

        Column(modifier = Modifier.fillMaxWidth()) {
            if (!state.isLoading) {
                Initials(
                    onUsernameChange = {
                        viewModel.update { u -> u.username = it }
                    },
                    state = state
                )

                Experience(
                    experience = state.user.experience,
                    experienceRequired = state.experienceRequired,
                    level = state.user.level
                )
            } else {
                Spacer(modifier = Modifier.height(180.dp))
            }

            Properties(
                onUpdateActivities = { viewModel.update { u -> u.activities = it } },
                onUpdateIngredients = { viewModel.update { u -> u.ingredients = it } },
                onUpdateHeight = { viewModel.update { u -> u.height = it } },
                onUpdateMass = { viewModel.update { u -> u.mass = it } },
                state = state
            )

            AccountDeleteButton(viewModel)
        }
    }
}

@Composable
private fun Properties(
    onUpdateActivities: (List<SportActivity>) -> Unit,
    onUpdateHeight: (Int) -> Unit,
    onUpdateIngredients: (List<Ingredient>) -> Unit,
    onUpdateMass: (Int) -> Unit,
    state: ProfileState
) {
    var activitiesSelected = remember(state.user.activities) {
        state.user.activities.map { it.title }
    }

    var activitiesSelectedEdit by remember { mutableStateOf(activitiesSelected) }
    var activitiesUnselected by remember { mutableStateOf(ACTIVITIES - activitiesSelected.toSet()) }
    val context = LocalContext.current
    var height by remember { mutableIntStateOf(state.user.height) }
    var heightEdit by remember { mutableStateOf(height.toString()) }

    var ingredientsSelected = remember(state.user.ingredients) {
        state.user.ingredients.map { it.title }
    }

    var ingredientsSelectedEdit by remember { mutableStateOf(ingredientsSelected) }
    var ingredientsUnselectedEdit by remember { mutableStateOf(INGREDIENTS - ingredientsSelected.toSet()) }
    var mass by remember { mutableIntStateOf(state.user.mass) }
    var massEdit by remember { mutableStateOf(mass.toString()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                end = 30.dp,
                start = 30.dp,
                top = 30.dp
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EditableInfoItem(
            dialogContent = {
                OutlinedTextField(
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    label = { Text("Mass") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    onValueChange = {
                        massEdit = it.replace(
                            " ",
                            ""
                        )
                    },
                    value = massEdit
                )
            },
            dialogTitle = { Text("Editing mass") },
            label = "Your mass",
            onConfirm = {
                val massNew = massEdit.toIntOrNull() ?: 0

                if (massNew in 2..700) {
                    mass = massNew

                    onUpdateMass(massNew)

                    context.showToast(message = "Your mass has been updated")
                } else {
                    massEdit = mass.toString()

                    context.showToast(message = "Please enter a mass between 2 and 700 kg")
                }
            },
            onDismiss = { massEdit = mass.toString() },
            value = "$mass kg"
        )

        EditableInfoItem(
            dialogContent = {
                OutlinedTextField(
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    label = { Text("Height") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    onValueChange = { heightEdit = it },
                    value = heightEdit
                )
            },
            dialogTitle = { Text("Editing height") },
            label = "Your height",
            onConfirm = {
                val newHeight = heightEdit.toIntOrNull() ?: 0

                if (newHeight in 40..300) {
                    height = newHeight

                    onUpdateHeight(newHeight)

                    context.showToast(message = "Your height has been updated")
                } else {
                    massEdit = mass.toString()

                    context.showToast(message = "Please enter a height between 40 and 300 cm")
                }
            },
            onDismiss = {
                heightEdit = height.toString()
            },
            value = "$height cm"
        )

        WeeklyActivitiesItem(
            title = "Your weekly activities",
            activities = state.user.weeklyActivities.map { it.title }
        )

        FiltersItem(
            dialogContent = {
                FiltersDialog(
                    boxTitle = "Activities that I like",
                    onValueChange = { selected, unselected ->
                        activitiesSelectedEdit = selected
                        activitiesUnselected = unselected
                    },
                    selectedFilters = activitiesSelectedEdit,
                    title = "activities",
                    unselectedFilters = activitiesUnselected
                )
            },
            dialogTitle = {
                Text("Editing preferable sport activities")
            },
            filtersType = "activities",
            onConfirmClick = {
                activitiesSelected = activitiesSelectedEdit

                onUpdateActivities(activitiesSelectedEdit.map { SportActivity.fromTitle(it) })

                context.showToast(message = "Your sport activities have been updated")
            },
            onDismiss = {
                activitiesSelectedEdit = activitiesSelected
                activitiesUnselected = ACTIVITIES - activitiesSelected.toSet()
            },
            selectedFilters = activitiesSelected,
            title = "Sport activities that you like"
        )

        FiltersItem(
            dialogContent = {
                FiltersDialog(
                    boxTitle = "Ingredients that I can't take",
                    onValueChange = { selected, unselected ->
                        ingredientsSelectedEdit = selected
                        ingredientsUnselectedEdit = unselected
                    },
                    selectedFilters = ingredientsSelectedEdit,
                    title = "ingredients",
                    unselectedFilters = ingredientsUnselectedEdit
                )
            },
            dialogTitle = {
                Text("Editing ingredients that you can't take")
            },
            filtersType = "ingredients",
            onConfirmClick = {
                ingredientsSelected = ingredientsSelectedEdit

                onUpdateIngredients(ingredientsSelectedEdit.mapNotNull { Ingredient.fromTitle(it) })

                context.showToast(message = "Your ingredients have been updated")
            },
            onDismiss = {
                ingredientsSelectedEdit = ingredientsSelected
                ingredientsUnselectedEdit = INGREDIENTS - ingredientsSelected.toSet()
            },
            selectedFilters = ingredientsSelected,
            title = "Ingredients that you can't take"
        )
    }
}

@Composable
private fun Username(
    onChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    username: String,
    usernameEdit: String
) {
    Row(
        modifier = Modifier.padding(top = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium,
            text = username,
        )

        IconButtonWithDialog(
            confirmButtonContent = { Text("Save") },
            dismissButtonContent = { Text("Cancel") },
            dialogContent = {
                OutlinedTextField(
                    label = { Text("Username") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    onValueChange = onChange,
                    value = usernameEdit
                )
            },
            dialogTitle = { Text("Editing username") },
            icon = Icons.Outlined.Edit,
            iconSize = 30.dp,
            iconDescription = "Edit Icon Button",
            modifier = Modifier.padding(
                start = 5.dp,
                top = 4.dp
            ),
            onConfirm = onSave,
            onDismiss = onDismiss
        )
    }
}

@Composable
fun WeeklyActivitiesItem(
    title: String,
    activities: List<String>
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
                        fontWeight = FontWeight.Bold,
                        text = title
                    )
                }
            }

            FiltersBox(filters = activities)
        }
    }
}