package com.carbit3333333.a2bulgary.model.dictionary

data class DictionaryWordListItem(
    val id: Long,
    val bgWord: String,
    val ruTranslation: String,
    val isBuiltIn: Boolean = false,
    val sourceLessonNumber: Int? = null,
)
