package com.carbit3333333.a2bulgary.ui.dictionary

import com.carbit3333333.a2bulgary.model.dictionary.DictionaryWordListItem
import com.carbit3333333.a2bulgary.model.dictionary.WordGroup

data class DictionaryListUiState(
    val isLoading: Boolean = false,
    val query: String = "",
    val selectedGroupId: Long? = null,
    val words: List<DictionaryWordListItem> = emptyList(),
    val visibleWords: List<DictionaryWordListItem> = emptyList(),
    val totalWordsCount: Int = 0,
    val canLoadMore: Boolean = false,
    val groups: List<WordGroup> = emptyList(),
    val errorMessage: String? = null,
)
