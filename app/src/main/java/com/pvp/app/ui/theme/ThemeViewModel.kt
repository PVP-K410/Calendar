package com.pvp.app.ui.theme

import androidx.lifecycle.ViewModel
import com.pvp.app.api.SettingService
import com.pvp.app.model.Setting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val settingService: SettingService
) : ViewModel() {

    fun getGeneralThemeSetting(): Flow<Int> {
        return settingService.get(Setting.Appearance.ApplicationTheme)
    }

    fun getDynamicThemeSetting(): Flow<Boolean> {
        return settingService.get(Setting.Appearance.DynamicThemeEnabled)
    }
}