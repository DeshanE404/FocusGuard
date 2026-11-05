package com.example.focusguard.security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun rememberSecurityInputState() = remember {
    SecurityInputState()
}

class SecurityInputState {
    var mode by mutableStateOf(SecurityMode.PIN)
    var value by mutableStateOf("")
    var confirmation by mutableStateOf("")
    var error by mutableStateOf<String?>(null)

    val keyboardType: KeyboardType
        get() = if (mode == SecurityMode.PIN) KeyboardType.Number else KeyboardType.Password
}

enum class SecurityMode { PIN, PASSWORD }
