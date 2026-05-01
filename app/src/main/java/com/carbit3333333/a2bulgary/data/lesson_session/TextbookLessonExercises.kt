package com.carbit3333333.a2bulgary.data.lesson_session

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class TextbookLessonExerciseItemAsset(
    val id: String,
    val theme: String,
    val bg: String,
    val ru: String,
    val correctWords: List<String>,
    val distractors: List<String> = emptyList(),
    val hintRu: String? = null,
)

@Serializable
data class TextbookLessonExerciseSetAsset(
    val lessonApp: Int,
    val lessonBook: Int,
    val title: String,
    val exerciseType: String,
    val source: String,
    val items: List<TextbookLessonExerciseItemAsset>,
)

class TextbookLessonExercisesRepository(
    context: Context,
) {
    private val appContext = context.applicationContext

    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun loadForLesson(lessonId: Int): TextbookLessonExerciseSetAsset? {
        if (lessonId != 2) return null

        return runCatching {
            appContext.assets
                .open("textbook_exercises_lesson$lessonId.json")
                .bufferedReader(Charsets.UTF_8)
                .use { json.decodeFromString<TextbookLessonExerciseSetAsset>(it.readText()) }
        }.getOrNull()
    }
}
