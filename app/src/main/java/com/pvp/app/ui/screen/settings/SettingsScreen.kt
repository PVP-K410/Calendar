package com.pvp.app.ui.screen.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.health.connect.HealthConnectManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.LocaleListCompat
import androidx.health.connect.client.HealthConnectClient
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.pvp.app.R
import com.pvp.app.common.CollectionUtil.indexOfOrNull
import com.pvp.app.model.Setting
import com.pvp.app.model.Setting.Appearance.Theme
import com.pvp.app.ui.common.ButtonConfirm
import com.pvp.app.ui.common.DropdownMenu
import com.pvp.app.ui.common.LocalShowSnackbar
import com.pvp.app.ui.common.Picker
import com.pvp.app.ui.common.PickerState.Companion.rememberPickerState

@Composable
private fun SettingNotificationReminderMinutes(model: SettingsViewModel = hiltViewModel()) {
    val range = remember { model.fromConfiguration { it.rangeReminderMinutes } }
    var minutes by model.rememberSetting(Setting.Notifications.ReminderBeforeTaskMinutes)
    val state = rememberPickerState(initialValue = minutes)
    val localeTitle = stringResource(R.string.settings_setting_reminder_title)

    SettingCard(
        description = stringResource(
            R.string.settings_setting_reminder_description,
            Setting.Notifications.ReminderBeforeTaskMinutes.defaultValue
        ),
        editContent = {
            Column(
                modifier = Modifier
                    .clip(shape = MaterialTheme.shapes.medium)
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .wrapContentSize()
                    .padding(32.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = localeTitle
                )

                Picker(
                    items = range,
                    label = {
                        stringResource(
                            R.string.settings_setting_reminder_button,
                            it
                        )
                    },
                    modifier = Modifier.padding(top = 16.dp),
                    startIndex = minutes / 5,
                    state = state
                )
            }
        },
        onEdit = { minutes = state.value },
        title = localeTitle,
        value = stringResource(
            R.string.settings_setting_reminder_button,
            minutes
        )
    )
}

@Composable
private fun SettingCupVolumeMl(model: SettingsViewModel = hiltViewModel()) {
    val range = remember { model.fromConfiguration { it.rangeCupVolume } }
    var volume by model.rememberSetting(Setting.Notifications.CupVolumeMl)
    val isEnabled by model.rememberSetting(Setting.Notifications.HydrationNotificationsEnabled)
    val state = rememberPickerState(initialValue = volume)
    val localeTitle = stringResource(R.string.settings_setting_cup_volume_title)

    SettingCard(
        description = stringResource(
            R.string.settings_setting_cup_volume_description,
            Setting.Notifications.CupVolumeMl.defaultValue
        ),
        editContent = {
            Column(
                modifier = Modifier
                    .clip(shape = MaterialTheme.shapes.medium)
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .wrapContentSize()
                    .padding(32.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = localeTitle
                )

                Picker(
                    items = range,
                    label = {
                        stringResource(
                            R.string.settings_setting_cup_volume_button,
                            it
                        )
                    },
                    modifier = Modifier.padding(top = 16.dp),
                    startIndex = range.indexOf(volume),
                    state = state
                )
            }
        },
        isEnabled = isEnabled,
        onEdit = { volume = state.value },
        title = localeTitle,
        value = stringResource(
            R.string.settings_setting_cup_volume_button,
            volume
        )
    )
}

@Composable
private fun SettingHydrationNotificationToggle(model: SettingsViewModel = hiltViewModel()) {
    var isEnabled by model.rememberSetting(Setting.Notifications.HydrationNotificationsEnabled)

    SettingCard(
        description = stringResource(R.string.settings_setting_hydration_reminder_description),
        onEdit = { isEnabled = !isEnabled },
        title = stringResource(R.string.settings_setting_hydration_reminder_title),
        value = isEnabled
    )
}

@Composable
private fun SettingDynamicTheme(model: SettingsViewModel = hiltViewModel()) {
    var isEnabled by model.rememberSetting(Setting.Appearance.DynamicThemeEnabled)

    SettingCard(
        description = stringResource(R.string.settings_setting_theme_dynamic_description),
        onEdit = { isEnabled = !isEnabled },
        title = stringResource(R.string.settings_setting_theme_dynamic_title),
        value = isEnabled
    )
}

@Composable
private fun SettingApplicationTheme(model: SettingsViewModel = hiltViewModel()) {
    val localeTitle = stringResource(R.string.settings_setting_theme_title)
    var themeValue by model.rememberSetting(Setting.Appearance.ApplicationTheme)

    val themes = mapOf(
        Theme.Auto to stringResource(R.string.settings_setting_theme_label_auto),
        Theme.Dark to stringResource(R.string.settings_setting_theme_label_dark),
        Theme.Light to stringResource(R.string.settings_setting_theme_label_light)
    )

    SettingCard(
        description = stringResource(R.string.settings_setting_theme_description),
        editContent = {
            Column(
                modifier = Modifier
                    .clip(shape = MaterialTheme.shapes.medium)
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .wrapContentSize()
                    .padding(32.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = localeTitle
                )

                Spacer(modifier = Modifier.size(16.dp))
                themes.forEach { (theme, label) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = themeValue == theme.ordinal,
                            onClick = { themeValue = theme.ordinal }
                        )

                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }

                }
            }
        },
        onEdit = {},
        value = themes[Theme.entries[themeValue]],
        title = localeTitle
    )
}

