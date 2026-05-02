package com.carbit3333333.a2bulgary.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.carbit3333333.a2bulgary.model.SavedLessonResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.lessonProgressDataStore by preferencesDataStore(name = "lesson_progress")

class LessonProgressStore(
    private val context: Context,
) {
    companion object {
        private val OPENED_LESSON_ID = intPreferencesKey("opened_lesson_id")
    }

    val openedLessonIdFlow: Flow<Int> = context.lessonProgressDataStore.data.map { preferences ->
        preferences[OPENED_LESSON_ID] ?: 1
    }

    suspend fun unlockNextLesson(nextLessonId: Int) {
        context.lessonProgressDataStore.edit { preferences ->
            val currentOpened = preferences[OPENED_LESSON_ID] ?: 1
            if (nextLessonId > currentOpened) {
                preferences[OPENED_LESSON_ID] = nextLessonId
            }
        }
    }

    suspend fun resetLessonUnlocks(maxLessonId: Int = 20) {
        context.lessonProgressDataStore.edit { preferences ->
            preferences[OPENED_LESSON_ID] = 1

            for (lessonId in 1..maxLessonId) {
                preferences.remove(intPreferencesKey("lesson_${lessonId}_best_correct"))
                preferences.remove(intPreferencesKey("lesson_${lessonId}_best_wrong"))
                preferences.remove(floatPreferencesKey("lesson_${lessonId}_best_score"))
                preferences.remove(floatPreferencesKey("lesson_${lessonId}_current_score"))
                preferences.remove(booleanPreferencesKey("lesson_${lessonId}_is_passed"))
                preferences.remove(intPreferencesKey("lesson_${lessonId}_current_step"))
                preferences.remove(intPreferencesKey("lesson_${lessonId}_total_steps"))
            }
        }
    }

    suspend fun saveLessonProgress(
        lessonId: Int,
        currentStep: Int,
        totalSteps: Int,
        currentScore: Float? = null,
    ) {
        context.lessonProgressDataStore.edit { preferences ->
            preferences[intPreferencesKey("lesson_${lessonId}_current_step")] = currentStep
            preferences[intPreferencesKey("lesson_${lessonId}_total_steps")] = totalSteps
            if (currentScore != null) {
                preferences[floatPreferencesKey("lesson_${lessonId}_current_score")] = currentScore
            }
        }
    }

    suspend fun saveLessonResult(
        lessonId: Int,
        correctCount: Int,
        wrongCount: Int,
        score: Float,
        isPassed: Boolean,
    ) {
        context.lessonProgressDataStore.edit { preferences ->
            val bestScoreKey = floatPreferencesKey("lesson_${lessonId}_best_score")
            val oldBestScore = preferences[bestScoreKey] ?: 0f

            if (score >= oldBestScore) {
                preferences[intPreferencesKey("lesson_${lessonId}_best_correct")] = correctCount
                preferences[intPreferencesKey("lesson_${lessonId}_best_wrong")] = wrongCount
                preferences[bestScoreKey] = score
            }

            preferences[booleanPreferencesKey("lesson_${lessonId}_is_passed")] = isPassed
        }
    }

    fun getLessonResultsFlow(lessonIds: List<Int>): Flow<Map<Int, SavedLessonResult>> {
        return context.lessonProgressDataStore.data.map { preferences ->
            lessonIds.mapNotNull { lessonId ->
                val bestScore = preferences[floatPreferencesKey("lesson_${lessonId}_best_score")]
                val currentScore = preferences[floatPreferencesKey("lesson_${lessonId}_current_score")]
                val currentStep = preferences[intPreferencesKey("lesson_${lessonId}_current_step")] ?: 0
                val totalSteps = preferences[intPreferencesKey("lesson_${lessonId}_total_steps")] ?: 0
                val isPassed = preferences[booleanPreferencesKey("lesson_${lessonId}_is_passed")] ?: false
                val bestCorrect = preferences[intPreferencesKey("lesson_${lessonId}_best_correct")] ?: 0
                val bestWrong = preferences[intPreferencesKey("lesson_${lessonId}_best_wrong")] ?: 0

                val hasAnyData = bestScore != null || currentScore != null || currentStep > 0 || totalSteps > 0 || isPassed
                if (!hasAnyData) return@mapNotNull null

                lessonId to SavedLessonResult(
                    lessonId = lessonId,
                    bestCorrectCount = bestCorrect,
                    bestWrongCount = bestWrong,
                    bestScore = bestScore,
                    currentScore = currentScore,
                    isPassed = isPassed,
                    currentStep = currentStep,
                    totalSteps = totalSteps,
                )
            }.toMap()
        }
    }
}
