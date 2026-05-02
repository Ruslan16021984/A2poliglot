package com.carbit3333333.a2bulgary.ui.lessons

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carbit3333333.a2bulgary.R
import com.carbit3333333.a2bulgary.model.Lesson
import com.carbit3333333.a2bulgary.ui.theme.A2BulgaryTheme
import com.carbit3333333.a2bulgary.viewmodel.LessonsViewModel
import java.util.Locale

@Composable
fun LessonsScreen(
    onBackClick: (() -> Unit)? = null,
    onLessonClick: (Int) -> Unit = {},
    onDictionaryClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    viewModel: LessonsViewModel = viewModel(
        factory = LessonsViewModel.provideFactory(
            LocalContext.current.applicationContext as Application
        )
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LessonsScreenContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onLessonClick = onLessonClick,
        onDictionaryClick = onDictionaryClick,
        onSettingsClick = onSettingsClick,
        onUnlockAllClick = {},
        onResetLessonsClick = {},
    )
}

@Composable
fun LessonsScreenContent(
    uiState: LessonsUiState,
    onBackClick: (() -> Unit)?,
    onLessonClick: (Int) -> Unit,
    onDictionaryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onUnlockAllClick: () -> Unit = {},
    onResetLessonsClick: () -> Unit = {},
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDarkTheme = isSystemInDarkTheme()
    var lockedLessonDialog by remember { mutableStateOf<Lesson?>(null) }
    val headerContainer = if (isDarkTheme) colorScheme.surfaceContainerHigh else colorScheme.surfaceContainerLow
    val headerAccentSurface = if (isDarkTheme) colorScheme.primary.copy(alpha = 0.20f) else colorScheme.primaryContainer
    val headerAccentText = if (isDarkTheme) colorScheme.primary else colorScheme.onPrimaryContainer
    val headerTitle = colorScheme.onSurface
    val headerBody = colorScheme.onSurfaceVariant
    val dictionaryContainer = if (isDarkTheme) colorScheme.surfaceContainer else colorScheme.secondaryContainer
    val dictionaryTitle = if (isDarkTheme) colorScheme.onSurface else colorScheme.onSecondaryContainer
    val dictionaryBody = if (isDarkTheme) colorScheme.onSurfaceVariant else colorScheme.onSecondaryContainer.copy(alpha = 0.78f)
    val lessonCardColor = if (isDarkTheme) colorScheme.surfaceContainerLow else colorScheme.surface
    val lockedLessonCardColor = if (isDarkTheme) colorScheme.surfaceContainer else colorScheme.surfaceContainerLow
    val actionOutlineColor = if (isDarkTheme) colorScheme.outlineVariant else colorScheme.outline
    val actionTextColor = colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        if (onBackClick != null) {
            Button(onClick = onBackClick) {
                Text(text = stringResource(R.string.common_back))
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        LessonsHeader(
            uiState = uiState,
            onSettingsClick = onSettingsClick,
            containerColor = headerContainer,
            accentSurfaceColor = headerAccentSurface,
            accentTextColor = headerAccentText,
            titleColor = headerTitle,
            bodyColor = headerBody,
        )

        Spacer(modifier = Modifier.height(16.dp))

        LessonActionsRow(
            showDeveloperActions = uiState.showDeveloperActions,
            onDictionaryClick = onDictionaryClick,
            onUnlockAllClick = onUnlockAllClick,
            onResetLessonsClick = onResetLessonsClick,
            containerColor = dictionaryContainer,
            titleColor = dictionaryTitle,
            bodyColor = dictionaryBody,
            actionOutlineColor = actionOutlineColor,
            actionTextColor = actionTextColor,
        )

        Spacer(modifier = Modifier.height(18.dp))

        when {
            uiState.isLoading -> {
                Text(text = stringResource(R.string.common_loading))
            }

            uiState.errorMessage != null -> {
                Text(text = uiState.errorMessage ?: stringResource(R.string.common_error))
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    items(uiState.lessons) { lesson ->
                        LessonItem(
                            lesson = lesson,
                            cardColor = lessonCardColor,
                            lockedCardColor = lockedLessonCardColor,
                            titleColor = colorScheme.onSurface,
                            subtitleColor = colorScheme.onSurfaceVariant,
                            lockedTextColor = colorScheme.outline,
                            progressDefaultColor = colorScheme.onSurfaceVariant,
                            progressActiveColor = colorScheme.primary,
                            progressDoneColor = colorScheme.tertiary,
                            onClick = {
                                if (lesson.isLocked) {
                                    lockedLessonDialog = lesson
                                } else {
                                    onLessonClick(lesson.id)
                                }
                            },
                        )
                    }
                }
            }
        }

        lockedLessonDialog?.let {
            AlertDialog(
                onDismissRequest = { lockedLessonDialog = null },
                confirmButton = {
                    TextButton(
                        onClick = {
                            lockedLessonDialog = null
                            onSettingsClick()
                        }
                    ) {
                        Text(stringResource(R.string.lessons_locked_dialog_open_settings))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { lockedLessonDialog = null }) {
                        Text(stringResource(R.string.common_cancel))
                    }
                },
                title = {
                    Text(stringResource(R.string.lessons_locked_dialog_title))
                },
                text = {
                    Text(stringResource(R.string.lessons_locked_dialog_message))
                },
            )
        }
    }
}

