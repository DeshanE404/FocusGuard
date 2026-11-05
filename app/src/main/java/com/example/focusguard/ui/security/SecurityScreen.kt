package com.example.focusguard.ui.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.focusguard.security.SecurityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(onBack: () -> Unit, viewModel: SecurityViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val securityState = state ?: return
    var showSetup by remember { mutableStateOf(false) }
    var showVerify by remember { mutableStateOf(false) }
    var disableAfterVerify by remember { mutableStateOf(false) }
    Scaffold(topBar = { TopAppBar(title = { Text("PIN & Security") }, navigationIcon = { IconButton(onClick = onBack) { Text("‹", style = MaterialTheme.typography.headlineMedium) } }) }) { padding ->
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(PaddingValues(20.dp)).padding(padding), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Protection: ${if (securityState.enabled) "Enabled" else "Disabled"}", style = MaterialTheme.typography.titleLarge)
            Text("This PIN will be required every time you open FocusGuard.")
            if (securityState.enabled) {
                Button(onClick = { showVerify = true; disableAfterVerify = false }, modifier = Modifier.fillMaxWidth()) { Text("Change PIN or password") }
                OutlinedButton(onClick = { showVerify = true; disableAfterVerify = true }, modifier = Modifier.fillMaxWidth()) { Text("Disable PIN protection") }
            } else Button(onClick = { showSetup = true }, modifier = Modifier.fillMaxWidth()) { Text("Set up PIN or password") }
        }
    }
    if (showSetup) CredentialDialog(currentOnly = false, onDismiss = { showSetup = false }) { value, mode -> viewModel.saveCredential(value, mode.name) { showSetup = false } }
    if (showVerify) CredentialDialog(currentOnly = true, initialMode = if (securityState.credentialType == "PASSWORD") com.example.focusguard.security.SecurityMode.PASSWORD else com.example.focusguard.security.SecurityMode.PIN, onDismiss = { showVerify = false }) { value, _ -> viewModel.verify(value) { valid, _ -> if (valid) { showVerify = false; if (disableAfterVerify) viewModel.disable() else showSetup = true } } }
}
