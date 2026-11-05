package com.example.focusguard.ui.security

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.focusguard.security.SecurityMode
import com.example.focusguard.security.SecurityViewModel
import com.example.focusguard.ui.theme.FocusGuardBackground

@Composable
fun LockScreen(viewModel: SecurityViewModel = viewModel(), onUnlocked: () -> Unit) {
    val securityState by viewModel.state.collectAsState()
    val mode = if (securityState?.credentialType == "PASSWORD") SecurityMode.PASSWORD else SecurityMode.PIN
    var input by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var lockedUntil by remember { mutableLongStateOf(0L) }
    LaunchedEffect(lockedUntil) {
        if (lockedUntil > System.currentTimeMillis()) {
            delay(lockedUntil - System.currentTimeMillis())
            lockedUntil = 0L
        }
    }
    BackHandler(enabled = true) {}
    Column(Modifier.fillMaxSize().background(FocusGuardBackground).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("FocusGuard locked", style = MaterialTheme.typography.headlineMedium.copy(color = Color.White, fontWeight = FontWeight.SemiBold))
        Text("Enter your ${if (mode == SecurityMode.PASSWORD) "password" else "PIN"} to continue.", color = Color.White, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(input, { input = it; error = null }, label = { Text(if (mode == SecurityMode.PASSWORD) "Password" else "PIN", color = Color.White, fontWeight = FontWeight.SemiBold) }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = if (mode == SecurityMode.PASSWORD) KeyboardType.Password else KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Button(onClick = { viewModel.verify(input) { valid, until ->
            if (valid) onUnlocked() else { lockedUntil = until; error = if (until > System.currentTimeMillis()) "Too many attempts. Try again in 30 seconds." else "Incorrect PIN or password." }
        } }, enabled = input.isNotEmpty() && lockedUntil <= System.currentTimeMillis(), modifier = Modifier.fillMaxWidth()) { Text("Unlock", color = Color.White, fontWeight = FontWeight.SemiBold) }
        error?.let { Text(it, color = Color(0xFFFF8A80), fontWeight = FontWeight.SemiBold) }
    }
}
