package com.carbit3333333.a2bulgary

import com.carbit3333333.a2bulgary.model.Lesson
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LessonJsonAssetsTest {

    private val json = Json {
        ignoreUnknownKeys = false
    }

    @Test
    fun lessonsAssetDefinesExpectedA2CourseStructure() {
        val assetStream = checkNotNull(javaClass.classLoader?.getResourceAsStream("assets/lessons_ru.json")) {
            "Expected lessons_ru.json in test resources"
        }
        val lessons = assetStream.bufferedReader(Charsets.UTF_8).use { reader ->
            json.decodeFromString<List<Lesson>>(reader.readText())
        }

        assertEquals(11, lessons.size)
        assertEquals((1..11).toList(), lessons.map(Lesson::id))
        assertEquals("Преговор A1", lessons.first().title)
        assertTrue(lessons.any { it.title == "Ало, ало!" })
        assertTrue(lessons.any { it.title == "Интервю за работа" })
        assertTrue(lessons.drop(1).all { it.theory.size >= 3 })
    }
}
