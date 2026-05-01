package com.carbit3333333.a2bulgary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.carbit3333333.a2bulgary.data.LessonSessionRepository
import com.carbit3333333.a2bulgary.model.ExerciseResult
import com.carbit3333333.a2bulgary.ui.lessons.LessonSessionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LessonSessionViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val repository = LessonSessionRepository(application)

    private val _uiState = MutableStateFlow(LessonSessionUiState())
    val uiState: StateFlow<LessonSessionUiState> = _uiState.asStateFlow()

    fun loadLessonSession(lessonId: Int) {
        runCatching {
            repository.getLessonSession(lessonId)
        }.onSuccess { session ->
            _uiState.value = LessonSessionUiState(
                lessonTitle = session.lessonTitle,
                exercises = session.exercises,
                results = List(session.exercises.size) { ExerciseResult.NONE },
            )
        }.onFailure {
            _uiState.value = LessonSessionUiState(
                errorMessage = "Упражнения для урока пока недоступны.",
            )
        }
    }

    fun selectWord(word: String) {
        val state = _uiState.value
        if (state.currentResult != ExerciseResult.NONE) return
        _uiState.value = state.copy(selectedWords = state.selectedWords + word)
    }

    fun removeSelectedWord(word: String) {
        val state = _uiState.value
        if (state.currentResult != ExerciseResult.NONE) return

        val updated = state.selectedWords.toMutableList()
        updated.remove(word)
        _uiState.value = state.copy(selectedWords = updated)
    }

    fun checkAnswer() {
        val state = _uiState.value
        val exercise = state.currentExercise ?: return

        val isCorrect = state.selectedWords == exercise.correctAnswerWords
        val newResults = state.results.toMutableList()
        newResults[state.currentExerciseIndex] = if (isCorrect) {
            ExerciseResult.CORRECT
        } else {
            ExerciseResult.WRONG
        }

        _uiState.value = state.copy(
            results = newResults,
            currentResult = if (isCorrect) ExerciseResult.CORRECT else ExerciseResult.WRONG,
            correctCount = state.correctCount + if (isCorrect) 1 else 0,
            wrongCount = state.wrongCount + if (isCorrect) 0 else 1,
        )
    }

    fun continueAfterAnswer() {
        val state = _uiState.value
        if (state.currentResult == ExerciseResult.NONE) return

        val nextIndex = state.currentExerciseIndex + 1
        if (nextIndex >= state.exercises.size) {
            _uiState.value = state.copy(
                selectedWords = emptyList(),
                currentResult = ExerciseResult.NONE,
                isLessonFinished = true,
            )
            return
        }

        _uiState.value = state.copy(
            currentExerciseIndex = nextIndex,
            selectedWords = emptyList(),
            currentResult = ExerciseResult.NONE,
        )
    }

    companion object {
        fun provideFactory(
            application: Application,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LessonSessionViewModel(application) as T
            }
        }
    }
}
