package com.carbit3333333.a2bulgary.ui.dictionary

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class DictionaryPalette(
    val pageBackground: Color,
    val surface: Color,
    val elevatedSurface: Color,
    val accent: Color,
    val accentText: Color,
    val accentSurface: Color,
    val title: Color,
    val body: Color,
    val border: Color,
    val successSurface: Color,
    val successText: Color,
    val errorSurface: Color,
    val errorText: Color,
    val infoSurface: Color,
    val infoText: Color,
    val hintPositiveSurface: Color,
    val hintPositiveText: Color,
    val hintNegativeSurface: Color,
    val hintNegativeText: Color,
)

@Composable
fun rememberDictionaryPalette(): DictionaryPalette {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()

    return if (isDark) {
        DictionaryPalette(
            pageBackground = colorScheme.background,
            surface = colorScheme.surfaceContainerLow,
            elevatedSurface = colorScheme.surfaceContainerHigh,
            accent = colorScheme.primary,
            accentText = colorScheme.onPrimary,
            accentSurface = colorScheme.primaryContainer,
            title = colorScheme.onSurface,
            body = colorScheme.onSurfaceVariant,
            border = colorScheme.outlineVariant,
            successSurface = colorScheme.tertiaryContainer,
            successText = colorScheme.onTertiaryContainer,
            errorSurface = colorScheme.errorContainer,
            errorText = colorScheme.onErrorContainer,
            infoSurface = colorScheme.secondaryContainer,
            infoText = colorScheme.onSecondaryContainer,
            hintPositiveSurface = colorScheme.tertiaryContainer,
            hintPositiveText = colorScheme.onTertiaryContainer,
            hintNegativeSurface = colorScheme.errorContainer,
            hintNegativeText = colorScheme.onErrorContainer,
        )
    } else {
        DictionaryPalette(
            pageBackground = colorScheme.background,
            surface = colorScheme.surface,
            elevatedSurface = colorScheme.surfaceContainerLow,
            accent = colorScheme.primary,
            accentText = colorScheme.onPrimary,
            accentSurface = colorScheme.primaryContainer,
            title = colorScheme.onSurface,
            body = colorScheme.onSurfaceVariant,
            border = colorScheme.outlineVariant,
            successSurface = colorScheme.tertiaryContainer,
            successText = colorScheme.onTertiaryContainer,
            errorSurface = colorScheme.errorContainer,
            errorText = colorScheme.onErrorContainer,
            infoSurface = colorScheme.secondaryContainer,
            infoText = colorScheme.onSecondaryContainer,
            hintPositiveSurface = colorScheme.tertiaryContainer,
            hintPositiveText = colorScheme.onTertiaryContainer,
            hintNegativeSurface = colorScheme.errorContainer,
            hintNegativeText = colorScheme.onErrorContainer,
        )
    }
}
