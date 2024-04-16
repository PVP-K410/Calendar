package com.pvp.app.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Snowboarding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.model.Setting
import com.pvp.app.ui.common.Button
import com.pvp.app.ui.common.Picker
import com.pvp.app.ui.common.PickerState.Companion.rememberPickerState

@Composable
private fun SettingNotificationReminderMinutes(
    model: SettingsViewModel = hiltViewModel()
) {
    val range = remember { model.fromConfiguration { it.rangeReminderMinutes } }

    val minutes by model
        .get(Setting.Notifications.ReminderBeforeTaskMinutes)
        .collectAsStateWithLifecycle()

    val state = rememberPickerState(initialValue = minutes)

    SettingCard(
        description = "Choose minutes before tasks reminder executes. Default is 10 minutes",
        editContent = {
            Column(
                modifier = Modifier
                    .clip(shape = MaterialTheme.shapes.small)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = MaterialTheme.shapes.small
                    )
                    .wrapContentSize()
                    .padding(32.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = "Select Reminder Minutes"
                )

                Picker(
                    items = range,
                    label = { "$it minutes" },
                    modifier = Modifier.padding(top = 16.dp),
                    startIndex = minutes - 1,
                    state = state
                )
            }
        },
        onEdit = {
            if (state.value != minutes) {
                model.merge(
                    Setting.Notifications.ReminderBeforeTaskMinutes,
                    state.value
                )
            }
        },
        title = "Set Reminder Time",
        value = "$minutes minute${if (minutes != 1) "s" else ""}"
    )
}

@Composable
private fun SettingCupVolumeMl(
    model: SettingsViewModel = hiltViewModel()
) {
    val range = remember { model.fromConfiguration { it.rangeCupVolume } }

    val volume by model
        .get(Setting.Notifications.CupVolumeMl)
        .collectAsStateWithLifecycle()

    val state = rememberPickerState(initialValue = volume)

    SettingCard(
        description = "Choose your cup volume for more accurate water drinking reminders. Default is 250 ml",
        editContent = {
            Column(
                modifier = Modifier
                    .clip(shape = MaterialTheme.shapes.small)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = MaterialTheme.shapes.small
                    )
                    .wrapContentSize()
                    .padding(32.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = "Select Cup Volume"
                )

                Picker(
                    items = range,
                    label = { "$it ml" },
                    modifier = Modifier.padding(top = 16.dp),
                    startIndex = range.indexOf(volume),
                    state = state
                )
            }
        },
        onEdit = {
            if (state.value != volume) {
                model.merge(
                    Setting.Notifications.CupVolumeMl,
                    state.value
                )
            }
        },
        title = "Set Cup Volume",
        value = "$volume ml"
    )
}

@Composable
private fun SettingApplicationTheme(
    model: SettingsViewModel = hiltViewModel()
) {
    val themeValue by model
        .get(Setting.ApplicationTheme)
        .collectAsStateWithLifecycle()

    val state = rememberPickerState(initialValue = themeValue)

    val valueText = when (themeValue) {
        1 -> "Dark"
        2 -> "Light"
        3 -> "Dynamic"
        else -> themeValue.toString()
    }

    SettingCard(
        description = "Choose the theme of the application. Default is the 'Dynamic Theme'.",
        editContent = {
            Column(
                modifier = Modifier
                    .clip(shape = MaterialTheme.shapes.small)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = MaterialTheme.shapes.small
                    )
                    .wrapContentSize()
                    .padding(32.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = "Select Application Theme"
                )

                Spacer(modifier = Modifier.size(16.dp))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .selectable(
                            selected = themeValue == 1,
                            onClick = {
                                state.value = 1
                            }
                        )
                ) {
                    RadioButton(
                        selected = state.value == 1,
                        onClick = {
                            state.value = 1
                        }
                    )

                    Text(
                        text = "Dark",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.size(16.dp))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .selectable(
                            selected = themeValue == 2,
                            onClick = {
                                state.value = 2
                            }
                        )
                ) {
                    RadioButton(
                        selected = state.value == 2,
                        onClick = {
                            state.value = 2
                        }
                    )

                    Text(
                        text = "Light",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.size(16.dp))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .selectable(
                            selected = themeValue == 3,
                            onClick = {
                                state.value = 3
                            }
                        )
                ) {
                    RadioButton(
                        selected = state.value == 3,
                        onClick = {
                            state.value = 3
                        }
                    )

                    Text(
                        text = "Dynamic",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        },
        onEdit = {
            if (state.value != themeValue) {
                model.merge(
                    Setting.ApplicationTheme,
                    state.value
                )
            }
        },
        value = valueText,
        title = "Set Application Theme"
    )
}

@Composable
fun SettingsScreen(
    model: SettingsViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CategoryRow(
            icon = Icons.Outlined.Notifications,
            title = "Notifications"
        )

        SettingNotificationReminderMinutes(model)

        SettingCupVolumeMl(model)

        CategoryRow(
            icon = Icons.Outlined.Snowboarding,
            title = "Theme"
        )

        SettingApplicationTheme(model)
    }
}

@Composable
fun CategoryRow(
    icon: ImageVector,
    title: String,
) {
    Spacer(modifier = Modifier.size(24.dp))

    Row {
        Icon(
            imageVector = icon,
            contentDescription = title,
        )

        Text(
            text = " $title",
            style = MaterialTheme.typography.titleLarge,
        )
    }

    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.tertiary
    )
}

@Composable
fun SettingCard(
    description: String,
    editContent: @Composable () -> Unit,
    onEdit: () -> Unit,
    title: String? = null,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium,
                width = 1.dp
            )
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium
            )
            .padding(8.dp)
    ) {
        if (title != null) {
            Text(
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 18.sp,
                text = title
            )

            Spacer(modifier = Modifier.size(8.dp))
        }

        Text(
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 14.sp,
            text = description
        )

        Spacer(modifier = Modifier.size(16.dp))

        var state by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .align(Alignment.End)
                .clickable { state = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyMedium,
                text = value
            )

            Spacer(modifier = Modifier.size(8.dp))

            Icon(
                tint = MaterialTheme.colorScheme.onPrimary,
                contentDescription = "Open dialog to edit a setting",
                imageVector = Icons.Outlined.Edit
            )
        }

        if (state) {
            Dialog(
                onDismissRequest = {
                    onEdit()

                    state = false
                }
            ) {
                editContent()
            }
        }
    }
}
