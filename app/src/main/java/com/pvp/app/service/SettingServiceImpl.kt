package com.pvp.app.service

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.pvp.app.api.SettingService
import com.pvp.app.model.Setting
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
}