package com.example.focusguard.ui.apptimelimits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.focusguard.data.AppLimit
import com.example.focusguard.ui.apptimelimits.components.AppLimitCard
import com.example.focusguard.ui.apptimelimits.components.EditLimitDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTimeLimitsScreen(
    onBack: () -> Unit,
    viewModel: AppTimeLimitsViewModel = viewModel()
) {
    val appLimits by viewModel.appLimits.collectAsState()
    LaunchedEffect(Unit) { viewModel.refresh() }
    var editingApp by remember { mutableStateOf<AppLimit?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Time Limits") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("‹", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(PaddingValues(16.dp))
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "Set daily limits for social-media apps.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            appLimits.forEach { app ->
                AppLimitCard(
                    appLimit = app,
                    onToggle = { viewModel.setEnabled(app, it) },
                    onClick = { editingApp = app }
                )
            }
        }
    }

    editingApp?.let { app ->
        EditLimitDialog(
            appLimit = app,
            onDismiss = { editingApp = null },
            onSave = { minutes ->
                viewModel.setLimit(app, minutes)
                editingApp = null
            }
        )
    }
}
