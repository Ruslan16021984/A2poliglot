package com.carbit3333333.a2bulgary

import androidx.test.core.app.ApplicationProvider
import com.carbit3333333.a2bulgary.model.Lesson
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LessonJsonAssetsTest {

    private val json = Json {
        ignoreUnknownKeys = false
    }

    @Test
    fun lessonsAssetDefinesExpectedA2CourseStructure() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val lessons = context.assets.open("lessons_ru.json").bufferedReader(Charsets.UTF_8).use { reader ->
            json.decodeFromString<List<Lesson>>(reader.readText())
        }

        assertEquals(11, lessons.size)
        assertEquals((1..11).toList(), lessons.map(Lesson::id))
        assertEquals("Преговор A1", lessons.first().title)
        assertTrue(lessons.any { it.title == "Ало, ало!" })
        assertTrue(lessons.any { it.title == "Интервю за работа" })
        assertTrue(lessons.all { it.title.isNotBlank() && it.subtitle.isNotBlank() })
        assertTrue(lessons.all { it.theory.size >= 3 })
        assertTrue(lessons.all { lesson ->
            lesson.theory.all { block ->
                block.title.isNotBlank() && block.text.isNotBlank()
            }
        })
    }
}
