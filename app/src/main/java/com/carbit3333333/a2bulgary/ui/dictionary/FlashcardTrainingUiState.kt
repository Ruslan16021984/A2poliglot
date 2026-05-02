package com.carbit3333333.a2bulgary.ui.dictionary

import com.carbit3333333.a2bulgary.model.dictionary.FlashcardItem

enum class FlashcardFace {
    Front,
    Back,
}

enum class FlashcardDirection {
    BgToRu,
    RuToBg,
}

data class FlashcardTrainingUiState(
    val isLoading: Boolean = false,
    val cards: List<FlashcardItem> = emptyList(),
    val currentIndex: Int = 0,
    val currentCardFace: FlashcardFace = FlashcardFace.Front,
    val direction: FlashcardDirection = FlashcardDirection.BgToRu,
    val knownCount: Int = 0,
    val unknownCount: Int = 0,
    val unknownCards: List<FlashcardItem> = emptyList(),
    val isFinished: Boolean = false,
    val groupName: String? = null,
    val errorMessage: String? = null,
) {
    val currentCard: FlashcardItem?
        get() = cards.getOrNull(currentIndex)

    val totalCount: Int
        get() = cards.size

    val hasUnknownCards: Boolean
        get() = unknownCards.isNotEmpty()

    val progressText: String
        get() = when {
            totalCount == 0 -> "0 / 0"
            isFinished -> "$totalCount / $totalCount"
            else -> "${currentIndex + 1} / $totalCount"
        }
}
