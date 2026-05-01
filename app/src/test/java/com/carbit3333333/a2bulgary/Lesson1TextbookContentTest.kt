package com.carbit3333333.a2bulgary

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.carbit3333333.a2bulgary.data.LessonRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Lesson1TextbookContentTest {

    @Test
    fun firstA2TopicKeepsPhoneCallContent() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = LessonRepository(context)
        val lesson = repository.getLessonById(2)

        assertEquals("Ало, ало!", lesson?.title)
        assertEquals("Телефонни разговори и уговорка на среща", lesson?.subtitle)
        assertEquals("Телефонни разговори", lesson?.theory?.first()?.title)
    }
}