@Composable
private fun LessonsHeader(
    uiState: LessonsUiState,
    onSettingsClick: () -> Unit,
    containerColor: Color,
    accentSurfaceColor: Color,
    accentTextColor: Color,
    titleColor: Color,
    bodyColor: Color,
) {
    val totalLessons = uiState.lessons.size
    val completedLessons = uiState.lessons.count { it.isCompleted }
    val availableLessons = uiState.lessons.count { !it.isLocked }
    val inProgressLessons = uiState.lessons.count { !it.isLocked && it.currentProgress > 0 && !it.isCompleted }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        color = accentSurfaceColor,
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Box(
                            modifier = Modifier.size(42.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = totalLessons.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                color = accentTextColor,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.lessons_list_title),
                            style = MaterialTheme.typography.headlineSmall,
                            color = titleColor,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.lessons_list_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = bodyColor,
                        )
                    }
                }

                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.settings_more_options),
                        tint = bodyColor,
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                HeaderStat(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.lessons_stat_available),
                    value = "$availableLessons/$totalLessons",
                    accent = MaterialTheme.colorScheme.primary,
                )
                HeaderStat(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.lessons_stat_in_progress),
                    value = inProgressLessons.toString(),
                    accent = MaterialTheme.colorScheme.secondary,
                )
                HeaderStat(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.lessons_stat_completed),
                    value = completedLessons.toString(),
                    accent = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
    }
}

@Composable
private fun HeaderStat(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    accent: Color,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 1.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = accent,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LessonActionsRow(
    showDeveloperActions: Boolean,
    onDictionaryClick: () -> Unit,
    onUnlockAllClick: () -> Unit,
    onResetLessonsClick: () -> Unit,
    containerColor: Color,
    titleColor: Color,
    bodyColor: Color,
    actionOutlineColor: Color,
    actionTextColor: Color,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onDictionaryClick),
            colors = CardDefaults.cardColors(
                containerColor = containerColor,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = stringResource(R.string.lessons_dictionary_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = titleColor,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.lessons_dictionary_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = bodyColor,
                    )
                }

                OutlinedButton(
                    onClick = onDictionaryClick,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = actionTextColor,
                    ),
                    border = BorderStroke(1.dp, actionOutlineColor),
                ) {
                    Text(stringResource(R.string.common_open))
                }
            }
        }

        if (showDeveloperActions) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onUnlockAllClick,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = actionTextColor,
                    ),
                    border = BorderStroke(1.dp, actionOutlineColor),
                ) {
                    Text(stringResource(R.string.lessons_unlock_all))
                }

                OutlinedButton(
                    onClick = onResetLessonsClick,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = actionTextColor,
                    ),
                    border = BorderStroke(1.dp, actionOutlineColor),
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.lessons_reset))
                }
            }
        }
    }
}

