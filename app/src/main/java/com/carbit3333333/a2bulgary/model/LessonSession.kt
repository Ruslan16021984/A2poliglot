package com.carbit3333333.a2bulgary.model

data class LessonSession(
    val lessonId: Int,
    val lessonTitle: String,
    val exercises: List<LessonExercise>,
)
