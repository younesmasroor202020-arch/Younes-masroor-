package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val CinematicDarkColorScheme = darkColorScheme(
    primary = OchreGoldPrimary,
    onPrimary = OchreGoldOnPrimary,
    primaryContainer = OchreGoldContainer,
    onPrimaryContainer = OchreGoldOnContainer,
    secondary = PalmOliveSecondary,
    onSecondary = PalmOliveOnSecondary,
    secondaryContainer = PalmOliveContainer,
    onSecondaryContainer = PalmOliveOnContainer,
    tertiary = JardanAmberTertiary,
    onTertiary = JardanAmberOnTertiary,
    tertiaryContainer = JardanAmberContainer,
    onTertiaryContainer = JardanAmberOnContainer,
    background = CinematicDarkBackground,
    onBackground = CinematicOnSurface,
    surface = CinematicDarkSurface,
    onSurface = CinematicOnSurface,
    surfaceVariant = CinematicDarkSurfaceVariant,
    onSurfaceVariant = CinematicOnSurfaceVariant,
    outline = CinematicOutline,
    outlineVariant = CinematicOutlineVariant
)

private val CinematicLightColorScheme = lightColorScheme(
    primary = LightOchrePrimary,
    onPrimary = Color.White,
    primaryContainer = OchreGoldOnContainer,
    onPrimaryContainer = OchreGoldOnPrimary,
    secondary = PalmOliveContainer,
    onSecondary = Color.White,
    secondaryContainer = PalmOliveOnContainer,
    onSecondaryContainer = PalmOliveOnSecondary,
    tertiary = JardanAmberTertiary,
    onTertiary = Color.White,
    background = LightSurface,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurface,
    outline = CinematicOutline,
    outlineVariant = CinematicOutlineVariant
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to rich cinematic dark mode for film director app
    dynamicColor: Boolean = false, // Keep thematic Shabwa golden atmosphere
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> CinematicDarkColorScheme
        else -> CinematicLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
