package com.carbit3333333.a2bulgary.ui.lessons

import com.carbit3333333.a2bulgary.model.Lesson

data class LessonsUiState(
    val isLoading: Boolean = false,
    val lessons: List<Lesson> = emptyList(),
    val errorMessage: String? = null,
    val showDeveloperActions: Boolean = false,
)
