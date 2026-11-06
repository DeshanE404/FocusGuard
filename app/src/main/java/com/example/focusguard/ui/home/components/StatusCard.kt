package com.example.focusguard.ui.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.focusguard.ui.theme.FocusGuardCard
import com.example.focusguard.ui.theme.FocusGuardPrimary

@Composable
fun StatusCard(
    socialUsageMinutes: Int = 0,
    monitoredApps: Int = 0,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = FocusGuardCard.copy(alpha = 0.82f)
        ),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, FocusGuardPrimary.copy(alpha = 0.28f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Social media usage", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${socialUsageMinutes} min", style = MaterialTheme.typography.headlineSmall)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Monitored apps",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text("$monitoredApps active", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
