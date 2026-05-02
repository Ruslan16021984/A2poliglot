package com.carbit3333333.a2bulgary.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

@Composable
fun HighlightedEndingText(
    word: String,
    ending: String,
    highlightColor: Color = MaterialTheme.colorScheme.tertiary,
) {
    val splitIndex = word.length - ending.length

    if (ending.isBlank() || splitIndex <= 0 || !word.endsWith(ending)) {
        Text(
            text = word,
            style = MaterialTheme.typography.bodyLarge,
        )
        return
    }

    val annotated = buildAnnotatedString {
        append(word.substring(0, splitIndex))
        withStyle(
            SpanStyle(color = highlightColor),
        ) {
            append(word.substring(splitIndex))
        }
    }

    Text(
        text = annotated,
        style = MaterialTheme.typography.bodyLarge,
    )
}
