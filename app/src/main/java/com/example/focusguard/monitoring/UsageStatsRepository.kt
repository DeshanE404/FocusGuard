package com.example.focusguard.monitoring

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import com.example.focusguard.data.AppLimit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class UsageStatsRepository(context: Context) {
    private val usageStats = context.getSystemService(UsageStatsManager::class.java)

    suspend fun usedMinutesToday(apps: List<AppLimit>): Map<String, Int> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val start = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val tracked = apps.map { it.packageName }.toSet()
        val totals = tracked.associateWith { 0L }.toMutableMap()
        val foreground = mutableMapOf<String, Long>()
        val events = usageStats.queryEvents(start, now)
        val event = UsageEvents.Event()

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            val packageName = event.packageName ?: continue
            if (packageName !in tracked) continue
            when (event.eventType) {
                UsageEvents.Event.MOVE_TO_FOREGROUND -> foreground.putIfAbsent(packageName, event.timeStamp)
                UsageEvents.Event.MOVE_TO_BACKGROUND -> foreground.remove(packageName)?.let { started ->
                    totals[packageName] = totals.getValue(packageName) + (event.timeStamp - started).coerceAtLeast(0)
                }
            }
        }
        foreground.forEach { (packageName, started) ->
            totals[packageName] = totals.getValue(packageName) + (now - started).coerceAtLeast(0)
        }
        totals.mapValues { (_, millis) -> (millis / 60_000L).toInt() }
    }
}

