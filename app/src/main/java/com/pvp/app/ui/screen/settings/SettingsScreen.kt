package com.pvp.app.ui.screen.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.health.connect.HealthConnectManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.PermIdentity
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.startActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.pvp.app.model.Setting
import com.pvp.app.ui.common.ButtonConfirm
import com.pvp.app.ui.common.Picker
import com.pvp.app.ui.common.PickerState.Companion.rememberPickerState
import com.pvp.app.ui.common.showToast

@Composable
private fun SettingNotificationReminderMinutes(
    model: SettingsViewModel = hiltViewModel()
) {
    val range = remember { model.fromConfiguration { it.rangeReminderMinutes } }
    var minutes by model.rememberSetting(Setting.Notifications.ReminderBeforeTaskMinutes)
    val state = rememberPickerState(initialValue = minutes)

    SettingCard(
        title = "Reminder Time",
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
                    startIndex = minutes / 5,
                    state = state
                )
            }
        },
        onEdit = { minutes = state.value }
    )
}

@Composable
private fun SettingCupVolumeMl(
    model: SettingsViewModel = hiltViewModel()
) {
    val range = remember { model.fromConfiguration { it.rangeCupVolume } }
    var volume by model.rememberSetting(Setting.Notifications.CupVolumeMl)
    val isEnabled by model.rememberSetting(Setting.Notifications.HydrationNotificationsEnabled)
    val state = rememberPickerState(initialValue = volume)

    SettingCard(
        title = "Cup Volume",
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
        onEdit = { volume = state.value }
    )
}

@Composable
private fun SettingHydrationNotificationToggle(
    model: SettingsViewModel = hiltViewModel()
) {
    var isEnabled by model.rememberSetting(Setting.Notifications.HydrationNotificationsEnabled)

    SettingCard(
        title = "Hydration Notifications",
        description = "Toggle water drinking reminder notifications",
        value = isEnabled,
        onEdit = { isEnabled = !isEnabled }
    )
}

enum class Theme {
    Dark,
    Light,
    Auto
}

@Composable
private fun SettingDynamicTheme(
    model: SettingsViewModel = hiltViewModel()
) {
    var isEnabled by model.rememberSetting(Setting.Appearance.DynamicThemeEnabled)

    SettingCard(
        title = "Dynamic Theme",
        description = "Choose whether or not you want to use the dynamic theme",
        value = isEnabled,
        onEdit = { isEnabled = !isEnabled }
    )
}

@Composable
private fun SettingApplicationTheme(
    model: SettingsViewModel = hiltViewModel()
) {
    var themeValue by model.rememberSetting(Setting.Appearance.ApplicationTheme)

    SettingCard(
        description = "Choose the theme of the application",
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

                Theme.entries.forEach {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = themeValue == it.ordinal,
                            onClick = { themeValue = it.ordinal }
                        )

                        Text(
                            text = it.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }
            }
        },
        onEdit = {},
        value = Theme.entries[themeValue],
        title = "Application Theme"
    )
}

@Composable
private fun SettingHealthConnectPermissions(context: Context) {
    SettingCard(
        title = "Health Connect",
        description = "By enabling certain Health Connect permissions you are" +
                " allowing us to provide you more great features. You can disable" +
                " those permissions anytime by coming here.",
        value = "Configure",
        onEdit = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                permissionsPostUpsideDownCake(context)
            } else {
                permissionsPreUpsideDownCake(context)
            }
        },
        icon = Icons.Outlined.Settings
    )
}

