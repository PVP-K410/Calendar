package com.pvp.app.ui.screen.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import com.pvp.app.model.SportActivity

@Composable
fun ActivitiesFilter(
    model: FilterViewModel = hiltViewModel()
) {
    val activities: List<String> = SportActivity.values().map { it.title }
    var selectedActivities by remember { mutableStateOf(emptyList<String>()) }
    var unselectedActivities by remember { mutableStateOf(emptyList<String>()) }
    val user = model.user.collectAsStateWithLifecycle()

    LaunchedEffect(user.value) {
        selectedActivities = user.value?.activities ?: emptyList()
        unselectedActivities = activities - selectedActivities
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        ActivitiesBox(
            title = "Selected activities",
            activities = selectedActivities,
            onClick = { activity ->
                selectedActivities = selectedActivities.minus(activity)
                unselectedActivities = unselectedActivities.plus(activity)
            }
        )

        ActivitiesBox(
            title = "Available activities",
            activities = unselectedActivities,
            onClick = { activity ->
                unselectedActivities = unselectedActivities.minus(activity)
                selectedActivities = selectedActivities.plus(activity)
            }
        )

        Button(
            onClick = {
                model.updateUserActivities(selectedActivities)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActivitiesBox(
    title: String,
    activities: List<String>,
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
                activities
                    .sorted()
                    .forEach { activity ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .background(
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.shapes.medium
                            ),
                        onClick = { onClick(activity) }
                    ) {
                        Text(
                            text = activity,
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
