package com.carbit3333333.a2bulgary.data

import android.content.Context
import com.carbit3333333.a2bulgary.model.Lesson
import kotlinx.serialization.json.Json

class LessonRepository(
    context: Context,
) {

    private val appContext = context.applicationContext

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val lessonsCache: List<Lesson> by lazy {
        loadLessonsFromAssets()
    }

    fun getLessons(): List<Lesson> = lessonsCache

    fun getLessonById(lessonId: Int): Lesson? {
        return lessonsCache.find { it.id == lessonId }
    }

    fun hasNextLesson(currentLessonId: Int): Boolean {
        return lessonsCache.any { it.id == currentLessonId + 1 }
    }

    fun getNextLessonId(currentLessonId: Int): Int? {
        return lessonsCache.find { it.id == currentLessonId + 1 }?.id
    }

    private fun loadLessonsFromAssets(): List<Lesson> {
        return appContext.assets
            .open(LESSONS_RU_ASSET_FILE)
            .bufferedReader(Charsets.UTF_8)
            .use { reader ->
                json.decodeFromString<List<Lesson>>(reader.readText())
            }
    }

    private companion object {
        const val LESSONS_RU_ASSET_FILE = "lessons_ru.json"
    }
}
