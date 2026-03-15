package com.meng.flasher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light colors - biru Material
private val LightColorScheme = lightColorScheme(
    primary          = Color(0xFF1565C0),
    onPrimary        = Color.White,
    primaryContainer = Color(0xFFD6E4FF),
    secondary        = Color(0xFF1E88E5),
    onSecondary      = Color.White,
    background       = Color(0xFFF5F5F5),
    onBackground     = Color(0xFF212121),
    surface          = Color.White,
    onSurface        = Color(0xFF212121),
    surfaceVariant   = Color(0xFFE8F0FE),
    onSurfaceVariant = Color(0xFF757575),
    outline          = Color(0xFF1E88E5),
)

// Dark colors - base #1a1a1a
private val DarkColorScheme = darkColorScheme(
    primary          = Color(0xFF90CAF9),
    onPrimary        = Color(0xFF1a1a1a),
    primaryContainer = Color(0xFF1E3A5F),
    secondary        = Color(0xFF64B5F6),
    onSecondary      = Color(0xFF1a1a1a),
    background       = Color(0xFF1a1a1a),
    onBackground     = Color(0xFFEEEEEE),
    surface          = Color(0xFF242424),
    onSurface        = Color(0xFFEEEEEE),
    surfaceVariant   = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFAAAAAA),
    outline          = Color(0xFF90CAF9),
)

@Composable
fun MengFlasherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
