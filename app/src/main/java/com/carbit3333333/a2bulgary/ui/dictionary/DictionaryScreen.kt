package com.carbit3333333.a2bulgary.ui.dictionary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carbit3333333.a2bulgary.R
import com.carbit3333333.a2bulgary.model.dictionary.DictionaryWordListItem
import com.carbit3333333.a2bulgary.model.dictionary.WordGroup
import com.carbit3333333.a2bulgary.ui.theme.A2BulgaryTheme
import com.carbit3333333.a2bulgary.viewmodel.DictionaryViewModel
import kotlinx.coroutines.launch

@Composable
fun DictionaryScreen(
    onBackClick: () -> Unit,
    onAddWordClick: () -> Unit,
    onTrainAllClick: () -> Unit,
    onTrainGroupClick: (WordGroup) -> Unit,
    onWordClick: (Long) -> Unit,
    onOpenLessonClick: (Int) -> Unit,
    viewModel: DictionaryViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val hasAnyWordsForTraining by viewModel.hasAnyWordsForTraining.collectAsStateWithLifecycle()

    DictionaryScreenContent(
        uiState = uiState,
        hasAnyWordsForTraining = hasAnyWordsForTraining,
        onBackClick = onBackClick,
        onAddWordClick = onAddWordClick,
        onTrainAllClick = onTrainAllClick,
        onTrainGroupClick = onTrainGroupClick,
        onWordClick = onWordClick,
        onOpenLessonClick = onOpenLessonClick,
        onQueryChange = viewModel::updateQuery,
        onGroupSelect = viewModel::selectGroup,
        onLoadMoreClick = viewModel::loadMoreWords,
        onDeleteWordClick = viewModel::deleteWord,
        onDismissError = viewModel::clearError,
    )
}

