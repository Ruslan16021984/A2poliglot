package com.carbit3333333.a2bulgary.model

data class LessonExercise(
    val id: Int,
    val sourceText: String,
    val instruction: String,
    val correctAnswerWords: List<String>,
    val availableWords: List<String>,
    val hint: String? = null,
)
