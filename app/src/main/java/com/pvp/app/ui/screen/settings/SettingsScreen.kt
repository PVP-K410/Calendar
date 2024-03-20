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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val minutes by model
        .get(Setting.Notifications.ReminderBeforeTaskMinutes)
        .collectAsStateWithLifecycle()

    val state = rememberPickerState(initialValue = minutes)

    SettingCard(
        description = "Choose minutes before tasks reminder executes. Default is 10 minutes",
        editContent = {
            Picker(
                items = (1..120).toList(),
                state = state,
                startIndex = minutes - 1,
            )
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
        value = "$minutes minute(s)"
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
