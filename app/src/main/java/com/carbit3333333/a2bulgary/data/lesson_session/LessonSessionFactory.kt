package com.carbit3333333.a2bulgary.data.lesson_session

import com.carbit3333333.a2bulgary.model.LessonExercise
import com.carbit3333333.a2bulgary.model.LessonSession

internal object LessonSessionFactory {

    fun create(
        lessonId: Int,
        textbookExercises: TextbookLessonExerciseSetAsset,
    ): LessonSession {
        return LessonSession(
            lessonId = lessonId,
            lessonTitle = textbookExercises.title,
            exercises = textbookExercises.items.mapIndexed { index, item ->
                LessonExercise(
                    id = index + 1,
                    sourceText = item.bg,
                    instruction = item.ru,
                    correctAnswerWords = item.correctWords,
                    availableWords = (item.correctWords + item.distractors).shuffled(),
                    hint = item.hintRu,
                )
            },
        )
    }
}
