package com.carbit3333333.a2bulgary.model.dictionary

data class WordCard(
    val id: Long = 0,
    val bgWord: String,
    val ruTranslation: String,
    val groupIds: List<Long> = emptyList(),
)
