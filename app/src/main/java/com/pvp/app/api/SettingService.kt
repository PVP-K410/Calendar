package com.pvp.app.api

import com.pvp.app.model.Setting
import kotlinx.coroutines.flow.Flow

interface SettingService {

    /**
     * Clear all settings from the local storage.
     */
    suspend fun clear()

    /**
     * Get the value of the setting. If the setting is not present in the local storage, the default
     * value is returned.
     */
    fun <T> get(
        setting: Setting<T>
    ): Flow<T>

    /**
     * Merge the value of the setting into the local storage. If the value is `null`, the setting is
     * removed from the local storage.
     */
    suspend fun <T> merge(
        setting: Setting<T>,
        value: T?
    )
}