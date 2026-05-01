package com.carbit3333333.a2bulgary

import com.carbit3333333.a2bulgary.model.Lesson
import com.carbit3333333.a2bulgary.ui.lessons.LessonUiState
import com.carbit3333333.a2bulgary.ui.lessons.LessonsUiState
import org.junit.Assert.assertEquals
import org.junit.Test

class LessonViewModelContractTest {

    @Test
    fun uiStateDefaultsArePredictable() {
        val lessonsState = LessonsUiState()
        val lessonState = LessonUiState()

        assertEquals(emptyList<Lesson>(), lessonsState.lessons)
        assertEquals(null, lessonState.lesson)
        assertEquals(false, lessonsState.isLoading)
        assertEquals(false, lessonState.isLoading)
    }
}
