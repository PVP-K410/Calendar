@file:OptIn(ExperimentalLayoutApi::class)

package com.pvp.app.ui.screen.survey

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Height
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.R
import com.pvp.app.model.Ingredient
import com.pvp.app.model.SportActivity
import com.pvp.app.ui.common.LabelFieldWrapper
import com.pvp.app.ui.common.Picker
import com.pvp.app.ui.common.PickerState
import com.pvp.app.ui.common.PickerState.Companion.rememberPickerState

@Composable
fun BodyMassIndexSurvey(
    handler: (height: Int, mass: Int) -> Unit,
    model: SurveyViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val rangeHeight = remember { model.fromConfiguration { it.rangeHeight } }
    val rangeMass = remember { model.fromConfiguration { it.rangeMass } }
    val stateHeight = rememberPickerState(rangeHeight[0])
    val stateMass = rememberPickerState(rangeMass[0])

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
            val localeMeasurementKilograms = stringResource(R.string.measurement_kg)

            BodyMassIndexPicker(
                contentDescription = "Mass",
                imageVector = Icons.Outlined.Scale,
                state = stateMass,
                textResult = { "$it $localeMeasurementKilograms" },
                textSelect = stringResource(R.string.surveys_select_mass),
                values = rangeMass
            )

            Spacer(modifier = Modifier.padding(16.dp))

            val localeMeasurementCentimeters = stringResource(R.string.measurement_cm)
            val localeMeasurementMeters = stringResource(R.string.measurement_m)

            BodyMassIndexPicker(
                contentDescription = "Height",
                imageVector = Icons.Outlined.Height,
                state = stateHeight,
                textResult = { "$it $localeMeasurementCentimeters (${it / 100.0} $localeMeasurementMeters)" },
                textSelect = stringResource(R.string.surveys_select_height),
                values = rangeHeight
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
    values: List<Int>
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
                    items = values,
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

@Composable
fun FilterSurvey(
    filters: List<String>,
    handler: (filters: List<String>) -> Unit,
    isActivities: Boolean,
    modifier: Modifier = Modifier,
    textOtherEmpty: String,
    textSelectedEmpty: String,
    title: String,
    titleOther: String,
    titleSelected: String
) {
    var filtersSelected by remember { mutableStateOf(filters) }
    var filtersUnselected by remember { mutableStateOf(emptyList<String>()) }

    filtersUnselected = if (isActivities) {
        SportActivity.entries.map { it.title() } - filtersSelected.toSet()
    } else {
        Ingredient.entries.map { it.title() } - filtersSelected.toSet()
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
        Text(
            color = Color.Black,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
            text = title
        )

        FiltersBox(
            boxTitle = titleSelected,
            filters = filtersSelected,
            isSelected = true,
            onClick = { filter ->
                filtersSelected = filtersSelected.minus(filter)
                filtersUnselected = filtersUnselected.plus(filter)
            },
            textOtherEmpty = textOtherEmpty,
            textSelectedEmpty = textSelectedEmpty
        )

        FiltersBox(
            boxTitle = titleOther,
            filters = filtersUnselected,
            isSelected = false,
            onClick = { filter ->
                filtersUnselected = filtersUnselected.minus(filter)
                filtersSelected = filtersSelected.plus(filter)
            },
            textOtherEmpty = textOtherEmpty,
            textSelectedEmpty = textSelectedEmpty
        )
    }
}

@Composable
private fun FiltersBox(
    boxTitle: String,
    filters: List<String>,
    isSelected: Boolean,
    onClick: (String) -> Unit,
    textOtherEmpty: String,
    textSelectedEmpty: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.large
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 18.sp),
                text = boxTitle
            )

            if (filters.isEmpty()) {
                Text(
                    text = if (!isSelected) textOtherEmpty else textSelectedEmpty,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontStyle = FontStyle.Italic
                    ),
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
                    .forEach {
                        FilterBoxCard(
                            entry = it,
                            isSelected = isSelected,
                            onClick = onClick
                        )
                    }
            }
        }
    }
}

@Composable
private fun FilterBoxCard(
    entry: String,
    isSelected: Boolean,
    onClick: (String) -> Unit
) {
    Box(
        modifier = Modifier.padding(
            end = 4.dp,
            bottom = 4.dp
        )
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            ),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.secondary
            ),
            onClick = { onClick(entry) }
        ) {
            Text(
                modifier = Modifier.padding(
                    top = 12.dp,
                    bottom = 12.dp,
                    start = 6.dp,
                    end = 6.dp
                ),
                style = TextStyle(fontSize = 15.sp),
                text = entry
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    end = 0.dp,
                    top = 0.dp
                )
                .clip(CircleShape)
                .background(Color.White)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = CircleShape
                )
                .size(16.dp)
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Outlined.Remove else Icons.Outlined.Add,
                contentDescription = null,
                tint = Color.Black
            )
        }
    }
}