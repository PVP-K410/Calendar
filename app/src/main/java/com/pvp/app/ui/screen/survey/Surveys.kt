package com.pvp.app.ui.screen.survey

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Height
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.common.LabelFieldWrapper
import com.pvp.app.ui.common.NumberPicker
import com.pvp.app.ui.common.rememberPickerState

private val massRange = (5..500).toList()
private val heightRange = (10..300).toList()

@Composable
fun BodyMassIndexSurvey(
    modifier: Modifier = Modifier,
    onSubmit: (mass: Int, height: Int) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        Column(
            modifier = modifier
        ) {
            val stateMass = rememberPickerState(massRange[0])
            val stateHeight = rememberPickerState(heightRange[0])

            LabelFieldWrapper(
                content = {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            style = MaterialTheme.typography.titleMedium,
                            text = "Select your mass"
                        )

                        Spacer(Modifier.padding(8.dp))

                        NumberPicker(
                            items = heightRange,
                            modifier = Modifier.fillMaxWidth(0.5f),
                            state = stateMass
                        )

                        Spacer(Modifier.padding(8.dp))

                        Icon(
                            imageVector = Icons.Outlined.Scale,
                            contentDescription = "Mass",
                        )
                    }
                },
                putBelow = true,
                text = "${stateMass.value} kg",
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.padding(8.dp))

            HorizontalDivider()

            Spacer(Modifier.padding(8.dp))

            LabelFieldWrapper(
                content = {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            style = MaterialTheme.typography.titleMedium,
                            text = "Select your height"
                        )

                        Spacer(Modifier.padding(8.dp))

                        NumberPicker(
                            items = heightRange,
                            modifier = Modifier.fillMaxWidth(0.5f),
                            state = stateHeight
                        )

                        Spacer(Modifier.padding(8.dp))

                        Icon(
                            imageVector = Icons.Outlined.Height,
                            contentDescription = "Height",
                        )
                    }
                },
                putBelow = true,
                text = "${stateHeight.value} cm (${stateHeight.value / 100.0} m)",
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.padding(8.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onSubmit(stateMass.value, stateHeight.value)
                }
            ) {
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    text = "Submit"
                )
            }
        }
    }
}