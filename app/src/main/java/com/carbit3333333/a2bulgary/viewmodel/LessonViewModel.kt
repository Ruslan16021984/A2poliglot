package com.carbit3333333.a2bulgary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.carbit3333333.a2bulgary.data.LessonRepository
import com.carbit3333333.a2bulgary.ui.lessons.LessonUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LessonViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val repository = LessonRepository(application)

    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

    fun loadLesson(lessonId: Int) {
        val lesson = repository.getLessonById(lessonId)

        _uiState.value = if (lesson != null) {
            LessonUiState(lesson = lesson)
        } else {
            LessonUiState(errorMessage = "Урокът не е намерен.")
        }
    }

    companion object {
        fun provideFactory(
            application: Application,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LessonViewModel(application) as T
            }
        }
    }
}
