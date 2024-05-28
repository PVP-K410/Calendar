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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.R
import com.pvp.app.model.Ingredient
import com.pvp.app.model.SportActivity
import com.pvp.app.ui.common.ButtonConfirm
import com.pvp.app.ui.common.CenteredSnackbarHost
import com.pvp.app.ui.common.EditablePickerItem
import com.pvp.app.ui.common.Experience
import com.pvp.app.ui.common.IconButtonWithDialog
import com.pvp.app.ui.common.LocalHorizontalPagerSettled
import com.pvp.app.ui.common.LocalRouteOptionsApplier
import com.pvp.app.ui.common.ProgressIndicatorWithinDialog
import com.pvp.app.ui.common.RouteTitle
import com.pvp.app.ui.common.TextError
import com.pvp.app.ui.common.darken
import com.pvp.app.ui.common.orInDarkTheme
import com.pvp.app.ui.router.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val ACTIVITIES = SportActivity.entries.map { it.title }
private val INGREDIENTS = Ingredient.entries.map { it.title }

@Composable
private fun AccountDeleteButton(
    viewModel: ProfileViewModel = hiltViewModel(),
    state: ProfileState,
    snackbarHostState: SnackbarHostState
) {
    val username = state.user.username
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
                    modifier = Modifier.padding(horizontal = 8.dp),
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
                            snackbarHostState.showSnackbar("Account deleted successfully")
                        } else {
                            snackbarHostState.showSnackbar("An error occurred while deleting the account")
                        }
                    }
                } else {
                    input = ""

                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Incorrect username")
                    }
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
    val email by remember { mutableStateOf(state.user.email) }
    var userName by remember { mutableStateOf(state.user.username) }
    val lengthMin = model.intervalUsernameLength.first
    val lengthMax = model.intervalUsernameLength.second

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = 15.dp,
                end = 30.dp,
                start = 30.dp,
                top = 15.dp
            ),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest
                        .darken(0.1f)
                        .orInDarkTheme(MaterialTheme.colorScheme.surfaceContainerHighest),
                    shape = CircleShape
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
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
        }

        Username(
            onSave = {
                userName = it

                onUsernameChange(it.trim())
            },
            username = userName,
            validate = { it.length in lengthMin..lengthMax },
            errorMessage = "Username must be between $lengthMin and $lengthMax characters long"
        )

        Text(
            style = MaterialTheme.typography.bodyMedium,
            text = email
        )
    }
}

@Composable
private fun BoxScope.Points(points: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(
                end = 16.dp,
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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    if (state.isLoading) {
        ProgressIndicatorWithinDialog()
    }

    RouteOptionsApplier()

    Scaffold(
        snackbarHost = { CenteredSnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .then(modifier)
        ) {
            Points(points = state.user.points)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
            ) {
                if (!state.isLoading) {
                    Initials(
                        onUsernameChange = { viewModel.update { u -> u.username = it } },
                        state = state
                    )

                    Experience(
                        experience = state.user.experience,
                        experienceRequired = state.experienceRequired,
                        level = state.user.level,
                        paddingStart = 16.dp,
                        paddingEnd = 16.dp
                    )
                } else {
                    Spacer(modifier = Modifier.height(180.dp))
                }

                Properties(
                    onUpdateActivities = { viewModel.update { u -> u.activities = it } },
                    onUpdateIngredients = { viewModel.update { u -> u.ingredients = it } },
                    onUpdateHeight = { viewModel.update { u -> u.height = it } },
                    onUpdateMass = { viewModel.update { u -> u.mass = it } },
                    state = state,
                    snackbarHostState = snackbarHostState,
                    coroutineScope = scope
                )

                AccountDeleteButton(
                    state = state,
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}

@Composable
private fun Properties(
    model: ProfileViewModel = hiltViewModel(),
    onUpdateActivities: (List<SportActivity>) -> Unit,
    onUpdateHeight: (Int) -> Unit,
    onUpdateIngredients: (List<Ingredient>) -> Unit,
    onUpdateMass: (Int) -> Unit,
    state: ProfileState,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    var activitiesSelected = remember(state.user.activities) {
        state.user.activities.map { it.title }
    }

    var activitiesSelectedEdit by remember(activitiesSelected) { mutableStateOf(activitiesSelected) }
    var activitiesUnselected by remember(activitiesSelected) { mutableStateOf(ACTIVITIES - activitiesSelected.toSet()) }
    val height by remember(state.user.height) { mutableIntStateOf(state.user.height) }

    var ingredientsSelected = remember(state.user.ingredients) {
        state.user.ingredients.map { it.title }
    }

    var ingredientsSelectedEdit by remember(ingredientsSelected) { mutableStateOf(ingredientsSelected) }
    var ingredientsUnselectedEdit by remember(ingredientsSelected) { mutableStateOf(INGREDIENTS - ingredientsSelected.toSet()) }
    val mass by remember(state.user.mass) { mutableIntStateOf(state.user.mass) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                end = 16.dp,
                start = 16.dp,
                top = 16.dp
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EditablePickerItem(
            label = "Mass",
            value = mass,
            valueLabel = "kg",
            items = model.rangeMass,
            itemsLabels = "kg",
            onValueChange = {
                onUpdateMass(it)

                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Your mass has been updated")
                }
            },
        )

        EditablePickerItem(
            label = "Height",
            value = height,
            valueLabel = "cm",
            items = model.rangeHeight,
            itemsLabels = "cm",
            onValueChange = {
                onUpdateHeight(it)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Your height has been updated")
                }
            },
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

                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Your sport activities have been updated")
                }
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

                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Your ingredients have been updated")
                }
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
private fun RouteOptionsApplier() {
    val settled = LocalHorizontalPagerSettled.current
    var applierRequired by remember(settled) { mutableStateOf(settled) }

    if (applierRequired) {
        LocalRouteOptionsApplier.current {
            Route.Options(title = {
                RouteTitle(stringResource(R.string.route_profile))
            })
        }

        applierRequired = false
    }
}

@Composable
private fun Username(
    onSave: (String) -> Unit,
    username: String,
    validate: (String) -> Boolean = { false },
    errorMessage: String = "Invalid input"
) {
    var usernameEdit by remember { mutableStateOf(username) }

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
            confirmButtonEnabled = validate(usernameEdit),
            dismissButtonContent = { Text("Cancel") },
            dialogContent = {
                Column {
                    OutlinedTextField(
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = { usernameEdit = it },
                        value = usernameEdit
                    )

                    TextError(
                        enabled = !validate(usernameEdit),
                        text = errorMessage
                    )
                }
            },
            dialogTitle = { Text("Editing username") },
            icon = Icons.Outlined.Edit,
            iconSize = 30.dp,
            iconDescription = "Edit Icon Button",
            modifier = Modifier.padding(
                start = 5.dp,
                top = 4.dp
            ),
            onConfirm = {
                if (validate(usernameEdit)) {
                    onSave(usernameEdit)
                }
            },
            onDismiss = {
                usernameEdit = username
            }
        )
    }
}

@Composable
fun WeeklyActivitiesItem(
    activities: List<String>,
    title: String
) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
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
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
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

            FiltersBox(
                filters = activities,
                emptyBoxText = "No weekly activities have been assigned yet",
                cardsClickable = false
            )
        }
    }
}