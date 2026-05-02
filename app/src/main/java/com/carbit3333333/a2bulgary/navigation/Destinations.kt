package com.carbit3333333.a2bulgary.navigation

import android.net.Uri

object Destinations {
    const val LESSONS = "lessons"
    const val SETTINGS = "settings"
    const val DICTIONARY_LIST = "dictionary"
    const val DICTIONARY_EDIT = "dictionary_edit?wordId={wordId}"
    const val DICTIONARY_TRAINING = "dictionary_training?groupId={groupId}&groupName={groupName}"
    const val LESSON_DETAILS = "lesson_details"
    const val LESSON_SESSION = "lesson_session"
    const val LESSON_RESULT = "lesson_result"

    private const val WORD_ID_ARGUMENT = "wordId"
    private const val GROUP_ID_ARGUMENT = "groupId"
    private const val GROUP_NAME_ARGUMENT = "groupName"

    fun lessonDetailsRoute(lessonId: Int): String {
        return "$LESSON_DETAILS/$lessonId"
    }

    fun lessonSessionRoute(lessonId: Int): String {
        return "$LESSON_SESSION/$lessonId"
    }

    fun lessonResultRoute(
        lessonId: Int,
        correctCount: Int,
        wrongCount: Int,
    ): String {
        return "$LESSON_RESULT/$lessonId/$correctCount/$wrongCount"
    }

    fun dictionaryEditRoute(wordId: Long? = null): String {
        val routeWordId = wordId?.takeIf { it > 0L } ?: -1L
        return "dictionary_edit?$WORD_ID_ARGUMENT=$routeWordId"
    }

    fun dictionaryTrainingRoute(
        groupId: Long? = null,
        groupName: String? = null,
    ): String {
        val routeGroupId = groupId?.takeIf { it > 0L } ?: -1L
        val routeGroupName = Uri.encode(groupName.orEmpty())
        return "dictionary_training?$GROUP_ID_ARGUMENT=$routeGroupId&$GROUP_NAME_ARGUMENT=$routeGroupName"
    }
}
