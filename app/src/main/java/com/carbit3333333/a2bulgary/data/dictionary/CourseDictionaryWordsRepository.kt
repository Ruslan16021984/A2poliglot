package com.carbit3333333.a2bulgary.data.dictionary

import android.content.Context
import com.carbit3333333.a2bulgary.model.dictionary.DictionaryWordListItem
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class CourseDictionaryWordsRepository(
    private val context: Context,
) {
    companion object {
        const val COURSE_GROUP_ID: Long = -1L
        private const val COURSE_WORDS_ASSET = "course_dictionary_words.json"
    }

    private val json = Json { ignoreUnknownKeys = true }

    fun loadWords(): List<DictionaryWordListItem> {
        return runCatching {
            context.assets.open(COURSE_WORDS_ASSET).use { input ->
                val decoded = json.decodeFromString<List<BuiltInDictionaryWordAsset>>(
                    input.bufferedReader(Charsets.UTF_8).readText()
                )
                decoded.mapIndexed { index, word ->
                    DictionaryWordListItem(
                        id = -(index + 1L),
                        bgWord = word.bgWord.trim(),
                        ruTranslation = word.ruTranslation.trim(),
                        isBuiltIn = true,
                        sourceLessonNumber = word.lessonNumber,
                    )
                }
            }
        }.getOrDefault(emptyList())
    }
}