@Composable
private fun SettingHealthConnectPermissions(context: Context) {
    SettingCard(
        description = stringResource(R.string.settings_setting_health_connect_description),
        icon = Icons.Outlined.Settings,
        onEdit = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                permissionsPostUpsideDownCake(context)
            } else {
                permissionsPreUpsideDownCake(context)
            }
        },
        title = stringResource(R.string.settings_setting_health_connect_title),
        value = stringResource(R.string.settings_setting_health_connect_button_configure)
    )
}

@Composable
fun SettingLanguage() {
    val localeTitle = stringResource(R.string.settings_setting_language_title)

    val options = mapOf(
        R.string.settings_locale_en to "",
        R.string.settings_locale_lt to "lt"
    )
        .mapKeys { stringResource(it.key) }

    var value = remember {
        val tag = AppCompatDelegate
            .getApplicationLocales()[0]
            ?.toLanguageTag() ?: ""

        options.entries.find {
            it.value.equals(
                tag,
                ignoreCase = true
            )
        }?.key ?: options.keys.first()
    }

    SettingCard(
        description = stringResource(R.string.settings_setting_language_description),
        editContent = {
            Column(
                modifier = Modifier
                    .clip(shape = MaterialTheme.shapes.medium)
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .wrapContentSize()
                    .padding(32.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = localeTitle
                )

                Spacer(modifier = Modifier.size(16.dp))

                DropdownMenu(
                    onSelect = { option ->
                        value = option

                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(options[option])
                        )
                    },
                    options = options.keys.toList(),
                    value = value
                )
            }
        },
        icon = Icons.Outlined.Edit,
        onEdit = { },
        title = localeTitle,
        value = value,
    )
}

@Composable
private fun GoogleCalendarAutoSynchronizer(model: SettingsViewModel = hiltViewModel()) {
    val localeNever = stringResource(R.string.settings_setting_google_calendar_auto_value_never)
    val localeTitle = stringResource(R.string.settings_setting_google_calendar_auto_title)
    val localeValue = stringResource(R.string.settings_setting_google_calendar_auto_value)
    var value by model.rememberSetting(setting = Setting.ThirdPartyServices.GoogleCalendarSyncInterval)
    val state = rememberPickerState(initialValue = value)

    val range = listOf(
        -1,
        6,
        12,
        24
    )

    SettingCard(
        description = stringResource(R.string.settings_setting_google_calendar_auto_description),
        editContent = {
            Column(
                modifier = Modifier
                    .clip(shape = MaterialTheme.shapes.medium)
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .wrapContentSize()
                    .padding(32.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = localeTitle
                )

                Picker(
                    items = range,
                    label = {
                        when (it) {
                            -1 -> localeNever
                            else -> localeValue.format(it)
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp),
                    startIndex = range.indexOfOrNull(value) ?: 0,
                    state = state
                )
            }
        },
        icon = Icons.Outlined.Sync,
        onEdit = { value = state.value },
        title = localeTitle,
        value = when (value) {
            -1 -> localeNever
            else -> localeValue.format(value)
        }
    )
}

@Composable
private fun GoogleCalendarSynchronizer(model: SettingsViewModel = hiltViewModel()) {
    val localeSuccess = stringResource(
        R.string.settings_setting_google_calendar_synchronize_success
    )

    var intent by remember { mutableStateOf<Intent?>(null) }
    val showSnackbar = LocalShowSnackbar.current

    fun synchronize() {
        model.synchronizeGoogleTasks(onCallback = {
            showSnackbar(localeSuccess)
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
        title = stringResource(R.string.settings_setting_google_calendar_title),
        description = stringResource(R.string.settings_setting_google_calendar_description),
        value = stringResource(R.string.settings_setting_google_calendar_button_synchronize),
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
            icon = Icons.Outlined.Style,
            title = stringResource(R.string.settings_category_appearance_title)
        )

        SettingLanguage()

        SettingApplicationTheme()

        SettingDynamicTheme()

        CategoryRow(
            icon = Icons.Outlined.Notifications,
            title = stringResource(R.string.settings_category_notifications_title)
        )

        SettingNotificationReminderMinutes()

        SettingHydrationNotificationToggle()

        SettingCupVolumeMl()

        CategoryRow(
            icon = Icons.Outlined.PermIdentity,
            title = stringResource(R.string.settings_category_3rd_party_services_title)
        )

        GoogleCalendarAutoSynchronizer()

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
fun ResetToDefaultButton(model: SettingsViewModel = hiltViewModel()) {
    val localeLabel = stringResource(R.string.settings_button_reset)

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
                    textAlign = TextAlign.Center,
                    text = localeLabel
                )
            },
            confirmationButtonContent = { Text(text = localeLabel) },
            confirmationTitle = { Text(text = stringResource(R.string.settings_button_reset_description)) },
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