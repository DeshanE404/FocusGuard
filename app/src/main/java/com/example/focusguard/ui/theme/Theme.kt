package com.example.focusguard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = FocusGuardPrimary,
    onPrimary = Color(0xFF003735),
    secondary = FocusGuardCard,
    onSecondary = FocusGuardOnCard,
    background = FocusGuardBackground,
    onBackground = FocusGuardOnBackground,
    surface = FocusGuardBackground,
    onSurface = FocusGuardOnBackground,
    surfaceVariant = FocusGuardCard,
    onSurfaceVariant = FocusGuardMuted
)

@Composable
fun FocusGuardTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}