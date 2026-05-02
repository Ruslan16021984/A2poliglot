package com.carbit3333333.a2bulgary.data.dictionary

import android.content.Context
import com.carbit3333333.a2bulgary.R
import com.carbit3333333.a2bulgary.model.dictionary.DictionaryWordListItem
import com.carbit3333333.a2bulgary.model.dictionary.FlashcardItem
import com.carbit3333333.a2bulgary.model.dictionary.WordCard
import com.carbit3333333.a2bulgary.model.dictionary.WordGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PersonalDictionaryRepository(
    context: Context,
) {
    private val appContext = context.applicationContext
    private val store = PersonalDictionaryStore(appContext)
    private val courseWordsRepository = CourseDictionaryWordsRepository(appContext)
    private val builtInWords = courseWordsRepository.loadWords()

    fun observeAllWords(): Flow<List<DictionaryWordListItem>> {
        return observeFilteredWords(query = "", groupId = null)
    }

    fun observeFilteredWords(
        query: String,
        groupId: Long? = null,
    ): Flow<List<DictionaryWordListItem>> {
        val normalizedQuery = query.trim()
        return store.stateFlow.map { state ->
            val userWords = if (groupId == CourseDictionaryWordsRepository.COURSE_GROUP_ID) {
                emptyList()
            } else {
                state.words
                    .filter { word ->
                        (groupId == null || groupId in word.groupIds) &&
                            word.matchesQuery(normalizedQuery)
                    }
                    .sortedWith(compareByDescending<PersonalDictionaryStore.StoredWordCard> { it.updatedAt }.thenByDescending { it.id })
                    .map { word ->
                        DictionaryWordListItem(
                            id = word.id,
                            bgWord = word.bgWord,
                            ruTranslation = word.ruTranslation,
                        )
                    }
            }

            val courseWords = if (groupId == null || groupId == CourseDictionaryWordsRepository.COURSE_GROUP_ID) {
                builtInWords.filter { it.matchesQuery(normalizedQuery) }
            } else {
                emptyList()
            }

            mergeWords(courseWords, userWords)
        }
    }

    fun observeGroupsWithCounts(): Flow<List<WordGroup>> {
        return store.stateFlow.map { state ->
            listOf(
                WordGroup(
                    id = CourseDictionaryWordsRepository.COURSE_GROUP_ID,
                    name = appContext.getString(R.string.dictionary_course_group_name),
                    wordCount = builtInWords.size.toLong(),
                )
            ) + state.groups
                .map { group ->
                    WordGroup(
                        id = group.id,
                        name = group.name,
                        wordCount = state.words.count { group.id in it.groupIds }.toLong(),
                    )
                }
                .sortedBy { it.name.lowercase() }
        }
    }

    suspend fun getWordById(wordId: Long): WordCard? {
        if (wordId <= 0L) return null
        val state = store.stateFlow.first()
        return state.words.firstOrNull { it.id == wordId }?.let { word ->
            WordCard(
                id = word.id,
                bgWord = word.bgWord,
                ruTranslation = word.ruTranslation,
                groupIds = word.groupIds.sorted(),
            )
        }
    }

    suspend fun saveWord(word: WordCard): Long {
        val normalizedBgWord = word.bgWord.trim()
        val normalizedRuTranslation = word.ruTranslation.trim()
        require(normalizedBgWord.isNotEmpty()) { "bgWord must not be blank" }
        require(normalizedRuTranslation.isNotEmpty()) { "ruTranslation must not be blank" }

        var savedWordId = 0L
        store.update { state ->
            val now = System.currentTimeMillis()
            val existing = state.words.firstOrNull { it.id == word.id && word.id > 0L }
            val nextId = if (existing == null) state.nextWordId else word.id
            savedWordId = nextId

            val savedWord = PersonalDictionaryStore.StoredWordCard(
                id = nextId,
                bgWord = normalizedBgWord,
                ruTranslation = normalizedRuTranslation,
                groupIds = word.groupIds.distinct().filter { it > 0L }.sorted(),
                createdAt = existing?.createdAt ?: now,
                updatedAt = now,
            )

            val updatedWords = if (existing == null) {
                state.words + savedWord
            } else {
                state.words.map { current -> if (current.id == nextId) savedWord else current }
            }

            state.copy(
                words = updatedWords,
                nextWordId = maxOf(state.nextWordId, nextId + 1),
            )
        }
        return savedWordId
    }

    suspend fun createGroup(name: String): Long {
        val normalizedName = name.trim()
        if (normalizedName.isEmpty()) return 0L

        var createdGroupId = 0L
        store.update { state ->
            val exists = state.groups.any { it.name.equals(normalizedName, ignoreCase = true) }
            if (exists) {
                createdGroupId = 0L
                state
            } else {
                val nextId = state.nextGroupId
                createdGroupId = nextId
                state.copy(
                    groups = state.groups + PersonalDictionaryStore.StoredWordGroup(
                        id = nextId,
                        name = normalizedName,
                    ),
                    nextGroupId = nextId + 1,
                )
            }
        }
        return createdGroupId
    }

    suspend fun deleteWord(wordId: Long) {
        if (wordId <= 0L) return
        store.update { state ->
            state.copy(words = state.words.filterNot { it.id == wordId })
        }
    }

    suspend fun loadFlashcardsForAllWords(): List<FlashcardItem> {
        val state = store.stateFlow.first()
        val userWords = state.words.map { it.toFlashcardItem() }
        return mergeFlashcards(
            builtIn = builtInWords.map { it.toFlashcardItem() },
            user = userWords,
        )
    }

    suspend fun loadFlashcardsForOneGroup(groupId: Long): List<FlashcardItem> {
        return if (groupId == CourseDictionaryWordsRepository.COURSE_GROUP_ID) {
            builtInWords.map { it.toFlashcardItem() }
        } else {
            val state = store.stateFlow.first()
            state.words
                .filter { groupId in it.groupIds }
                .map { it.toFlashcardItem() }
                .sortedBy { it.bgWord.lowercase() }
        }
    }

    private fun PersonalDictionaryStore.StoredWordCard.matchesQuery(query: String): Boolean {
        if (query.isBlank()) return true
        val normalizedQuery = query.lowercase()
        return bgWord.lowercase().contains(normalizedQuery) ||
            ruTranslation.lowercase().contains(normalizedQuery)
    }

    private fun DictionaryWordListItem.matchesQuery(query: String): Boolean {
        if (query.isBlank()) return true
        val normalizedQuery = query.lowercase()
        return bgWord.lowercase().contains(normalizedQuery) ||
            ruTranslation.lowercase().contains(normalizedQuery)
    }

    private fun mergeWords(
        builtIn: List<DictionaryWordListItem>,
        user: List<DictionaryWordListItem>,
    ): List<DictionaryWordListItem> {
        val merged = LinkedHashMap<String, DictionaryWordListItem>()
        builtIn.forEach { word -> merged[word.mergeKey()] = word }
        user.forEach { word -> merged[word.mergeKey()] = word }
        return merged.values.sortedBy { it.bgWord.lowercase() }
    }

    private fun mergeFlashcards(
        builtIn: List<FlashcardItem>,
        user: List<FlashcardItem>,
    ): List<FlashcardItem> {
        val merged = LinkedHashMap<String, FlashcardItem>()
        builtIn.forEach { word -> merged[word.mergeKey()] = word }
        user.forEach { word -> merged[word.mergeKey()] = word }
        return merged.values.sortedBy { it.bgWord.lowercase() }
    }

    private fun DictionaryWordListItem.mergeKey(): String {
        return "${bgWord.lowercase()}|${ruTranslation.lowercase()}"
    }

    private fun FlashcardItem.mergeKey(): String {
        return "${bgWord.lowercase()}|${ruTranslation.lowercase()}"
    }

    private fun DictionaryWordListItem.toFlashcardItem(): FlashcardItem {
        return FlashcardItem(id = id, bgWord = bgWord, ruTranslation = ruTranslation)
    }

    private fun PersonalDictionaryStore.StoredWordCard.toFlashcardItem(): FlashcardItem {
        return FlashcardItem(id = id, bgWord = bgWord, ruTranslation = ruTranslation)
    }
}
