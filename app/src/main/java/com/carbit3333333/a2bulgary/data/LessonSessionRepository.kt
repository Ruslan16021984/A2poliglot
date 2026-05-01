package com.carbit3333333.a2bulgary.data

import android.content.Context
import com.carbit3333333.a2bulgary.data.lesson_session.LessonSessionFactory
import com.carbit3333333.a2bulgary.data.lesson_session.TextbookLessonExercisesRepository
import com.carbit3333333.a2bulgary.model.LessonSession

class LessonSessionRepository(
    context: Context,
) {
    private val textbookExercisesRepository = TextbookLessonExercisesRepository(context)

    fun getLessonSession(lessonId: Int): LessonSession {
        val textbookExercises = requireNotNull(textbookExercisesRepository.loadForLesson(lessonId)) {
            "Textbook exercises not found for lesson $lessonId"
        }

        return LessonSessionFactory.create(
            lessonId = lessonId,
            textbookExercises = textbookExercises,
        )
    }
}
