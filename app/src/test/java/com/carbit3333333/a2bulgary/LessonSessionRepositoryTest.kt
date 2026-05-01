package com.carbit3333333.a2bulgary

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.carbit3333333.a2bulgary.data.LessonSessionRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LessonSessionRepositoryTest {

    @Test
    fun lesson2SessionLoadsConstructorExercises() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = LessonSessionRepository(context)

        val session = repository.getLessonSession(2)

        assertEquals(2, session.lessonId)
        assertEquals("Ало, ало!", session.lessonTitle)
        assertTrue(session.exercises.size >= 3)
        assertEquals("Обаждам се за срещата утре.", session.exercises.first().sourceText)
    }
}
