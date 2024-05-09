package com.pvp.app.service

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.pvp.app.api.SettingService
import com.pvp.app.model.Setting
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

val Context.dataStoreSettings: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingServiceImpl @Inject constructor(
    @ApplicationContext
    private val context: Context
) : SettingService {

    override suspend fun clear() {
        context.dataStoreSettings.edit { it.clear() }
    }

    override fun <T> get(
        setting: Setting<T>
    ): Flow<T> {
        return context.dataStoreSettings.data.map { it[setting.key] ?: setting.defaultValue }
    }

    override suspend fun <T> merge(
        setting: Setting<T>,
        value: T?
    ) {
        if (value == null) {
            context.dataStoreSettings.edit { it.remove(setting.key) }

            return
        }

        context.dataStoreSettings.edit { it[setting.key] = value }
    }

    @Composable
    override fun <T> remember(
        setting: Setting<T>
    ): MutableState<T> {
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val state = androidx.compose.runtime.remember {
            context.dataStoreSettings.data.map {
                it[setting.key] ?: setting.defaultValue
            }
        }
            .collectAsState(initial = setting.defaultValue)

        return androidx.compose.runtime.remember {
            object : MutableState<T> {
                override var value: T
                    get() = state.value
                    set(value) {
                        coroutineScope.launch {
                            Log.e("SettingServiceImpl", "Setting value: $value")
                            context.dataStoreSettings.edit {
                                it[setting.key] = value
                            }
                        }
                    }

                override fun component1() = value
                override fun component2(): (T) -> Unit = { value = it }
            }
        }
    }
}