@Composable
private fun GoogleCalendarSynchronizer(model: SettingsViewModel = hiltViewModel()) {
    var intent by remember { mutableStateOf<Intent?>(null) }
    val context = LocalContext.current

    fun synchronize() {
        model.synchronizeGoogleTasks(onCallback = {
            context.showToast(message = "Google Calendar successfully synchronized")
        }) { e ->
            intent = when (e) {
                is UserRecoverableAuthIOException -> e.intent
                else -> null
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            synchronize()
        }
    }

    LaunchedEffect(intent) {
        if (intent != null) {
            launcher.launch(intent!!)

            intent = null
        }
    }

    SettingCard(
        title = "Google Calendar",
        description = "Synchronize your Google Calendar with our app to find all your tasks " +
                "in one place. Google tasks from the past will not be synchronized, only today's " +
                "and future tasks will be synchronized.",
        value = "Synchronize",
        onEdit = ::synchronize,
        icon = Icons.Outlined.Sync
    )
}

@Composable
fun SettingsScreen(modifier: Modifier) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .then(modifier)
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        CategoryRow(
            icon = Icons.Outlined.Notifications,
            title = "Notifications"
        )

        SettingNotificationReminderMinutes()

        SettingHydrationNotificationToggle()

        SettingCupVolumeMl()

        CategoryRow(
            icon = Icons.Outlined.Style,
            title = "Appearance"
        )

        SettingApplicationTheme()

        SettingDynamicTheme()

        CategoryRow(
            icon = Icons.Outlined.PermIdentity,
            title = "3rd Party Services"
        )

        GoogleCalendarSynchronizer()

        SettingHealthConnectPermissions(LocalContext.current)

        ResetToDefaultButton()
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
            tint = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = " $title",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ResetToDefaultButton(
    model: SettingsViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ButtonConfirm(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(
                    top = 30.dp,
                    bottom = 20.dp
                ),
            border = BorderStroke(
                1.dp,
                Color.Red
            ),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
            contentAlignment = Alignment.BottomCenter,
            shape = MaterialTheme.shapes.extraLarge,
            content = {
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    text = "Reset to Default"
                )
            },
            confirmationButtonContent = { Text(text = "Reset to Default") },
            confirmationTitle = { Text(text = "Are you sure you want to reset all of your settings to default?") },
            onConfirm = { model.clear() }
        )
    }
}

@Composable
fun <T> SettingCard(
    description: String,
    editContent: (@Composable () -> Unit)? = null,
    onEdit: () -> Unit,
    title: String,
    value: T,
    icon: ImageVector = Icons.Outlined.Edit,
    iconDescription: String? = null,
    isEnabled: Boolean = true
) {
    var textColor = MaterialTheme.colorScheme.onSurface
    var backgroundColor = MaterialTheme.colorScheme.surfaceContainer

    if (!isEnabled) {
        textColor = textColor.copy(alpha = 0.5f)
        backgroundColor = backgroundColor.copy(alpha = 0.5f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
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
                                tint = MaterialTheme.colorScheme.surface
                            )
                        }
                    } else {
                        null
                    },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = MaterialTheme.colorScheme.surface,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceDim,
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        checkedBorderColor = MaterialTheme.colorScheme.primary,
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
                        if (isEnabled && editContent != null) {
                            dialogOpen = true
                        } else {
                            onEdit()
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    text = value.toString()
                )

                Spacer(modifier = Modifier.size(8.dp))

                Icon(
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = iconDescription,
                    imageVector = icon
                )
            }

            if (dialogOpen && editContent != null) {
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

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
private fun permissionsPostUpsideDownCake(context: Context) {
    // On some phones even on Android 14 (API Level 14)
    // Intent of ACTION_MANAGE_HEALTH_PERMISSIONS causes an exception
    try {
        startActivity(
            context,
            Intent(HealthConnectManager.ACTION_MANAGE_HEALTH_PERMISSIONS)
                .putExtra(
                    Intent.EXTRA_PACKAGE_NAME,
                    context.packageName
                ),
            null
        )
    } catch (e: Exception) {
        permissionsPreUpsideDownCake(context)
    }
}

private fun permissionsPreUpsideDownCake(context: Context) {
    startActivity(
        context,
        Intent(HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS),
        null
    )
}