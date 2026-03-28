package com.urlopener.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val Teal = Color(0xFF42BFB5)
private val TealDark = Color(0xFF2D8A83)
private val TealLight = Color(0xFF6DD5CD)
private val DarkBlue = Color(0xFF2D4E6D)
private val White = Color(0xFFFFFFFF)
private val Black = Color(0xFF000000)

private val LightColorScheme = lightColorScheme(
    primary = Teal,
    onPrimary = White,
    primaryContainer = TealLight,
    onPrimaryContainer = DarkBlue,
    secondary = DarkBlue,
    onSecondary = White,
    secondaryContainer = Color(0xFFD4E4EF),
    onSecondaryContainer = DarkBlue,
    background = White,
    onBackground = DarkBlue,
    surface = White,
    onSurface = DarkBlue,
    error = Color(0xFFBA1A1A),
    onError = White
)

private val DarkColorScheme = darkColorScheme(
    primary = TealLight,
    onPrimary = DarkBlue,
    primaryContainer = TealDark,
    onPrimaryContainer = White,
    secondary = Color(0xFFB3C9DB),
    onSecondary = DarkBlue,
    secondaryContainer = DarkBlue,
    onSecondaryContainer = White,
    background = Color(0xFF1A1C1E),
    onBackground = White,
    surface = Color(0xFF1A1C1E),
    onSurface = White,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun URLFileOpenerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(colorScheme = colorScheme, content = content)
}
