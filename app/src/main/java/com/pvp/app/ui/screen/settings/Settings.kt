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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

        SettingCard(title = "Placeholder")
        SettingCard(title = "Placeholder")
        SettingCard(title = "Placeholder")

        CategoryRow(
            icon = Icons.Outlined.Notifications,
            title = "Notifications"
        )

        SettingCard(title = "Placeholder")
        SettingCard(title = "Placeholder")
        SettingCard(title = "Placeholder")
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

    Divider(
        color = MaterialTheme.colorScheme.tertiary,
        thickness = 1.dp,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SettingCard(
    title: String,
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
    }
}
