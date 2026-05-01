package com.carbit3333333.a2bulgary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.carbit3333333.a2bulgary.data.LessonRepository
import com.carbit3333333.a2bulgary.ui.lessons.LessonsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LessonsViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val repository = LessonRepository(application)

    private val _uiState = MutableStateFlow(LessonsUiState(isLoading = true))
    val uiState: StateFlow<LessonsUiState> = _uiState.asStateFlow()

    init {
        loadLessons()
    }

    private fun loadLessons() {
        _uiState.value = LessonsUiState(
            lessons = repository.getLessons(),
            isLoading = false,
        )
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
