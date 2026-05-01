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
class ArchitectureAlignmentSmokeTest {

    @Test
    fun lessonTitlesComeFromJsonBackedRepository() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = LessonRepository(context)

        assertEquals("Преговор A1", repository.getLessonById(1)?.title)
        assertEquals("Ало, ало!", repository.getLessonById(2)?.title)
    }
}
