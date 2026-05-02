package com.carbit3333333.a2bulgary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.carbit3333333.a2bulgary.data.LessonProgressStore
import com.carbit3333333.a2bulgary.data.LessonRepository
import com.carbit3333333.a2bulgary.ui.lessons.LessonsUiState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LessonsViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val repository = LessonRepository(application)
    private val progressStore = LessonProgressStore(application)

    private val _uiState = MutableStateFlow(LessonsUiState(isLoading = true))
    val uiState: StateFlow<LessonsUiState> = _uiState.asStateFlow()

    init {
        observeLessons()
    }

    private fun observeLessons() {
        viewModelScope.launch {
            val lessonIds = repository.getLessons().map { it.id }

            combine(
                progressStore.openedLessonIdFlow,
                progressStore.getLessonResultsFlow(lessonIds),
            ) { openedLessonId, savedResults ->
                val lessons = repository.getLessons().map { lesson ->
                    val savedResult = savedResults[lesson.id]
                    lesson.copy(
                        isLocked = lesson.id > openedLessonId,
                        isCompleted = savedResult?.isPassed == true,
                        bestScore = savedResult?.bestScore,
                        currentScore = savedResult?.currentScore,
                        currentProgress = savedResult?.currentStep ?: 0,
                        totalProgress = savedResult?.totalSteps ?: 0,
                    )
                }

                LessonsUiState(
                    lessons = lessons,
                    isLoading = false,
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    companion object {
        fun provideFactory(
            application: Application,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LessonsViewModel(application) as T
            }
        }
    }
}
