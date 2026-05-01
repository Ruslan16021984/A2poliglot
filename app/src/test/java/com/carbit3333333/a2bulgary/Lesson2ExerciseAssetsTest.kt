package com.carbit3333333.a2bulgary

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.carbit3333333.a2bulgary.data.lesson_session.TextbookLessonExerciseSetAsset
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Lesson2ExerciseAssetsTest {

    @Test
    fun lesson2ExercisesAssetHasExpectedStructure() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val json = Json { ignoreUnknownKeys = true }
        val asset = context.assets.open("textbook_exercises_lesson2.json")
            .bufferedReader(Charsets.UTF_8)
            .use { json.decodeFromString<TextbookLessonExerciseSetAsset>(it.readText()) }

        assertEquals(2, asset.lessonApp)
        assertEquals("Ало, ало!", asset.title)
        assertTrue(asset.items.size >= 3)
        assertTrue(asset.items.all { it.bg.isNotBlank() && it.ru.isNotBlank() })
        assertTrue(asset.items.all { it.correctWords.isNotEmpty() })
    }
}
