package com.example.focusguard.data

data class AppLimit(
    val packageName: String,
    val appName: String,
    val dailyLimitMinutes: Int,
    val isEnabled: Boolean,
    val usedMinutesToday: Int = 0
) {
    val remainingMinutes: Int
        get() = (dailyLimitMinutes - usedMinutesToday).coerceAtLeast(0)

    val progress: Float
        get() = if (dailyLimitMinutes > 0) {
            (remainingMinutes.toFloat() / dailyLimitMinutes).coerceIn(0f, 1f)
        } else {
            0f
        }
}
