package com.pvp.app.ui.screen.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.model.Ingredient
import com.pvp.app.model.SportActivity
import com.pvp.app.ui.common.showToast

val activities = SportActivity.entries
    .map { it.title }

val ingredients = Ingredient.entries
    .map { it.title }

@Composable
fun FilterScreen(
    title: String,
    isActivities: Boolean,
    model: FilterViewModel = hiltViewModel()
) {
    var selectedFilters by remember { mutableStateOf(emptyList<String>()) }
    var unselectedFilters by remember { mutableStateOf(emptyList<String>()) }
    val user = model.user.collectAsStateWithLifecycle()

    LaunchedEffect(user.value) {
        if (isActivities) {
            selectedFilters = user.value?.activities?.map { it.title } ?: emptyList()
            unselectedFilters = activities - selectedFilters.toSet()
        } else {
            selectedFilters = user.value?.ingredients?.map { it.title } ?: emptyList()
            unselectedFilters = ingredients - selectedFilters.toSet()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        FiltersBox(
            title = "Selected $title",
            filters = selectedFilters,
            onClick = { filter ->
                selectedFilters = selectedFilters.minus(filter)
                unselectedFilters = unselectedFilters.plus(filter)
            }
        )

        FiltersBox(
            title = "Available $title",
            filters = unselectedFilters,
            onClick = { filter ->
                unselectedFilters = unselectedFilters.minus(filter)
                selectedFilters = selectedFilters.plus(filter)
            }
        )

        val context = LocalContext.current

        Button(
            onClick = {
                model.updateUserFilters(selectedFilters, isActivities)

                context.showToast(message = "Your $title have been updated!")

                /* TODO: redirect back to profile? page */
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Update",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun FiltersDialog(
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
        FiltersBox(
            title = "",
            filters = selectedFilters,
            onClick = { filter ->
                selectedFilters = selectedFilters.minus(filter)
                unselectedFilters = unselectedFilters.plus(filter)

                onValueChange(selectedFilters, unselectedFilters)
            }
        )

        FiltersBox(
            title = "",
            filters = unselectedFilters,
            onClick = { filter ->
                unselectedFilters = unselectedFilters.minus(filter)
                selectedFilters = selectedFilters.plus(filter)

                onValueChange(selectedFilters, unselectedFilters)
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltersBox(
    title: String? = null,
    filters: List<String>,
    onClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                filters
                    .sorted()
                    .forEach { filter ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.shapes.medium
                                ),
                            onClick = { onClick(filter) }
                        ) {
                            Text(
                                text = filter,
                                style = TextStyle(fontSize = 16.sp),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
            }
        }
    }
}

@Composable
fun ActivitiesFilter(
    model: FilterViewModel = hiltViewModel()
) {
    LaunchedEffect(model) {
        model.fetchUserData()
    }

    FilterScreen(
        title = "activities",
        isActivities = true,
        model = model
    )
}

@Composable
fun IngredientsFilter(
    model: FilterViewModel = hiltViewModel()
) {
    LaunchedEffect(model) {
        model.fetchUserData()
    }

    FilterScreen(
        title = "ingredients",
        isActivities = false,
        model = model
    )
}
