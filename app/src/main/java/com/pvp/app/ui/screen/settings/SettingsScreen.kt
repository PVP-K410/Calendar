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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Snowboarding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
        title = "Set Reminder Time",
        description = "Choose minutes before tasks reminder executes. Default is 10 minutes",
        iconDescription = "Clickable icon to edit reminder time",
        value = "$minutes minute${if (minutes != 1) "s" else ""}",
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
        }
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

    val isEnabled by model
        .get(Setting.Notifications.HydrationNotificationsEnabled)
        .collectAsStateWithLifecycle()

    val state = rememberPickerState(initialValue = volume)

    SettingCard(
        title = "Set Cup Volume",
        description = "Choose your cup volume for more accurate water drinking reminders. Default is 250 ml",
        iconDescription = "Clickable icon to edit cup volume",
        value = "$volume ml",
        isEnabled = isEnabled,
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
        }
    )
}

@Composable
private fun SettingHydrationNotificationToggle(
    model: SettingsViewModel = hiltViewModel()
) {
    val isEnabled by model
        .get(Setting.Notifications.HydrationNotificationsEnabled)
        .collectAsStateWithLifecycle()

    SettingCard(
        title = "Hydration Notifications",
        description = "Toggle water drinking reminder notifications",
        value = isEnabled,
        onEdit = {
            model.merge(
                Setting.Notifications.HydrationNotificationsEnabled,
                !isEnabled
            )
        }
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
                ) {
                    RadioButton(
                        onClick = {
                            state.value = 1
                        },
                        selected = state.value == 1,
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
                ) {
                    RadioButton(
                        onClick = {
                            state.value = 2
                        },
                        selected = state.value == 2,
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
                ) {
                    RadioButton(
                        onClick = {
                            state.value = 3
                        },
                        selected = state.value == 3,
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

        SettingHydrationNotificationToggle(model)

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
fun <T> SettingCard(
    description: String,
    editContent: @Composable () -> Unit = {},
    onEdit: () -> Unit,
    title: String,
    value: T,
    iconDescription: String? = null,
    isEnabled: Boolean = true
) {
    var textColor = MaterialTheme.colorScheme.onPrimary
    var backgroundColor = MaterialTheme.colorScheme.primary

    if (!isEnabled) {
        textColor = textColor.copy(alpha = 0.5f)
        backgroundColor = backgroundColor.copy(alpha = 0.5f)
    }

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
                color = backgroundColor,
                shape = MaterialTheme.shapes.medium
            )
            .padding(8.dp)
    ) {
        Text(
            color = textColor,
            fontSize = 18.sp,
            text = title
        )

        Spacer(modifier = Modifier.size(8.dp))


        Text(
            color = textColor,
            fontSize = 14.sp,
            text = description
        )

        Spacer(modifier = Modifier.size(16.dp))

        if (value is Boolean) {
            Row(
                modifier = Modifier.align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = value,
                    onCheckedChange = {
                        onEdit()
                    },
                    thumbContent = if (value) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    } else {
                        null
                    },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = MaterialTheme.colorScheme.surface,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceDim,
                        checkedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        checkedBorderColor = MaterialTheme.colorScheme.outline,
                        uncheckedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    enabled = isEnabled
                )
            }
        } else if (value != null) {
            var dialogOpen by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        if (isEnabled) {
                            dialogOpen = true
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium,
                    text = value.toString()
                )

                Spacer(modifier = Modifier.size(8.dp))

                Icon(
                    tint = textColor,
                    contentDescription = iconDescription,
                    imageVector = Icons.Outlined.Edit
                )
            }

            if (dialogOpen) {
                Dialog(
                    onDismissRequest = {
                        onEdit()

                        dialogOpen = false
                    }
                ) {
                    editContent()
                }
            }
        }
    }
}
