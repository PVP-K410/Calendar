package com.pvp.app.ui.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.Configuration
import com.pvp.app.api.SettingService
import com.pvp.app.model.Setting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val configuration: Configuration,
    private val settingService: SettingService
) : ViewModel() {

    fun clear() {
        viewModelScope.launch(Dispatchers.IO) {
            settingService.clear()
        }
    }

    @Composable
    fun <T> rememberSetting(
        setting: Setting<T>
    ): MutableState<T> {
        return settingService.remember(setting)
    }

    fun <T> fromConfiguration(function: (Configuration) -> T): T {
        return function(configuration)
    }
}