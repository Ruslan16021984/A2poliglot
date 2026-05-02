package com.carbit3333333.a2bulgary.data.dictionary

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.carbit3333333.a2bulgary.ui.dictionary.FlashcardDirection
import kotlinx.coroutines.flow.first

private val Context.flashcardTrainingPreferencesDataStore by preferencesDataStore(
    name = "flashcard_training_preferences",
)

class FlashcardTrainingPreferencesStore(
    context: Context,
) {
    private val appContext = context.applicationContext

    suspend fun loadDirection(): FlashcardDirection {
        val preferences = appContext.flashcardTrainingPreferencesDataStore.data.first()
        return when (preferences[Keys.DIRECTION]) {
            FlashcardDirection.RuToBg.name -> FlashcardDirection.RuToBg
            else -> FlashcardDirection.BgToRu
        }
    }

    suspend fun saveDirection(direction: FlashcardDirection) {
        appContext.flashcardTrainingPreferencesDataStore.edit { preferences ->
            preferences[Keys.DIRECTION] = direction.name
        }
    }

    private object Keys {
        val DIRECTION = stringPreferencesKey("direction")
    }
}
