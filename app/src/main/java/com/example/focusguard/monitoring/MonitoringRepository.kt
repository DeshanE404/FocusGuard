package com.example.focusguard.monitoring

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.monitoringDataStore by preferencesDataStore(name = "monitoring")

class MonitoringRepository(private val context: Context) {
    suspend fun unlockUntil(packageName: String, timestamp: Long) {
        context.monitoringDataStore.edit { it[longPreferencesKey("unlock_until_$packageName")] = timestamp }
    }

    suspend fun isUnlocked(packageName: String, now: Long = System.currentTimeMillis()): Boolean {
        val expiry = context.monitoringDataStore.data.first()[longPreferencesKey("unlock_until_$packageName")] ?: 0L
        if (expiry > 0 && expiry <= now) unlockUntil(packageName, 0)
        return expiry > now
    }
}

