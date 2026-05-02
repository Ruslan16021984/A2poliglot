package com.carbit3333333.a2bulgary.model

data class SavedLessonResult(
    val lessonId: Int,
    val bestCorrectCount: Int,
    val bestWrongCount: Int,
    val bestScore: Float?,
    val currentScore: Float?,
    val isPassed: Boolean,
    val currentStep: Int,
    val totalSteps: Int,
)
