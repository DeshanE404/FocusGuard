package com.example.focusguard.ui.apptimelimits

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusguard.data.AppLimit
import com.example.focusguard.data.AppLimitRepository
import com.example.focusguard.data.defaultAppLimits
import com.example.focusguard.monitoring.UsageStatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppTimeLimitsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppLimitRepository(application)
    private val usageRepository = UsageStatsRepository(application)
    private val usedMinutes = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val configuredLimits = repository.limits(defaultAppLimits)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), defaultAppLimits)

    val appLimits: StateFlow<List<AppLimit>> = combine(configuredLimits, usedMinutes) { limits, usage ->
        limits.map { it.copy(usedMinutesToday = usage[it.packageName] ?: 0) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), defaultAppLimits)

    fun refresh() {
        viewModelScope.launch {
            usedMinutes.value = usageRepository.usedMinutesToday(configuredLimits.value)
        }
    }

    fun setEnabled(app: AppLimit, enabled: Boolean) {
        viewModelScope.launch { repository.setEnabled(app.packageName, enabled) }
    }

    fun setLimit(app: AppLimit, minutes: Int) {
        viewModelScope.launch { repository.setLimit(app.packageName, minutes) }
    }
}