@Composable
private fun LessonItem(
    lesson: Lesson,
    cardColor: Color,
    lockedCardColor: Color,
    titleColor: Color,
    subtitleColor: Color,
    lockedTextColor: Color,
    progressDefaultColor: Color,
    progressActiveColor: Color,
    progressDoneColor: Color,
    onClick: () -> Unit,
) {
    val containerColor = if (lesson.isLocked) lockedCardColor else cardColor
    val textColor = if (lesson.isLocked) lockedTextColor else titleColor
    val subtitleTextColor = if (lesson.isLocked) lockedTextColor.copy(alpha = 0.85f) else subtitleColor

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = lesson.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = subtitleTextColor,
                )

                Spacer(modifier = Modifier.height(12.dp))

                val progressText = when {
                    lesson.isLocked -> stringResource(R.string.lessons_progress_locked)
                    lesson.totalProgress > 0 -> {
                        stringResource(
                            R.string.lessons_progress_value,
                            lesson.currentProgress,
                            lesson.totalProgress,
                            lesson.progressPercent,
                        )
                    }

                    else -> stringResource(R.string.lessons_progress_empty)
                }

                val progressColor = when {
                    lesson.isLocked -> lockedTextColor
                    lesson.progressPercent in 1..99 -> progressActiveColor
                    lesson.progressPercent >= 100 -> progressDoneColor
                    else -> progressDefaultColor
                }

                Text(
                    text = progressText,
                    style = MaterialTheme.typography.bodySmall,
                    color = progressColor,
                )

                Spacer(modifier = Modifier.height(4.dp))

                val resultText = when {
                    lesson.isLocked -> stringResource(R.string.lessons_result_locked)
                    lesson.isCompleted -> stringResource(R.string.lessons_result_passed)
                    lesson.currentScore != null && lesson.currentProgress in 1 until lesson.totalProgress -> stringResource(
                        R.string.lessons_result_score,
                        String.format(Locale.US, "%.1f", lesson.currentScore),
                    )
                    lesson.bestScore != null -> stringResource(
                        R.string.lessons_result_score,
                        String.format(Locale.US, "%.1f", lesson.bestScore),
                    )
                    else -> stringResource(R.string.lessons_result_not_started)
                }

                val resultColor = when {
                    lesson.isLocked -> lockedTextColor
                    lesson.isCompleted -> progressDoneColor
                    lesson.currentScore != null && lesson.currentProgress in 1 until lesson.totalProgress -> progressActiveColor
                    lesson.bestScore != null -> progressDefaultColor
                    else -> progressDefaultColor
                }

                Text(
                    text = resultText,
                    style = MaterialTheme.typography.bodySmall,
                    color = resultColor,
                )
            }

            if (lesson.isLocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = lockedTextColor,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LessonsScreenContentPreview() {
    A2BulgaryTheme {
        LessonsScreenContent(
            uiState = LessonsUiState(
                lessons = listOf(
                    Lesson(
                        id = 1,
                        title = "Урок 1",
                        subtitle = "Приветствие и базовые фразы",
                        theory = emptyList(),
                        isCompleted = false,
                        isLocked = false,
                        bestScore = null,
                        currentScore = 0.2f,
                        currentProgress = 3,
                        totalProgress = 100,
                    ),
                    Lesson(
                        id = 2,
                        title = "Урок 2",
                        subtitle = "Знакомство",
                        theory = emptyList(),
                        isCompleted = true,
                        isLocked = false,
                        bestScore = 4.8f,
                        currentProgress = 100,
                        totalProgress = 100,
                    ),
                    Lesson(
                        id = 3,
                        title = "Урок 3",
                        subtitle = "Прошедшее время",
                        theory = emptyList(),
                        isLocked = true,
                    ),
                ),
            ),
            onBackClick = {},
            onLessonClick = {},
            onDictionaryClick = {},
            onSettingsClick = {},
            onUnlockAllClick = {},
            onResetLessonsClick = {},
        )
    }
}