@Composable
fun DictionaryScreenContent(
    uiState: DictionaryListUiState,
    hasAnyWordsForTraining: Boolean,
    onBackClick: () -> Unit,
    onAddWordClick: () -> Unit,
    onTrainAllClick: () -> Unit,
    onTrainGroupClick: (WordGroup) -> Unit,
    onWordClick: (Long) -> Unit,
    onOpenLessonClick: (Int) -> Unit,
    onQueryChange: (String) -> Unit,
    onGroupSelect: (Long?) -> Unit,
    onLoadMoreClick: () -> Unit,
    onDeleteWordClick: (Long) -> Unit,
    onDismissError: () -> Unit,
) {
    val palette = rememberDictionaryPalette()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showScrollToTopButton = listState.firstVisibleItemIndex > 6
    var pendingDeleteWordId by rememberSaveable { mutableStateOf<Long?>(null) }
    var pendingDeleteWordLabel by rememberSaveable { mutableStateOf("") }

    pendingDeleteWordId?.let { wordId ->
        AlertDialog(
            onDismissRequest = {
                pendingDeleteWordId = null
                pendingDeleteWordLabel = ""
            },
            title = { Text(stringResource(R.string.dictionary_delete_word_title)) },
            text = { Text(stringResource(R.string.dictionary_delete_word_message, pendingDeleteWordLabel)) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteWordClick(wordId)
                        pendingDeleteWordId = null
                        pendingDeleteWordLabel = ""
                    },
                ) {
                    Text(stringResource(R.string.common_delete))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        pendingDeleteWordId = null
                        pendingDeleteWordLabel = ""
                    },
                ) {
                    Text(stringResource(R.string.common_cancel))
                }
            },
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = palette.pageBackground,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(palette.surface),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.common_back),
                                tint = palette.title,
                            )
                        }

                        Button(
                            onClick = onAddWordClick,
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                        ) {
                            Text(stringResource(R.string.dictionary_add_button))
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = palette.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.dictionary_title),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.title,
                            )
                            Text(
                                text = stringResource(R.string.dictionary_description),
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.body,
                            )
                            OutlinedTextField(
                                value = uiState.query,
                                onValueChange = onQueryChange,
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = palette.body,
                                    )
                                },
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.dictionary_search_placeholder),
                                        color = palette.body,
                                    )
                                },
                                shape = RoundedCornerShape(18.dp),
                                singleLine = true,
                            )
                            Button(
                                onClick = onTrainAllClick,
                                enabled = hasAnyWordsForTraining,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                contentPadding = PaddingValues(vertical = 16.dp),
                            ) {
                                Text(stringResource(R.string.dictionary_train_all))
                            }
                            GroupChipsRow(
                                groups = uiState.groups,
                                selectedGroupId = uiState.selectedGroupId,
                                onGroupSelect = onGroupSelect,
                                accentColor = palette.accent,
                                borderColor = palette.border,
                                bodyColor = palette.body,
                            )
                        }
                    }
                }

                if (uiState.groups.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.dictionary_groups_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.title,
                        )
                    }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(uiState.groups, key = { it.id }) { group ->
                                GroupTrainingCard(
                                    group = group,
                                    onTrainGroupClick = onTrainGroupClick,
                                    accentColor = palette.accent,
                                    surfaceColor = palette.elevatedSurface,
                                    borderColor = palette.border,
                                    titleColor = palette.title,
                                    bodyColor = palette.body,
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.dictionary_words_count_title, uiState.totalWordsCount),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.title,
                        )
                        Text(
                            text = stringResource(R.string.dictionary_sort_alphabetical),
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.body,
                        )
                    }
                }

                if (uiState.isLoading) {
                    item {
                        EmptyDictionaryBlock(
                            title = stringResource(R.string.dictionary_loading_title),
                            message = stringResource(R.string.dictionary_loading_message),
                        )
                    }
                } else if (uiState.visibleWords.isEmpty()) {
                    item {
                        EmptyDictionaryBlock(
                            title = stringResource(R.string.dictionary_empty_title),
                            message = if (uiState.query.isBlank() && uiState.selectedGroupId == null) {
                                stringResource(R.string.dictionary_empty_message)
                            } else {
                                stringResource(R.string.dictionary_empty_filtered_message)
                            },
                        )
                    }
                } else {
                    items(uiState.visibleWords, key = { it.id }) { word ->
                        DictionaryWordRow(
                            word = word,
                            onClick = {
                                if (word.isBuiltIn) {
                                    word.sourceLessonNumber?.let(onOpenLessonClick)
                                } else {
                                    onWordClick(word.id)
                                }
                            },
                            onOpenLessonClick = { word.sourceLessonNumber?.let(onOpenLessonClick) },
                            onDeleteClick = {
                                if (!word.isBuiltIn) {
                                    pendingDeleteWordId = word.id
                                    pendingDeleteWordLabel = word.bgWord
                                }
                            },
                            surfaceColor = palette.elevatedSurface,
                            builtInSurfaceColor = palette.accentSurface,
                            builtInBadgeColor = palette.accent,
                            builtInBadgeTextColor = palette.accentText,
                            titleColor = palette.title,
                            bodyColor = palette.body,
                            borderColor = palette.border,
                        )
                    }
                    if (uiState.canLoadMore) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    text = stringResource(
                                        R.string.dictionary_load_more_progress,
                                        uiState.visibleWords.size,
                                        uiState.totalWordsCount,
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = palette.body,
                                )
                                Button(
                                    onClick = onLoadMoreClick,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    contentPadding = PaddingValues(vertical = 14.dp),
                                ) {
                                    Text(stringResource(R.string.common_show_more))
                                }
                            }
                        }
                    }
                }
            }

            if (showScrollToTopButton) {
                ExtendedFloatingActionButton(
                    onClick = {
                        coroutineScope.launch { listState.animateScrollToItem(0) }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .navigationBarsPadding()
                        .padding(end = 18.dp, bottom = 18.dp),
                    containerColor = palette.accent,
                    contentColor = palette.accentText,
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Text(stringResource(R.string.common_scroll_to_top))
                }
            }
        }
    }
}

@Composable
private fun GroupChipsRow(
    groups: List<WordGroup>,
    selectedGroupId: Long?,
    onGroupSelect: (Long?) -> Unit,
    accentColor: Color,
    borderColor: Color,
    bodyColor: Color,
) {
    val palette = rememberDictionaryPalette()
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        DictionaryFilterChip(
            label = stringResource(R.string.dictionary_all_filter),
            selected = selectedGroupId == null,
            onClick = { onGroupSelect(null) },
            accentColor = accentColor,
            accentTextColor = palette.accentText,
            surfaceColor = palette.surface,
            borderColor = borderColor,
            bodyColor = bodyColor,
        )
        groups.forEach { group ->
            DictionaryFilterChip(
                label = group.name,
                selected = selectedGroupId == group.id,
                onClick = { onGroupSelect(group.id) },
                accentColor = accentColor,
                accentTextColor = palette.accentText,
                surfaceColor = palette.surface,
                borderColor = borderColor,
                bodyColor = bodyColor,
            )
        }
    }
}

