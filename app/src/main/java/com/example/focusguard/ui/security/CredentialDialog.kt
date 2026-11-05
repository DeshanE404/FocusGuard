package com.example.focusguard.ui.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.focusguard.security.SecurityMode

@Composable
fun CredentialDialog(currentOnly: Boolean, initialMode: SecurityMode = SecurityMode.PIN, onDismiss: () -> Unit, onSubmit: (String, SecurityMode) -> Unit) {
    var mode by remember { mutableStateOf(initialMode) }
    var value by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var visible by remember { mutableStateOf(false) }
    val keyboard = if (mode == SecurityMode.PIN) KeyboardType.Number else KeyboardType.Password
    AlertDialog(onDismissRequest = onDismiss, title = { Text(if (currentOnly) "Enter current PIN or password" else "Set PIN or password") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = mode == SecurityMode.PIN, onClick = { mode = SecurityMode.PIN }, label = { Text("PIN") })
                FilterChip(selected = mode == SecurityMode.PASSWORD, onClick = { mode = SecurityMode.PASSWORD }, label = { Text("Password") })
            }
            OutlinedTextField(value, { value = it; error = null }, label = { Text(if (mode == SecurityMode.PIN) "PIN" else "Password") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = keyboard), visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton({ visible = !visible }) { Text(if (visible) "Hide" else "Show") } }, modifier = Modifier.fillMaxWidth())
            if (!currentOnly) OutlinedTextField(confirmation, { confirmation = it; error = null }, label = { Text("Confirm") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = keyboard), visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
            error?.let { Text(it) }
        }
    }, confirmButton = { Button(onClick = { error = when { !currentOnly && mode == SecurityMode.PIN && !value.matches(Regex("\\d{4,6}")) -> "PIN must contain only digits and be 4–6 digits long."; !currentOnly && mode == SecurityMode.PASSWORD && value.length < 8 -> "Password must be at least 8 characters."; !currentOnly && value != confirmation -> "Confirmation must match."; currentOnly && value.isEmpty() -> "Enter your current PIN or password."; else -> { onSubmit(value, mode); null } } }) { Text("Save") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}
