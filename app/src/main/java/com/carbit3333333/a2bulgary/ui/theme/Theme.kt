package com.carbit3333333.a2bulgary.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Mist,
    secondary = DeepTeal,
    tertiary = ClayRed,
    background = NightForest,
    surface = Color(0xFF1B302B),
    onPrimary = Ink,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFF2F5F1),
    onSurface = Color(0xFFF2F5F1),
    onSurfaceVariant = Color(0xFFD7E0D8),
    primaryContainer = Color(0xFF2B4A43),
    onPrimaryContainer = Color(0xFFF2F5F1),
    secondaryContainer = Color(0xFF33524B),
    onSecondaryContainer = Color(0xFFF2F5F1),
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    secondary = DeepTeal,
    tertiary = ClayRed,
    background = Paper,
    surface = SurfaceWarm,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Ink,
    onSurface = Ink,
    onSurfaceVariant = Color(0xFF5C6762),
    primaryContainer = Color(0xFFDCE8D8),
    onPrimaryContainer = Ink,
    secondaryContainer = Color(0xFFD9E6DF),
    onSecondaryContainer = Ink,
)

@Composable
fun A2BulgaryTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
