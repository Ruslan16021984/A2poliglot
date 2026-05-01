package com.carbit3333333.a2bulgary.ui.lessons

import com.carbit3333333.a2bulgary.model.ExerciseResult
import com.carbit3333333.a2bulgary.model.LessonExercise

data class LessonSessionUiState(
    val lessonTitle: String = "",
    val exercises: List<LessonExercise> = emptyList(),
    val currentExerciseIndex: Int = 0,
    val selectedWords: List<String> = emptyList(),
    val results: List<ExerciseResult> = emptyList(),
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val currentResult: ExerciseResult = ExerciseResult.NONE,
    val isLessonFinished: Boolean = false,
    val errorMessage: String? = null,
) {
    val currentExercise: LessonExercise?
        get() = exercises.getOrNull(currentExerciseIndex)
}
