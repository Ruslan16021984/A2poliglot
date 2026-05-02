package com.carbit3333333.a2bulgary.ui.dictionary

import com.carbit3333333.a2bulgary.model.dictionary.WordGroup

data class WordEditorUiState(
    val isLoading: Boolean = false,
    val wordId: Long? = null,
    val bgWord: String = "",
    val ruTranslation: String = "",
    val selectedGroupIds: Set<Long> = emptySet(),
    val availableGroups: List<WordGroup> = emptyList(),
    val isNewGroupDialogVisible: Boolean = false,
    val newGroupName: String = "",
    val showValidationErrors: Boolean = false,
    val isSaving: Boolean = false,
    val isSaveEnabled: Boolean = true,
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
) {
    val isEditMode: Boolean
        get() = wordId != null

    val hasBgWordError: Boolean
        get() = showValidationErrors && bgWord.trim().isEmpty()

    val hasRuTranslationError: Boolean
        get() = showValidationErrors && ruTranslation.trim().isEmpty()

    val isBackEnabled: Boolean
        get() = !isSaving

    val isEditorInteractive: Boolean
        get() = !isSaving
}
