package com.carbit3333333.a2bulgary.ui.dictionary

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carbit3333333.a2bulgary.R
import com.carbit3333333.a2bulgary.model.dictionary.WordGroup
import com.carbit3333333.a2bulgary.ui.theme.A2BulgaryTheme
import com.carbit3333333.a2bulgary.viewmodel.WordEditorViewModel

@Composable
fun WordEditorScreen(
    onBackClick: () -> Unit,
    viewModel: WordEditorViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var speechTarget by remember { mutableStateOf<WordEditorViewModel.SpeechTarget?>(null) }

    BackHandler(enabled = uiState.isSaving) {}

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val recognizedText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!recognizedText.isNullOrBlank()) {
                speechTarget?.let { target -> viewModel.appendRecognizedText(target, recognizedText) }
            }
        }
        speechTarget = null
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            viewModel.acknowledgeSaveCompleted()
            if (uiState.isEditMode) onBackClick()
        }
    }

    WordEditorScreenContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onBgWordChange = viewModel::updateBgWord,
        onRuTranslationChange = viewModel::updateRuTranslation,
        onToggleGroup = viewModel::toggleGroupSelection,
        onShowNewGroupDialog = viewModel::showNewGroupDialog,
        onDismissNewGroupDialog = viewModel::dismissNewGroupDialog,
        onNewGroupNameChange = viewModel::updateNewGroupName,
        onCreateGroup = viewModel::createGroup,
        onSaveClick = viewModel::saveWord,
        onDismissError = viewModel::clearError,
        onDismissSuccess = viewModel::clearSuccess,
        onSpeechInputClick = { target ->
            if (!uiState.isEditorInteractive) return@WordEditorScreenContent
            speechTarget = target
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    if (target == WordEditorViewModel.SpeechTarget.Bulgarian) "bg-BG" else "ru-RU",
                )
                putExtra(
                    RecognizerIntent.EXTRA_PROMPT,
                    if (target == WordEditorViewModel.SpeechTarget.Bulgarian) {
                        context.getString(R.string.word_editor_speech_prompt_bg)
                    } else {
                        context.getString(R.string.word_editor_speech_prompt_ru)
                    },
                )
            }
            runCatching { speechLauncher.launch(intent) }.onFailure {
                speechTarget = null
                viewModel.onSpeechRecognitionUnavailable()
            }
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WordEditorScreenContent(
    uiState: WordEditorUiState,
    onBackClick: () -> Unit,
    onBgWordChange: (String) -> Unit,
    onRuTranslationChange: (String) -> Unit,
    onToggleGroup: (Long) -> Unit,
    onShowNewGroupDialog: () -> Unit,
    onDismissNewGroupDialog: () -> Unit,
    onNewGroupNameChange: (String) -> Unit,
    onCreateGroup: () -> Unit,
    onSaveClick: () -> Unit,
    onDismissError: () -> Unit,
    onDismissSuccess: () -> Unit,
    onSpeechInputClick: (WordEditorViewModel.SpeechTarget) -> Unit,
) {
    val palette = rememberDictionaryPalette()
    if (uiState.isNewGroupDialogVisible) {
        AlertDialog(
            onDismissRequest = { if (uiState.isEditorInteractive) onDismissNewGroupDialog() },
            title = { Text(stringResource(R.string.word_editor_new_group_title)) },
            text = {
                OutlinedTextField(
                    value = uiState.newGroupName,
                    onValueChange = onNewGroupNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = uiState.isEditorInteractive,
                    label = { Text(stringResource(R.string.word_editor_group_name_label)) },
                    placeholder = { Text(stringResource(R.string.word_editor_group_name_placeholder)) },
                    shape = RoundedCornerShape(16.dp),
                )
            },
            confirmButton = {
                Button(
                    onClick = onCreateGroup,
                    enabled = uiState.isEditorInteractive && uiState.newGroupName.trim().isNotEmpty(),
                ) {
                    Text(stringResource(R.string.common_create))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDismissNewGroupDialog, enabled = uiState.isEditorInteractive) {
                    Text(stringResource(R.string.common_cancel))
                }
            },
        )
    }

    Surface(modifier = Modifier.fillMaxSize(), color = palette.pageBackground) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBackClick,
                            enabled = uiState.isBackEnabled,
                            modifier = Modifier.clip(CircleShape).background(palette.surface),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.common_back),
                                tint = palette.title,
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (uiState.isEditMode) {
                                stringResource(R.string.word_editor_edit_title)
                            } else {
                                stringResource(R.string.word_editor_new_title)
                            },
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.title,
                        )
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
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                    ) {
                        if (uiState.isLoading) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            EditorFieldCard(
                                title = stringResource(R.string.word_editor_bg_title),
                                value = uiState.bgWord,
                                placeholder = stringResource(R.string.word_editor_bg_placeholder),
                                supportingText = if (uiState.hasBgWordError) {
                                    stringResource(R.string.word_editor_bg_required)
                                } else null,
                                enabled = uiState.isEditorInteractive,
                                onValueChange = onBgWordChange,
                                onSpeechInputClick = {
                                    onSpeechInputClick(WordEditorViewModel.SpeechTarget.Bulgarian)
                                },
                                palette = palette,
                            )
                            EditorFieldCard(
                                title = stringResource(R.string.word_editor_ru_title),
                                value = uiState.ruTranslation,
                                placeholder = stringResource(R.string.word_editor_ru_placeholder),
                                supportingText = if (uiState.hasRuTranslationError) {
                                    stringResource(R.string.word_editor_ru_required)
                                } else null,
                                enabled = uiState.isEditorInteractive,
                                onValueChange = onRuTranslationChange,
                                onSpeechInputClick = {
                                    onSpeechInputClick(WordEditorViewModel.SpeechTarget.Russian)
                                },
                                palette = palette,
                            )
                        }
                    }
                }
            }

            uiState.errorMessage?.let { error ->
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = palette.errorSurface), shape = RoundedCornerShape(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = error, modifier = Modifier.weight(1f), color = palette.errorText)
                            OutlinedButton(onClick = onDismissError) {
                                Text(stringResource(R.string.common_ok))
                            }
                        }
                    }
                }
            }

            uiState.successMessage?.let { success ->
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = palette.successSurface), shape = RoundedCornerShape(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = success, modifier = Modifier.weight(1f), color = palette.successText)
                            OutlinedButton(onClick = onDismissSuccess) {
                                Text(stringResource(R.string.common_ok))
                            }
                        }
                    }
                }
            }

            if (uiState.isSaving) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = palette.infoSurface), shape = RoundedCornerShape(18.dp)) {
                        Text(
                            text = stringResource(R.string.word_editor_saving_message),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            color = palette.infoText,
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = palette.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.word_editor_save_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.body,
                        )
                        Button(
                            onClick = onSaveClick,
                            enabled = uiState.isSaveEnabled,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            contentPadding = PaddingValues(vertical = 16.dp),
                        ) {
                            Text(
                                if (uiState.isEditMode) {
                                    stringResource(R.string.word_editor_save_changes)
                                } else {
                                    stringResource(R.string.word_editor_add_word)
                                }
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = palette.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.word_editor_groups_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.title,
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            uiState.availableGroups.forEach { group ->
                                GroupSelectionChip(
                                    group = group,
                                    selected = group.id in uiState.selectedGroupIds,
                                    onClick = { onToggleGroup(group.id) },
                                    accentColor = palette.accent,
                                    surfaceColor = palette.surface,
                                    borderColor = palette.border,
                                    bodyColor = palette.body,
                                    titleColor = palette.title,
                                    enabled = uiState.isEditorInteractive,
                                )
                            }
                            OutlinedButton(
                                onClick = onShowNewGroupDialog,
                                enabled = uiState.isEditorInteractive,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.5.dp, palette.accent.copy(alpha = 0.55f)),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = palette.accent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.word_editor_new_group), color = palette.accent)
                            }
                        }

                        if (uiState.availableGroups.isEmpty()) {
                            Text(
                                text = stringResource(R.string.word_editor_no_groups),
                                style = MaterialTheme.typography.bodySmall,
                                color = palette.body,
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = palette.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = palette.title)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.word_editor_additional),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = palette.title,
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = palette.body,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EditorFieldCard(
    title: String,
    value: String,
    placeholder: String,
    supportingText: String?,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    onSpeechInputClick: () -> Unit,
    palette: DictionaryPalette,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium, color = palette.title)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = !enabled,
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            placeholder = { Text(placeholder, color = palette.body) },
            trailingIcon = {
                IconButton(onClick = onSpeechInputClick, enabled = enabled) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_btn_speak_now),
                        contentDescription = stringResource(R.string.word_editor_voice_input),
                        tint = palette.body,
                    )
                }
            },
            isError = supportingText != null,
            supportingText = if (supportingText == null) {
                null
            } else {
                { Text(supportingText) }
            },
        )
    }
}

