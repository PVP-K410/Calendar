package com.pvp.app.ui.screen.survey

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Height
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pvp.app.model.Ingredient
import com.pvp.app.model.SportActivity
import com.pvp.app.ui.common.LabelFieldWrapper
import com.pvp.app.ui.common.NumberPicker
import com.pvp.app.ui.common.PickerState
import com.pvp.app.ui.common.PickerState.Companion.rememberPickerState

private val massRange = (5..500).toList()
private val heightRange = (10..300).toList()

@Composable
@SuppressLint("ComposableNaming")
fun BodyMassIndexSurvey(
    handler: (height: Int, mass: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val stateMass = rememberPickerState(massRange[0])
    val stateHeight = rememberPickerState(heightRange[0])

    LaunchedEffect(
        handler,
        stateMass.value,
        stateHeight.value
    ) {
        handler(
            stateHeight.value,
            stateMass.value
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            BodyMassIndexPicker(
                contentDescription = "Mass",
                imageVector = Icons.Outlined.Scale,
                state = stateMass,
                textResult = { "$it kg" },
                textSelect = "Select your mass"
            )

            Spacer(modifier = Modifier.padding(16.dp))

            BodyMassIndexPicker(
                contentDescription = "Height",
                imageVector = Icons.Outlined.Height,
                state = stateHeight,
                textResult = { "$it cm (${it / 100.0} m)" },
                textSelect = "Select your height",
            )
        }
    }
}

@Composable
private fun BodyMassIndexPicker(
    contentDescription: String,
    imageVector: ImageVector,
    state: PickerState<Int>,
    textResult: (Int) -> String,
    textSelect: String,
) {
    LabelFieldWrapper(
        content = {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = textSelect
                )

                Spacer(Modifier.padding(8.dp))

                NumberPicker(
                    items = heightRange,
                    modifier = Modifier.fillMaxWidth(0.5f),
                    state = state
                )

                Spacer(Modifier.padding(8.dp))

                Icon(
                    imageVector = imageVector,
                    contentDescription = contentDescription,
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.shapes.medium
            )
            .padding(4.dp),
        putBelow = true,
        text = textResult(state.value),
        textAlign = TextAlign.Center
    )
}

private val activities = SportActivity
    .values()
    .map { it.title }

private val ingredients = Ingredient
    .values()
    .map { it.title }

@Composable
fun FilterActivitiesSurvey(
    handler: (filters: List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilters by remember { mutableStateOf(emptyList<String>()) }
    var unselectedFilters by remember { mutableStateOf(emptyList<String>()) }

    unselectedFilters = activities - selectedFilters


    LaunchedEffect(
        handler,
        selectedFilters,
        unselectedFilters
    ) {
        handler(
            selectedFilters
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        FiltersBox(
            title = "Selected activities",
            filters = selectedFilters,
            onClick = { filter ->
                selectedFilters = selectedFilters.minus(filter)
                unselectedFilters = unselectedFilters.plus(filter)
            }
        )

        FiltersBox(
            title = "Available activities",
            filters = unselectedFilters,
            onClick = { filter ->
                unselectedFilters = unselectedFilters.minus(filter)
                selectedFilters = selectedFilters.plus(filter)
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltersBox(
    title: String,
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
            Text(
                text = title,
                style = TextStyle(fontSize = 18.sp),
                modifier = Modifier.padding(8.dp)
            )

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
