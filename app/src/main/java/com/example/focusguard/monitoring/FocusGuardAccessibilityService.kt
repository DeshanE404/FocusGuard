package com.example.focusguard.monitoring

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.example.focusguard.blocking.BlockActivity
import com.example.focusguard.data.AppLimitRepository
import com.example.focusguard.data.defaultAppLimits
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FocusGuardAccessibilityService : AccessibilityService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var limits: AppLimitRepository
    private lateinit var usage: UsageStatsRepository
    private lateinit var unlocks: MonitoringRepository
    private var lastCheckedPackage = ""
    private var lastCheckedAt = 0L
    private var activePackage = ""
    private var activeSince = 0L
    private var activeJob: Job? = null
    private var blockedPackage = ""

    override fun onServiceConnected() {
        super.onServiceConnected()
        limits = AppLimitRepository(applicationContext)
        usage = UsageStatsRepository(applicationContext)
        unlocks = MonitoringRepository(applicationContext)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val packageName = event.packageName?.toString() ?: return
        if (activePackage.isNotEmpty() && packageName != activePackage) {
            activeJob?.cancel()
            activeJob = null
            activePackage = ""
        }
        if (packageName !in defaultAppLimits.map { it.packageName }) return
        if (activePackage != packageName) {
            activePackage = packageName
            activeSince = System.currentTimeMillis()
            activeJob = scope.launch { monitorActiveApp(packageName) }
        }
        val now = System.currentTimeMillis()
        if (packageName == lastCheckedPackage && now - lastCheckedAt < 15_000) return
        lastCheckedPackage = packageName
        lastCheckedAt = now
        scope.launch {
            val app = limits.current(defaultAppLimits).firstOrNull { it.packageName == packageName } ?: return@launch
            if (!app.isEnabled || unlocks.isUnlocked(packageName)) return@launch
            val used = usage.usedMinutesToday(listOf(app))[packageName] ?: 0
            if (used >= app.dailyLimitMinutes && blockedPackage != packageName) {
                blockedPackage = packageName
                startActivity(Intent(this@FocusGuardAccessibilityService, BlockActivity::class.java).apply {
                    putExtra(BlockActivity.EXTRA_PACKAGE_NAME, packageName)
                    putExtra(BlockActivity.EXTRA_APP_NAME, app.appName)
                    putExtra(BlockActivity.EXTRA_USED_MINUTES, used)
                    putExtra(BlockActivity.EXTRA_LIMIT_MINUTES, app.dailyLimitMinutes)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                })
            }
        }
    }

    private suspend fun monitorActiveApp(packageName: String) {
        while (activePackage == packageName) {
            val app = limits.current(defaultAppLimits).firstOrNull { it.packageName == packageName }
            if (unlocks.isUnlocked(packageName)) {
                blockedPackage = ""
                delay(5_000)
                continue
            }
            if (app != null && app.isEnabled) {
                val usedFromStats = usage.usedMinutesToday(listOf(app))[packageName] ?: 0
                val currentSessionMinutes = ((System.currentTimeMillis() - activeSince) / 60_000L).toInt()
                val used = maxOf(usedFromStats, currentSessionMinutes)
                if (used >= app.dailyLimitMinutes && blockedPackage != packageName) {
                    blockedPackage = packageName
                    startActivity(Intent(this, BlockActivity::class.java).apply {
                        putExtra(BlockActivity.EXTRA_PACKAGE_NAME, packageName)
                        putExtra(BlockActivity.EXTRA_APP_NAME, app.appName)
                        putExtra(BlockActivity.EXTRA_USED_MINUTES, used)
                        putExtra(BlockActivity.EXTRA_LIMIT_MINUTES, app.dailyLimitMinutes)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    })
                    break
                }
            }
            delay(5_000)
        }
    }

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
