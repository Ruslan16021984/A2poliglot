package com.carbit3333333.a2bulgary

import com.carbit3333333.a2bulgary.navigation.Destinations
import org.junit.Assert.assertEquals
import org.junit.Test

class DestinationsTest {

    @Test
    fun lessonDetailsRouteEmbedsLessonId() {
        assertEquals("lesson_details/2", Destinations.lessonDetailsRoute(2))
    }
}
