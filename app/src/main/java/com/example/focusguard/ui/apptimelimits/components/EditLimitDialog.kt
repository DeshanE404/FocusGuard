package com.example.focusguard.ui.apptimelimits.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.focusguard.data.AppLimit

@Composable
fun EditLimitDialog(
    appLimit: AppLimit,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var input by remember(appLimit) { mutableStateOf(appLimit.dailyLimitMinutes.toString()) }
    var error by remember { mutableStateOf<String?>(null) }
    val quickLimits = listOf(10, 15, 30, 45, 60, 90)

    fun save() {
        val minutes = input.toIntOrNull()
        error = if (minutes == null || minutes !in 1..720) {
            "Enter a whole number from 1 to 720 minutes"
        } else {
            onSave(minutes)
            null
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${appLimit.appName} limit") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Current limit: ${appLimit.dailyLimitMinutes} min")
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it.filter(Char::isDigit); error = null },
                    label = { Text("Minutes") },
                    singleLine = true,
                    isError = error != null,
                    supportingText = { error?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickLimits.take(3).forEach { minutes ->
                        FilterChip(
                            selected = input == minutes.toString(),
                            onClick = { input = minutes.toString(); error = null },
                            label = { Text("$minutes") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickLimits.drop(3).forEach { minutes ->
                        FilterChip(
                            selected = input == minutes.toString(),
                            onClick = { input = minutes.toString(); error = null },
                            label = { Text("$minutes") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = { Button(onClick = ::save) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
