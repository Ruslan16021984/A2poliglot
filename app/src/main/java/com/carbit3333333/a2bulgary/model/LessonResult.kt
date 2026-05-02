package com.carbit3333333.a2bulgary.model

data class LessonResult(
    val lessonId: Int,
    val lessonTitle: String,
    val totalExercises: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val score: Float,
    val isPassed: Boolean,
)