@Composable
private fun DictionaryFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    accentColor: Color,
    accentTextColor: Color,
    surfaceColor: Color,
    borderColor: Color,
    bodyColor: Color,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(999.dp),
        color = if (selected) accentColor else surfaceColor,
        border = if (selected) null else BorderStroke(1.dp, borderColor),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            color = if (selected) accentTextColor else bodyColor,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun GroupTrainingCard(
    group: WordGroup,
    onTrainGroupClick: (WordGroup) -> Unit,
    accentColor: Color,
    surfaceColor: Color,
    borderColor: Color,
    titleColor: Color,
    bodyColor: Color,
) {
    val iconTint = colorForGroup(group.name)
    Card(
        modifier = Modifier.width(164.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.8f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = iconTint.copy(alpha = 0.14f),
            ) {
                Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                    Icon(imageVector = iconForGroup(group.name), contentDescription = null, tint = iconTint)
                }
            }
            Column {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.dictionary_word_count, group.wordCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = bodyColor,
                )
            }
            OutlinedButton(
                onClick = { onTrainGroupClick(group) },
                enabled = group.wordCount > 0,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
            ) {
                Text(stringResource(R.string.dictionary_train_group), color = accentColor)
            }
        }
    }
}

@Composable
private fun DictionaryWordRow(
    word: DictionaryWordListItem,
    onClick: () -> Unit,
    onOpenLessonClick: () -> Unit,
    onDeleteClick: () -> Unit,
    surfaceColor: Color,
    builtInSurfaceColor: Color,
    builtInBadgeColor: Color,
    builtInBadgeTextColor: Color,
    titleColor: Color,
    bodyColor: Color,
    borderColor: Color,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !word.isBuiltIn || word.sourceLessonNumber != null, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (word.isBuiltIn) builtInSurfaceColor else surfaceColor
        ),
        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (word.isBuiltIn) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(color = builtInBadgeColor, shape = RoundedCornerShape(999.dp)) {
                            Text(
                                text = stringResource(R.string.dictionary_course_word_badge),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = builtInBadgeTextColor,
                            )
                        }
                        word.sourceLessonNumber?.let { lessonNumber ->
                            Surface(color = builtInBadgeColor.copy(alpha = 0.18f), shape = RoundedCornerShape(999.dp)) {
                                Text(
                                    text = stringResource(R.string.dictionary_course_lesson_badge, lessonNumber),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = titleColor,
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = word.bgWord,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = word.ruTranslation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = bodyColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (word.isBuiltIn && word.sourceLessonNumber != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onOpenLessonClick,
                        shape = RoundedCornerShape(999.dp),
                        border = BorderStroke(1.dp, builtInBadgeColor.copy(alpha = 0.35f)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.dictionary_open_lesson_action),
                            color = builtInBadgeColor,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }

            if (!word.isBuiltIn) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.dictionary_delete_word_content_description),
                        tint = bodyColor,
                    )
                }
                Text(text = ">", color = bodyColor, style = MaterialTheme.typography.titleMedium)
            } else if (word.sourceLessonNumber != null) {
                Text(text = ">", color = builtInBadgeColor, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun EmptyDictionaryBlock(title: String, message: String) {
    val palette = rememberDictionaryPalette()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = palette.title)
            Text(text = message, style = MaterialTheme.typography.bodyMedium, color = palette.body)
        }
    }
}

private fun iconForGroup(name: String): ImageVector {
    val normalized = name.lowercase()
    return when {
        "быт" in normalized || "дом" in normalized -> Icons.Default.Home
        "еда" in normalized || "ресторан" in normalized -> Icons.Default.Add
        else -> Icons.Default.Star
    }
}

@Composable
private fun colorForGroup(name: String): Color {
    val normalized = name.lowercase()
    val colorScheme = MaterialTheme.colorScheme
    return when {
        "еда" in normalized || "ресторан" in normalized -> colorScheme.tertiary
        "пут" in normalized || "дорог" in normalized || "поезд" in normalized -> colorScheme.secondary
        "быт" in normalized || "дом" in normalized -> colorScheme.primary
        else -> colorScheme.error
    }
}

@Preview(showBackground = true)
@Composable
private fun DictionaryScreenContentPreview() {
    A2BulgaryTheme {
        DictionaryScreenContent(
            uiState = DictionaryListUiState(
                words = listOf(
                    DictionaryWordListItem(1L, "zdravei", "привет"),
                    DictionaryWordListItem(2L, "bilet", "билет"),
                ),
                visibleWords = listOf(
                    DictionaryWordListItem(1L, "zdravei", "привет"),
                    DictionaryWordListItem(2L, "bilet", "билет"),
                ),
                totalWordsCount = 2,
                groups = listOf(
                    WordGroup(1L, "Путешествие", 10),
                    WordGroup(2L, "Еда", 6),
                ),
            ),
            hasAnyWordsForTraining = true,
            onBackClick = {},
            onAddWordClick = {},
            onTrainAllClick = {},
            onTrainGroupClick = {},
            onWordClick = {},
            onOpenLessonClick = {},
            onQueryChange = {},
            onGroupSelect = {},
            onLoadMoreClick = {},
            onDeleteWordClick = {},
            onDismissError = {},
        )
    }
}
