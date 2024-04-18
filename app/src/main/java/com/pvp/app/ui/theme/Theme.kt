package com.pvp.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import com.pvp.app.api.SettingService
import com.pvp.app.model.Setting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    tertiary = DarkTertiary
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    tertiary = LightTertiary
)

@Composable
fun CalendarTheme(
    model: ThemeViewModel,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val themeValue = model.getGeneralThemeSetting().collectAsState(initial = 1).value
    val dynamicColor = model.getDynamicThemeSetting().collectAsState(initial = false).value
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (themeValue == 0) {
                dynamicDarkColorScheme(context)
            } else if (themeValue == 1) {
                dynamicLightColorScheme(context)
            } else {
                if (darkTheme) dynamicDarkColorScheme(context)
                else dynamicLightColorScheme(context)
            }
        }

        themeValue == 0 -> DarkColorScheme
        themeValue == 1 -> LightColorScheme
        else -> {
            if (darkTheme) DarkColorScheme
            else LightColorScheme
        }
    }

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            window.navigationBarColor = colorScheme.surfaceContainer.toArgb()
            window.statusBarColor = colorScheme.surfaceContainer.toArgb()

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}