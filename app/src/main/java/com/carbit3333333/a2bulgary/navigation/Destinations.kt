package com.carbit3333333.a2bulgary.navigation

object Destinations {
    const val LESSONS = "lessons"
    const val LESSON_DETAILS = "lesson_details"
    const val LESSON_SESSION = "lesson_session"

    fun lessonDetailsRoute(lessonId: Int): String {
        return "$LESSON_DETAILS/$lessonId"
    }

    fun lessonSessionRoute(lessonId: Int): String {
        return "$LESSON_SESSION/$lessonId"
    }
}
