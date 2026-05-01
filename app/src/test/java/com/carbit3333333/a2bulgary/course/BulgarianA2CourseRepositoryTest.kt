package com.carbit3333333.a2bulgary.course

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BulgarianA2CourseRepositoryTest {

    @Test
    fun exposesReviewBlockAndTenLessons() {
        val units = BulgarianA2CourseRepository.units

        assertEquals(11, units.size)
        assertEquals("Преговор A1", units.first().title)
        assertEquals("Ало, ало!", units[1].title)
        assertTrue(units.any { it.title == "Интервю за работа" })
    }

    @Test
    fun eachUnitProvidesAtLeastOneGoalAndOneGrammarPoint() {
        assertTrue(BulgarianA2CourseRepository.units.all { unit ->
            unit.goals.isNotEmpty() && unit.grammar.isNotEmpty()
        })
    }

    @Test
    fun lessonTitlesStayDistinctAcrossTheCourse() {
        val titles = BulgarianA2CourseRepository.units.map { it.title }

        assertEquals(titles.size, titles.distinct().size)
    }

    @Test
    fun firstLessonProvidesStudyContentForDetailScreen() {
        val lesson = BulgarianA2CourseRepository.units.first { it.title == "Ало, ало!" }

        assertEquals("Обаждам се за срещата утре.", lesson.sampleDialogue)
        assertTrue(lesson.keyPhrases.size >= 3)
        assertTrue(lesson.practiceTasks.size >= 3)
        assertFalse(lesson.checkpoints.isEmpty())
    }
}
