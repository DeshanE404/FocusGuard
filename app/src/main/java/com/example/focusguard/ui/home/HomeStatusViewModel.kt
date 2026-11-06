package com.example.focusguard.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusguard.data.AppLimitRepository
import com.example.focusguard.data.defaultAppLimits
import com.example.focusguard.monitoring.UsageStatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class HomeStatus(
    val socialUsageMinutes: Int = 0,
    val monitoredApps: Int = 0
)

class HomeStatusViewModel(application: Application) : AndroidViewModel(application) {
    private val limits = AppLimitRepository(application)
    private val usage = UsageStatsRepository(application)
    private val _status = MutableStateFlow(HomeStatus())
    val status: StateFlow<HomeStatus> = _status

    fun refresh() {
        viewModelScope.launch {
            val apps = limits.limits(defaultAppLimits).first()
            val used = usage.usedMinutesToday(apps)
            _status.value = HomeStatus(
                socialUsageMinutes = used.values.sum(),
                monitoredApps = apps.count { it.isEnabled }
            )
        }
    }
}
