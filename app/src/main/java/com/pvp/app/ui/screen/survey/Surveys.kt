package com.pvp.app.ui.screen.survey

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Height
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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