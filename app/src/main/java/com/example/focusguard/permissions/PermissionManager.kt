package com.example.focusguard.permissions

import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.os.Process

class PermissionManager(private val context: Context) {
    fun hasUsageAccess(): Boolean {
        val appOps = context.getSystemService(AppOpsManager::class.java)
        return appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName) == AppOpsManager.MODE_ALLOWED
    }

    fun hasAccessibilityService(): Boolean {
        val enabled = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES).orEmpty()
        val component = ComponentName(context, "${context.packageName}.monitoring.FocusGuardAccessibilityService").flattenToString()
        return enabled.split(':').any { it.equals(component, ignoreCase = true) }
    }

    fun hasOverlayPermission(): Boolean = Settings.canDrawOverlays(context)

    fun openUsageAccess() = context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    fun openAccessibilitySettings() = context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    fun openOverlaySettings() = context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}")))
}
