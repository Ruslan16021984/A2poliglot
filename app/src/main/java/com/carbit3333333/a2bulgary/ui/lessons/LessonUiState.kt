package com.carbit3333333.a2bulgary.ui.lessons

import com.carbit3333333.a2bulgary.model.Lesson

data class LessonUiState(
    val lesson: Lesson? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
