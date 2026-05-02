package com.carbit3333333.a2bulgary.data.dictionary

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.personalDictionaryDataStore by preferencesDataStore(name = "personal_dictionary")

class PersonalDictionaryStore(
    context: Context,
) {
    private val appContext = context.applicationContext
    private val json = Json { ignoreUnknownKeys = true }

    val stateFlow: Flow<PersonalDictionaryState> =
        appContext.personalDictionaryDataStore.data.map { preferences ->
            val rawState = preferences[Keys.STATE]
            val nextWordId = preferences[Keys.NEXT_WORD_ID] ?: 1L
            val nextGroupId = preferences[Keys.NEXT_GROUP_ID] ?: 1L
            val decoded = rawState?.let {
                runCatching { json.decodeFromString<PersonalDictionaryState>(it) }.getOrNull()
            } ?: PersonalDictionaryState()

            decoded.copy(
                nextWordId = maxOf(decoded.nextWordId, nextWordId),
                nextGroupId = maxOf(decoded.nextGroupId, nextGroupId),
            )
        }

    suspend fun update(transform: (PersonalDictionaryState) -> PersonalDictionaryState) {
        appContext.personalDictionaryDataStore.edit { preferences ->
            val current = preferences[Keys.STATE]?.let {
                runCatching { json.decodeFromString<PersonalDictionaryState>(it) }.getOrNull()
            } ?: PersonalDictionaryState()
            val nextState = transform(current)
            preferences[Keys.STATE] = json.encodeToString(nextState)
            preferences[Keys.NEXT_WORD_ID] = nextState.nextWordId
            preferences[Keys.NEXT_GROUP_ID] = nextState.nextGroupId
        }
    }

    @Serializable
    data class PersonalDictionaryState(
        val words: List<StoredWordCard> = emptyList(),
        val groups: List<StoredWordGroup> = emptyList(),
        val nextWordId: Long = 1L,
        val nextGroupId: Long = 1L,
    )

    @Serializable
    data class StoredWordCard(
        val id: Long,
        val bgWord: String,
        val ruTranslation: String,
        val groupIds: List<Long> = emptyList(),
        val createdAt: Long,
        val updatedAt: Long,
    )

    @Serializable
    data class StoredWordGroup(
        val id: Long,
        val name: String,
    )

    private object Keys {
        val STATE = stringPreferencesKey("state")
        val NEXT_WORD_ID = longPreferencesKey("next_word_id")
        val NEXT_GROUP_ID = longPreferencesKey("next_group_id")
    }
}
