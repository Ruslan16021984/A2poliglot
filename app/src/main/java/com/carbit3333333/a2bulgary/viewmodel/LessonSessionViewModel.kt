package com.carbit3333333.a2bulgary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.carbit3333333.a2bulgary.R
import com.carbit3333333.a2bulgary.data.LessonProgressStore
import com.carbit3333333.a2bulgary.data.LessonSessionRepository
import com.carbit3333333.a2bulgary.model.ExerciseResult
import com.carbit3333333.a2bulgary.model.LessonResult
import com.carbit3333333.a2bulgary.ui.lessons.LessonSessionUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LessonSessionViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val repository = LessonSessionRepository(application)
    private val progressStore = LessonProgressStore(application)
    private val resources = application.resources
    private var currentLessonId: Int = 0
    private val praises = listOf(
        R.string.lesson_session_praise_bravo,
        R.string.lesson_session_praise_super,
        R.string.lesson_session_praise_wonderful,
        R.string.lesson_session_praise_excellent,
        R.string.lesson_session_praise_talent,
    )

    private val _uiState = MutableStateFlow(LessonSessionUiState())
    val uiState: StateFlow<LessonSessionUiState> = _uiState.asStateFlow()

    fun loadLessonSession(lessonId: Int) {
        currentLessonId = lessonId
        runCatching {
            repository.getLessonSession(lessonId)
        }.onSuccess { session ->
            _uiState.value = LessonSessionUiState(
                lessonTitle = session.lessonTitle,
                exercises = session.exercises,
                results = List(session.exercises.size) { ExerciseResult.NONE },
            )
            viewModelScope.launch {
                progressStore.saveLessonProgress(
                    lessonId = lessonId,
                    currentStep = 0,
                    totalSteps = session.exercises.size,
                    currentScore = 0f,
                )
            }
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
            praiseText = if (isCorrect) resources.getString(praises.random()) else null,
        )

        if (isCorrect) {
            viewModelScope.launch {
                delay(1800)
                continueAfterAnswer()
            }
        }
    }

    fun continueAfterAnswer() {
        val state = _uiState.value
        if (state.currentResult == ExerciseResult.NONE) return

        val nextIndex = state.currentExerciseIndex + 1
        if (nextIndex >= state.exercises.size) {
            val totalExercises = state.exercises.size
            val score = if (totalExercises > 0) {
                (state.correctCount.toFloat() / totalExercises.toFloat()) * 5f
            } else {
                0f
            }
            val lessonResult = LessonResult(
                lessonId = currentLessonId,
                lessonTitle = state.lessonTitle,
                totalExercises = totalExercises,
                correctCount = state.correctCount,
                wrongCount = state.wrongCount,
                score = score,
                isPassed = score >= 4.5f,
            )

            _uiState.value = state.copy(
                selectedWords = emptyList(),
                currentResult = ExerciseResult.NONE,
                praiseText = null,
                isLessonFinished = true,
                lessonResult = lessonResult,
            )
            viewModelScope.launch {
                progressStore.saveLessonProgress(
                    lessonId = currentLessonId,
                    currentStep = totalExercises,
                    totalSteps = totalExercises,
                    currentScore = score,
                )
                progressStore.saveLessonResult(
                    lessonId = currentLessonId,
                    correctCount = state.correctCount,
                    wrongCount = state.wrongCount,
                    score = score,
                    isPassed = lessonResult.isPassed,
                )
                if (lessonResult.isPassed) {
                    progressStore.unlockNextLesson(currentLessonId + 1)
                }
            }
            return
        }

        _uiState.value = state.copy(
            currentExerciseIndex = nextIndex,
            selectedWords = emptyList(),
            currentResult = ExerciseResult.NONE,
            praiseText = null,
        )
        viewModelScope.launch {
            progressStore.saveLessonProgress(
                lessonId = currentLessonId,
                currentStep = nextIndex,
                totalSteps = state.exercises.size,
                currentScore = if (state.exercises.isNotEmpty()) {
                    (state.correctCount.toFloat() / state.exercises.size.toFloat()) * 5f
                } else {
                    0f
                },
            )
        }
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
