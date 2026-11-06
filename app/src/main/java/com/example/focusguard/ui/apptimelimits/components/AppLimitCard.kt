package com.example.focusguard.ui.apptimelimits.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.focusguard.R
import com.example.focusguard.data.AppLimit
import com.example.focusguard.ui.theme.FocusGuardCard
import com.example.focusguard.ui.theme.FocusGuardPrimary

@Composable
fun AppLimitCard(
    appLimit: AppLimit,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressColor = when {
        appLimit.progress <= 0.1f -> Color(0xFFE53935)
        appLimit.progress <= 0.3f -> Color(0xFFFFB300)
        else -> Color(0xFF43A047)
    }
    val disabled = !appLimit.isEnabled
    val appIcon = when (appLimit.packageName) {
        "com.zhiliaoapp.musically" -> R.drawable.tiktok
        "com.facebook.katana" -> R.drawable.facebook
        "com.google.android.youtube" -> R.drawable.youtube
        "com.instagram.android" -> R.drawable.instagram
        else -> null
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = FocusGuardCard.copy(alpha = 0.42f)),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, FocusGuardPrimary.copy(alpha = 0.28f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                if (appIcon != null) {
                    Image(
                        painter = painterResource(appIcon),
                        contentDescription = "${appLimit.appName} icon",
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_menu_apps),
                        contentDescription = "${appLimit.appName} icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = appLimit.appName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = appLimit.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF43A047),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFE53935),
                        uncheckedBorderColor = Color(0xFFE53935)
                    )
                )
            }
            Text(
                text = if (disabled) "No limit" else "Limit: ${appLimit.dailyLimitMinutes} min",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (disabled) "Enable to limit this app" else "Remaining: ${appLimit.remainingMinutes} min",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LinearProgressIndicator(
                progress = { if (disabled) 0f else appLimit.progress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = if (disabled) Color.Gray else progressColor,
                trackColor = Color.Black.copy(alpha = 0.25f)
            )
        }
    }
}
