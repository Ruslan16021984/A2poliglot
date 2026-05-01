package com.carbit3333333.a2bulgary.navigation

object Destinations {
    const val LESSONS = "lessons"
    const val LESSON_DETAILS = "lesson_details"

    fun lessonDetailsRoute(lessonId: Int): String {
        return "$LESSON_DETAILS/$lessonId"
    }
}
