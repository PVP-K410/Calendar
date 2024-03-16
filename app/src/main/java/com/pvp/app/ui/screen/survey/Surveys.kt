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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.pvp.app.ui.common.Picker
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
    val stateHeight = rememberPickerState(heightRange[0])
    val stateMass = rememberPickerState(massRange[0])

    LaunchedEffect(
        handler,
        stateHeight.value,
        stateMass.value
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
                textSelect = "Select your height"
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

                Picker(
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

private val activities = SportActivity.entries.map { it.title }
private val ingredients = Ingredient.entries.map { it.title }

@Composable
fun FilterSurvey(
    filters: List<String>,
    handler: (filters: List<String>) -> Unit,
    isActivities: Boolean,
    modifier: Modifier = Modifier,
    title: String
) {
    var filtersSelected by remember { mutableStateOf(filters) }
    var filtersUnselected by remember { mutableStateOf(emptyList<String>()) }

    filtersUnselected = if (isActivities) {
        activities - filtersSelected.toSet()
    } else {
        ingredients - filtersSelected.toSet()
    }

    LaunchedEffect(
        handler,
        filtersSelected,
        filtersUnselected
    ) {
        handler(filtersSelected)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        FiltersBox(
            filters = filtersSelected,
            onClick = { filter ->
                filtersSelected = filtersSelected.minus(filter)
                filtersUnselected = filtersUnselected.plus(filter)
            },
            title = "Selected $title"
        )

        FiltersBox(
            filters = filtersUnselected,
            onClick = { filter ->
                filtersUnselected = filtersUnselected.minus(filter)
                filtersSelected = filtersSelected.plus(filter)
            },
            title = "Available $title"
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltersBox(
    filters: List<String>,
    onClick: (String) -> Unit,
    title: String
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
                style = TextStyle(fontSize = 18.sp),
                modifier = Modifier.padding(8.dp),
                text = title
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                filters
                    .sorted()
                    .forEach {
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.shapes.medium
                                ),
                            onClick = { onClick(it) }
                        ) {
                            Text(
                                modifier = Modifier.padding(16.dp),
                                style = TextStyle(fontSize = 16.sp),
                                text = it
                            )
                        }
                    }
            }
        }
    }
}
