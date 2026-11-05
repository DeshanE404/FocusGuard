package com.example.focusguard.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

private val Context.appLimitDataStore by preferencesDataStore(name = "app_limits")

class AppLimitRepository(private val context: Context) {
    fun limits(defaults: List<AppLimit>): Flow<List<AppLimit>> = context.appLimitDataStore.data.map { preferences ->
        defaults.map { default ->
            default.copy(
                dailyLimitMinutes = preferences[intPreferencesKey("app_limit_minutes_${default.packageName}")]
                    ?: default.dailyLimitMinutes,
                isEnabled = preferences[booleanPreferencesKey("app_limit_enabled_${default.packageName}")]
                    ?: default.isEnabled
            )
        }
    }

    suspend fun current(defaults: List<AppLimit>): List<AppLimit> = limits(defaults).first()

    suspend fun setEnabled(packageName: String, enabled: Boolean) {
        context.appLimitDataStore.edit { preferences ->
            preferences[booleanPreferencesKey("app_limit_enabled_$packageName")] = enabled
        }
    }

    suspend fun setLimit(packageName: String, minutes: Int) {
        context.appLimitDataStore.edit { preferences ->
            preferences[intPreferencesKey("app_limit_minutes_$packageName")] = minutes
        }
    }
}