@Composable
private fun GroupSelectionChip(
    group: WordGroup,
    selected: Boolean,
    onClick: () -> Unit,
    accentColor: Color,
    surfaceColor: Color,
    borderColor: Color,
    bodyColor: Color,
    titleColor: Color,
    enabled: Boolean,
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) accentColor.copy(alpha = 0.08f).compositeOver(surfaceColor) else surfaceColor,
        border = BorderStroke(1.dp, if (selected) accentColor else borderColor),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if ("еда" in group.name.lowercase()) Icons.Default.Add else Icons.Default.Star,
                contentDescription = null,
                tint = if (selected) accentColor else bodyColor,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = group.name,
                color = if (selected) accentColor else titleColor,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WordEditorScreenPreview() {
    A2BulgaryTheme {
        WordEditorScreenContent(
            uiState = WordEditorUiState(
                availableGroups = listOf(
                    WordGroup(1L, "Путешествие", 24),
                    WordGroup(2L, "Еда", 18),
                ),
                selectedGroupIds = setOf(2L),
            ),
            onBackClick = {},
            onBgWordChange = {},
            onRuTranslationChange = {},
            onToggleGroup = {},
            onShowNewGroupDialog = {},
            onDismissNewGroupDialog = {},
            onNewGroupNameChange = {},
            onCreateGroup = {},
            onSaveClick = {},
            onDismissError = {},
            onDismissSuccess = {},
            onSpeechInputClick = {},
        )
    }
}
