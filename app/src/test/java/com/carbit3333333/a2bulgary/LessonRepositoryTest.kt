package com.carbit3333333.a2bulgary

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.carbit3333333.a2bulgary.data.LessonRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LessonRepositoryTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Test
    fun loadsLessonsFromAssetsAndExposesNextLessonHelpers() {
        val repository = LessonRepository(context)

        val lessons = repository.getLessons()

        assertEquals(11, lessons.size)
        assertEquals("\u0410\u043b\u043e, \u0430\u043b\u043e!", repository.getLessonById(2)?.title)
        assertTrue(repository.hasNextLesson(1))
        assertEquals(2, repository.getNextLessonId(1))
        assertFalse(repository.hasNextLesson(11))
        assertNull(repository.getNextLessonId(11))
        assertNull(repository.getLessonById(99))
    }
}
