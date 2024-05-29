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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.R
import com.pvp.app.model.Diet
import com.pvp.app.model.Ingredient
import com.pvp.app.model.SportActivity
import com.pvp.app.ui.common.ButtonConfirm
import com.pvp.app.ui.common.EditablePickerItem
import com.pvp.app.ui.common.Experience
import com.pvp.app.ui.common.IconButtonWithDialog
import com.pvp.app.ui.common.LocalHorizontalPagerSettled
import com.pvp.app.ui.common.LocalRouteOptionsApplier
import com.pvp.app.ui.common.LocalShowSnackbar
import com.pvp.app.ui.common.ProgressIndicatorWithinDialog
import com.pvp.app.ui.common.RouteTitle
import com.pvp.app.ui.common.TextError
import com.pvp.app.ui.common.darken
import com.pvp.app.ui.common.orInDarkTheme
import com.pvp.app.ui.router.Route
import kotlinx.coroutines.launch

@Composable
private fun AccountDeleteButton(
    viewModel: ProfileViewModel = hiltViewModel(),
    state: ProfileState
) {
    val username = state.user.username
    var input by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val localeError = stringResource(R.string.profile_button_delete_error)
    val localeErrorIncorrect = stringResource(R.string.profile_button_delete_error_incorrect)
    val localeSuccess = stringResource(R.string.profile_button_delete_success)
    val showSnackbar = LocalShowSnackbar.current

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
                        text = stringResource(R.string.profile_button_delete)
                    )
                }
            },
            confirmationButtonContent = { Text(stringResource(R.string.action_confirm)) },
            confirmationTitle = { Text(stringResource(R.string.profile_button_delete_confirm_title)) },
            confirmationDescription = {
                Column {
                    Text(stringResource(R.string.profile_button_delete_confirm_description))

                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        stringResource(
                            R.string.profile_button_delete_confirm_input_label,
                            username
                        )
                    )

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
                            showSnackbar(localeSuccess)
                        } else {
                            showSnackbar(localeError)
                        }
                    }
                } else {
                    input = ""

                    showSnackbar(localeErrorIncorrect)
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
            errorMessage = stringResource(
                R.string.input_field_username_error_length,
                lengthMin,
                lengthMax
            )
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

    if (state.isLoading) {
        ProgressIndicatorWithinDialog()
    }

    RouteOptionsApplier()

    Box(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .then(modifier)
    ) {
        Points(points = state.user.points)

        Column(modifier = Modifier.fillMaxWidth()) {
            if (!state.isLoading) {
                val showSnackbar = LocalShowSnackbar.current
                val localeSuccess = stringResource(R.string.input_field_username_success)

                Initials(
                    onUsernameChange = {
                        viewModel.update { u -> u.username = it }

                        showSnackbar(localeSuccess)
                    },
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
                onUpdateDiet = { viewModel.update { u -> u.diet = it } },
                onUpdateIngredients = { viewModel.update { u -> u.ingredients = it } },
                onUpdateHeight = { viewModel.update { u -> u.height = it } },
                onUpdateMass = { viewModel.update { u -> u.mass = it } },
                state = state
            )

            AccountDeleteButton(state = state)
        }
    }
}

@Composable
private fun Properties(
    model: ProfileViewModel = hiltViewModel(),
    onUpdateActivities: (List<SportActivity>) -> Unit,
    onUpdateDiet: (Diet) -> Unit,
    onUpdateHeight: (Int) -> Unit,
    onUpdateIngredients: (List<Ingredient>) -> Unit,
    onUpdateMass: (Int) -> Unit,
    state: ProfileState
) {
    val activities = SportActivity.entries.associateBy { it.title() }
    val context = LocalContext.current

    var activitiesSelected = remember(state.user.activities) {
        state.user.activities.map { context.getString(it.titleId) }
    }

    var activitiesSelectedEdit by remember(activitiesSelected) { mutableStateOf(activitiesSelected) }
    var activitiesUnselected by remember(activitiesSelected) { mutableStateOf(activities.keys.toList() - activitiesSelected.toSet()) }
    val height by remember(state.user.height) { mutableIntStateOf(state.user.height) }
    val ingredients = Ingredient.entries.associateBy { it.title() }

    var ingredientsSelected = remember(state.user.ingredients) {
        state.user.ingredients.map { context.getString(it.titleId) }
    }

    var ingredientsSelectedEdit by remember(ingredientsSelected) { mutableStateOf(ingredientsSelected) }
    var ingredientsUnselectedEdit by remember(ingredientsSelected) { mutableStateOf(ingredients.keys.toList() - ingredientsSelected.toSet()) }
    val mass by remember(state.user.mass) { mutableIntStateOf(state.user.mass) }
    val showSnackbar = LocalShowSnackbar.current

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
        val localeMeasurementKilograms = stringResource(R.string.measurement_kg)
        val localeSuccessMass = stringResource(R.string.input_field_mass_success)

        EditablePickerItem(
            editLabel = stringResource(R.string.input_field_mass_edit_label),
            items = model.rangeMass,
            itemsLabels = localeMeasurementKilograms,
            label = stringResource(R.string.input_field_mass_label),
            onValueChange = {
                onUpdateMass(it)

                showSnackbar(localeSuccessMass)
            },
            value = mass,
            valueLabel = localeMeasurementKilograms
        )

        val localeMeasurementCentimeters = stringResource(R.string.measurement_cm)
        val localeSuccessHeight = stringResource(R.string.input_field_height_success)

        EditablePickerItem(
            editLabel = stringResource(R.string.input_field_height_edit_label),
            items = model.rangeHeight,
            itemsLabels = localeMeasurementCentimeters,
            label = stringResource(R.string.input_field_height_label),
            onValueChange = {
                onUpdateHeight(it)

                showSnackbar(localeSuccessHeight)
            },
            value = height,
            valueLabel = localeMeasurementCentimeters
        )

        EditablePickerItem(
            editLabel = stringResource(R.string.input_field_diet_edit_label),
            label = stringResource(R.string.input_field_diet_label),
            value = state.user.diet,
            valueLabel = { it.title },
            items = Diet.entries,
            itemsLabel = { it.title },
            onValueChange = {
                onUpdateDiet(it)

                showSnackbar("Your diet has been updated")
            }
        )


        WeeklyActivitiesItem(
            title = stringResource(R.string.input_field_weekly_activities_label),
            activities = state.user.weeklyActivities.map { it.title() }
        )

        val localeFilterActivitiesSuccess = stringResource(
            R.string.input_field_filter_sport_activities_success
        )

        val localeFilterActivitiesOtherEmptyText = stringResource(
            R.string.input_field_filter_sport_activities_other_empty
        )

        val localeFilterActivitiesSelectedEmptyText = stringResource(
            R.string.input_field_filter_sport_activities_selected_empty
        )

        FiltersItem(
            dialogContent = {
                FiltersDialog(
                    onValueChange = { selected, unselected ->
                        activitiesSelectedEdit = selected
                        activitiesUnselected = unselected
                    },
                    selectedFilters = activitiesSelectedEdit,
                    unselectedFilters = activitiesUnselected,
                    textOtherEmpty = localeFilterActivitiesOtherEmptyText,
                    textSelectedEmpty = localeFilterActivitiesSelectedEmptyText,
                    titleOther = stringResource(R.string.input_field_filter_sport_activities_other_label),
                    titleSelected = stringResource(R.string.input_field_filter_sport_activities_selected_label)
                )
            },
            dialogTitle = { Text(stringResource(R.string.input_field_filter_sport_activities_edit_label)) },
            onConfirmClick = {
                activitiesSelected = activitiesSelectedEdit

                onUpdateActivities(activitiesSelectedEdit.mapNotNull { activities[it] })

                showSnackbar(localeFilterActivitiesSuccess)
            },
            onDismiss = {
                activitiesSelectedEdit = activitiesSelected
                activitiesUnselected = activities.keys.toList() - activitiesSelected.toSet()
            },
            selectedFilters = activitiesSelected,
            textOtherEmpty = localeFilterActivitiesOtherEmptyText,
            textSelectedEmpty = localeFilterActivitiesSelectedEmptyText,
            title = stringResource(R.string.input_field_filter_sport_activities_label)
        )

        val localeFilterIngredientsSuccess = stringResource(
            R.string.input_field_filter_ingredients_success
        )

        val localeFilterIngredientsOtherEmptyText = stringResource(
            R.string.input_field_filter_ingredients_other_empty
        )

        val localeFilterIngredientsSelectedEmptyText = stringResource(
            R.string.input_field_filter_ingredients_selected_empty
        )

        FiltersItem(
            dialogContent = {
                FiltersDialog(
                    onValueChange = { selected, unselected ->
                        ingredientsSelectedEdit = selected
                        ingredientsUnselectedEdit = unselected
                    },
                    selectedFilters = ingredientsSelectedEdit,
                    unselectedFilters = ingredientsUnselectedEdit,
                    textOtherEmpty = localeFilterIngredientsOtherEmptyText,
                    textSelectedEmpty = localeFilterIngredientsSelectedEmptyText,
                    titleOther = stringResource(R.string.input_field_filter_ingredients_other_label),
                    titleSelected = stringResource(R.string.input_field_filter_ingredients_selected_label)
                )
            },
            dialogTitle = { Text(stringResource(R.string.input_field_filter_ingredients_edit_label)) },
            onConfirmClick = {
                ingredientsSelected = ingredientsSelectedEdit

                onUpdateIngredients(ingredientsSelectedEdit.mapNotNull { ingredients[it] })

                showSnackbar(localeFilterIngredientsSuccess)
            },
            onDismiss = {
                ingredientsSelectedEdit = ingredientsSelected
                ingredientsUnselectedEdit = ingredients.keys.toList() - ingredientsSelected.toSet()
            },
            selectedFilters = ingredientsSelected,
            textOtherEmpty = localeFilterIngredientsOtherEmptyText,
            textSelectedEmpty = localeFilterIngredientsSelectedEmptyText,
            title = stringResource(R.string.input_field_filter_ingredients_label)
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
    errorMessage: String
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
            confirmButtonContent = { Text(stringResource(R.string.action_save)) },
            confirmButtonEnabled = validate(usernameEdit),
            dismissButtonContent = { Text(stringResource(R.string.action_cancel)) },
            dialogContent = {
                Column {
                    OutlinedTextField(
                        label = { Text(stringResource(R.string.input_field_username_label)) },
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
            dialogTitle = { Text(stringResource(R.string.input_field_username_edit_label)) },
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
                emptyBoxText = stringResource(R.string.input_field_weekly_activities_unassigned),
                cardsClickable = false
            )
        }
    }
}