package com.terminal.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppDarkColorScheme = darkColorScheme(
    primary = AccentPrimary,
    onPrimary = Color.White,
    primaryContainer = AccentSecondary,
    onPrimaryContainer = Color.White,
    secondary = GlassTint,
    background = BgPrimary,
    onBackground = TextPrimary,
    surface = BgElevated,
    onSurface = TextPrimary,
    surfaceVariant = BgSecondary,
    onSurfaceVariant = TextSecondary,
    error = Danger,
    onError = Color.White
)

@Composable
fun TerminalAppTheme(
    useDark: Boolean = true,
    content: @Composable () -> Unit
) {
    // Force dark by default - the UI is designed dark (iOS-27 style).
    val dark = useDark || isSystemInDarkTheme()
    val cs = if (dark) AppDarkColorScheme else AppDarkColorScheme
    MaterialTheme(
        colorScheme = cs,
        typography = AppTypography,
        content = content
    )
}
