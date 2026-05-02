package com.carbit3333333.a2bulgary.ui.lessons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class LessonSessionPalette(
    val pageBackground: Color,
    val topBar: Color,
    val topBarText: Color,
    val counterSurface: Color,
    val counterCorrect: Color,
    val counterWrong: Color,
    val cardSurface: Color,
    val cardBorder: Color,
    val titleText: Color,
    val bodyText: Color,
    val instructionSurface: Color,
    val instructionText: Color,
    val selectedChipSurface: Color,
    val selectedChipText: Color,
    val primaryButton: Color,
    val primaryButtonText: Color,
    val disabledButton: Color,
    val disabledButtonText: Color,
    val progressIdle: Color,
    val progressCorrect: Color,
    val progressWrong: Color,
)

@Composable
fun rememberLessonSessionPalette(): LessonSessionPalette {
    return LessonSessionPalette(
        pageBackground = Color(0xFFF8F8FE),
        topBar = Color(0xFF566BA1),
        topBarText = Color(0xFFFDFEFF),
        counterSurface = Color(0xFFFDFEFF),
        counterCorrect = Color(0xFF566BA1),
        counterWrong = Color(0xFFB86C6C),
        cardSurface = Color(0xFFF4F6FF),
        cardBorder = Color(0xFFDCE1F2),
        titleText = Color(0xFF1F2430),
        bodyText = Color(0xFF666E7E),
        instructionSurface = Color(0xFFD9EBD7),
        instructionText = Color(0xFF2E3B2E),
        selectedChipSurface = Color(0xFFDCE7FF),
        selectedChipText = Color(0xFF2B3C63),
        primaryButton = Color(0xFF566BA1),
        primaryButtonText = Color(0xFFFDFEFF),
        disabledButton = Color(0xFFD9DCE4),
        disabledButtonText = Color(0xFF9A9EAA),
        progressIdle = Color(0xFFD7DBE6),
        progressCorrect = Color(0xFF4CAF50),
        progressWrong = Color(0xFFE98B8B),
    )
}
