package com.pvp.app.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.Configuration
import com.pvp.app.api.SettingService
import com.pvp.app.model.Setting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val configuration: Configuration,
    private val settingService: SettingService
) : ViewModel() {

    fun <T> get(setting: Setting<T>): StateFlow<T> {
        return settingService
            .get(setting)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = setting.defaultValue
            )
    }

    fun <T> merge(
        setting: Setting<T>,
        value: T
    ) {
        viewModelScope.launch {
            settingService.merge(setting, value)
        }
    }

    fun <T> fromConfiguration(function: (Configuration) -> T): T {
        return function(configuration)
    }
}