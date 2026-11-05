package com.example.focusguard.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.focusguard.R
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.focusguard.ui.home.components.MenuCard
import com.example.focusguard.ui.home.components.StatusCard
import kotlinx.coroutines.launch

private data class HomeMenuItem(
    val title: String,
    val description: String,
    val icon: Int,
    val contentDescription: String
)

@Composable
fun HomeScreen(
    onAppTimeLimitsClick: () -> Unit = {},
    onSecurityClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val menuItems = remember {
        listOf(
            HomeMenuItem("Monitored Apps", "Choose apps to limit", R.drawable.ic_menu_apps, "Monitored apps"),
            HomeMenuItem("App Time Limits", "Set daily usage limits", R.drawable.ic_menu_time, "App time limits"),
            HomeMenuItem("PIN & Security", "Protect app settings", R.drawable.ic_menu_security, "PIN and security"),
            HomeMenuItem("Settings", "Notifications and preferences", R.drawable.ic_menu_settings, "Settings")
        )
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(PaddingValues(20.dp))
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("FocusGuard", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Protect your study time",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            StatusCard()
            menuItems.forEach { item ->
                MenuCard(
                    icon = item.icon,
                    iconDescription = item.contentDescription,
                    title = item.title,
                    description = item.description,
                    onClick = when (item.title) {
                        "App Time Limits" -> onAppTimeLimitsClick
                        "PIN & Security" -> onSecurityClick
                        "Settings" -> onSettingsClick
                        else -> { { scope.launch { snackbarHostState.showSnackbar("Coming soon") } } }
                    } as () -> Unit
                )
            }
        }
    }
}
