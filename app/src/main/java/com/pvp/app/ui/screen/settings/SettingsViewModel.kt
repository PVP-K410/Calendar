package com.pvp.app.ui.screen.settings

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.Configuration
import com.pvp.app.api.SettingService
import com.pvp.app.api.TaskService
import com.pvp.app.model.Setting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val configuration: Configuration,
    private val settingService: SettingService,
    private val taskService: TaskService
) : ViewModel() {

    fun clear() {
        viewModelScope.launch(Dispatchers.IO) {
            settingService.clear()
        }
    }

    @Composable
    fun <T> rememberSetting(setting: Setting<T>): MutableState<T> {
        val coroutineScope = rememberCoroutineScope()

        val state = remember {
            settingService.get(setting)
        }
            .collectAsState(initial = setting.defaultValue)

        return remember {
            object : MutableState<T> {
                override var value: T
                    get() = state.value
                    set(value) {
                        coroutineScope.launch {
                            settingService.merge(
                                setting,
                                value
                            )
                        }
                    }

                override fun component1() = value
                override fun component2(): (T) -> Unit = { value = it }
            }
        }
    }

    fun <T> fromConfiguration(function: (Configuration) -> T): T {
        return function(configuration)
    }

    fun synchronizeGoogleTasks(
        onCallback: () -> Unit,
        onException: (Exception) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskService.synchronizeGoogleCalendar(LocalDate.now())

                withContext(Dispatchers.Main) {
                    onCallback()
                }
            } catch (e: Exception) {
                Log.e(
                    "SettingsViewModel",
                    "Google Calendar synchronization failed: ${e.message}"
                )

                withContext(Dispatchers.Main) {
                    onException(e)
                }
            }
        }
    }
}