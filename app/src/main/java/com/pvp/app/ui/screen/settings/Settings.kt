package com.pvp.app.ui.screen.settings

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.pvp.app.ui.common.Picker
import com.pvp.app.ui.common.PickerState.Companion.rememberPickerState

var reminderTimeMinutes by mutableIntStateOf(10)

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CategoryRow(
            icon = Icons.Outlined.Badge,
            title = "General"
        )

        //SettingCard(title = "Placeholder")
        //SettingCard(title = "Placeholder")
        //SettingCard(title = "Placeholder")

        CategoryRow(
            icon = Icons.Outlined.Notifications,
            title = "Notifications"
        )

        SettingCard(
            title = "Set Reminder time",
            value = reminderTimeMinutes,
            editContent = { value ->
                val pickerState = rememberPickerState(initialValue = value)
                Picker(
                    items = (1..60).toList(),
                    state = pickerState,
                    startIndex = reminderTimeMinutes - 1,
                )
                reminderTimeMinutes = pickerState.value ?: reminderTimeMinutes
            },
            description = "Choose how long before the task would you like to be reminded"
        )

        //SettingCard(title = "Placeholder")
        //SettingCard(title = "Placeholder")
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
    title: String? = null,
    value: T? = null,
    editContent: @Composable (T?) -> Unit = { _ -> },
    description: String? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.shapes.medium
            ),
    ) {
        Text(
            text = " $title",
            fontSize = 18.sp
        )

        if (description != null) {
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        editContent(value)
    }
}
