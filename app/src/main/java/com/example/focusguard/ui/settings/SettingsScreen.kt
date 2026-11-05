package com.example.focusguard.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.focusguard.permissions.PermissionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, refreshKey: Int = 0) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val permissions = remember(context, refreshKey) { PermissionManager(context) }
    val usage = permissions.hasUsageAccess()
    val accessibility = permissions.hasAccessibilityService()
    val overlay = permissions.hasOverlayPermission()
    Scaffold(topBar = { TopAppBar(title = { Text("Settings") }, navigationIcon = { IconButton(onClick = onBack) { Text("‹", style = MaterialTheme.typography.headlineMedium) } }) }) { padding ->
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(PaddingValues(20.dp)).padding(padding), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Permissions", style = MaterialTheme.typography.headlineSmall)
            PermissionCard("Usage Access", "Usage Access is required to track social media time.", usage, "Grant Usage Access") { permissions.openUsageAccess() }
            PermissionCard("Accessibility Service", "Accessibility Service is required to detect and block apps.", accessibility, "Enable Accessibility Service") { permissions.openAccessibilitySettings() }
            PermissionCard("Overlay Permission", "Allow display over other apps to show blocking screens.", overlay, "Allow display over other apps") { permissions.openOverlaySettings() }
        }
    }
}

@Composable
private fun PermissionCard(title: String, description: String, granted: Boolean, action: String, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(if (granted) "Enabled" else description, color = if (granted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            if (!granted) Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(action) }
        }
    }
}
