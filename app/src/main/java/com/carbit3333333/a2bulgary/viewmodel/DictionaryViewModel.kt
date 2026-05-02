package com.carbit3333333.a2bulgary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.carbit3333333.a2bulgary.R
import com.carbit3333333.a2bulgary.data.dictionary.PersonalDictionaryRepository
import com.carbit3333333.a2bulgary.model.dictionary.DictionaryWordListItem
import com.carbit3333333.a2bulgary.model.dictionary.WordGroup
import com.carbit3333333.a2bulgary.ui.dictionary.DictionaryListUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DictionaryViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private companion object {
        const val PAGE_SIZE = 20
    }

    private val repository = PersonalDictionaryRepository(application)
    private val resources = application.resources

    private val queryState = MutableStateFlow("")
    private val selectedGroupIdState = MutableStateFlow<Long?>(null)
    private val visibleWordsLimitState = MutableStateFlow(PAGE_SIZE)
    private val errorMessageState = MutableStateFlow<String?>(null)

    private val filteredWords: StateFlow<List<DictionaryWordListItem>> =
        combine(queryState, selectedGroupIdState) { query, selectedGroupId ->
            query.trim() to selectedGroupId
        }.flatMapLatest { (query, selectedGroupId) ->
            repository.observeFilteredWords(query = query, groupId = selectedGroupId).catch {
                errorMessageState.value =
                    it.message ?: resources.getString(R.string.dictionary_error_load_words)
                emit(emptyList())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    private val visibleWordsUiState: StateFlow<VisibleWordsUiState> =
        combine(filteredWords, visibleWordsLimitState) { words, visibleWordsLimit ->
            val visibleWords = words.take(visibleWordsLimit)
            VisibleWordsUiState(
                allWords = words,
                visibleWords = visibleWords,
                canLoadMore = visibleWords.size < words.size,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VisibleWordsUiState(),
        )

    val groups: StateFlow<List<WordGroup>> =
        repository.observeGroupsWithCounts()
            .catch {
                errorMessageState.value =
                    it.message ?: resources.getString(R.string.dictionary_error_load_groups)
                emit(emptyList())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )

    val hasAnyWordsForTraining: StateFlow<Boolean> =
        repository.observeAllWords()
            .map { words -> words.isNotEmpty() }
            .catch {
                errorMessageState.value =
                    it.message ?: resources.getString(R.string.dictionary_error_load_words)
                emit(false)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false,
            )

    val uiState: StateFlow<DictionaryListUiState> = combine(
        queryState,
        selectedGroupIdState,
        visibleWordsUiState,
        groups,
        errorMessageState,
    ) { query, selectedGroupId, visibleWordsState, groups, errorMessage ->
        DictionaryListUiState(
            isLoading = false,
            query = query,
            selectedGroupId = selectedGroupId,
            words = visibleWordsState.allWords,
            visibleWords = visibleWordsState.visibleWords,
            totalWordsCount = visibleWordsState.allWords.size,
            canLoadMore = visibleWordsState.canLoadMore,
            groups = groups,
            errorMessage = errorMessage,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DictionaryListUiState(isLoading = true),
    )

    fun updateQuery(query: String) {
        queryState.value = query
        visibleWordsLimitState.value = PAGE_SIZE
    }

    fun selectGroup(groupId: Long?) {
        selectedGroupIdState.value = if (selectedGroupIdState.value == groupId) null else groupId
        visibleWordsLimitState.value = PAGE_SIZE
    }

    fun loadMoreWords() {
        visibleWordsLimitState.value += PAGE_SIZE
    }

    fun clearError() {
        errorMessageState.value = null
    }

    fun deleteWord(wordId: Long) {
        viewModelScope.launch {
            runCatching {
                repository.deleteWord(wordId)
            }.onFailure { throwable ->
                errorMessageState.value =
                    throwable.message ?: resources.getString(R.string.dictionary_error_delete_word)
            }
        }
    }

    private data class VisibleWordsUiState(
        val allWords: List<DictionaryWordListItem> = emptyList(),
        val visibleWords: List<DictionaryWordListItem> = emptyList(),
        val canLoadMore: Boolean = false,
    )
}
