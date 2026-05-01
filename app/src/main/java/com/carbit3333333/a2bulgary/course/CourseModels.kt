package com.carbit3333333.a2bulgary.course

data class LessonUnit(
    val numberLabel: String,
    val title: String,
    val page: Int?,
    val goals: List<String>,
    val grammar: List<String>,
    val vocabulary: List<String>,
    val culture: String,
)
