package com.voicejournal.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6C63FF),
    secondary = Color(0xFFFF6584),
    tertiary = Color(0xFF4ECDC4),
    background = Color(0xFF0A0E27),
    surface = Color(0xFF1A1F3A),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFE8E8E8),
    onSurface = Color(0xFFE8E8E8)
)

@Composable
fun VoiceJournalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
