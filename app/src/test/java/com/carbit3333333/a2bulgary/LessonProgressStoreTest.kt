package com.carbit3333333.a2bulgary

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.carbit3333333.a2bulgary.data.LessonProgressStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LessonProgressStoreTest {

    @Test
    fun savesPassedResultAndUnlocksNextLesson() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LessonProgressStore(context)

        store.resetLessonUnlocks(maxLessonId = 11)
        store.saveLessonResult(
            lessonId = 2,
            correctCount = 3,
            wrongCount = 1,
            score = 4.6f,
            isPassed = true,
        )
        store.unlockNextLesson(3)

        val openedLessonId = store.openedLessonIdFlow.first()
        val savedResults = store.getLessonResultsFlow(listOf(2)).first()
        val lesson2 = savedResults[2]

        assertEquals(3, openedLessonId)
        assertNotNull(lesson2)
        assertEquals(4.6f, lesson2?.bestScore)
        assertEquals(3, lesson2?.bestCorrectCount)
        assertTrue(lesson2?.isPassed == true)
    }
}
