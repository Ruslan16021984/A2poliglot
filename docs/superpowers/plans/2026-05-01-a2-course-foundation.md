# A2 Course Foundation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the starter screen with a real A2 course overview backed by textbook-derived lesson data.

**Architecture:** Add a small in-memory course repository and a focused Compose screen that reads from it. Keep data and presentation separate so later lesson details and exercises can build on the same contract.

**Tech Stack:** Kotlin, Jetpack Compose Material 3, JUnit4

---

### Task 1: Seed A2 Course Data

**Files:**
- Create: `app/src/main/java/com/carbit3333333/a2bulgary/course/CourseModels.kt`
- Create: `app/src/main/java/com/carbit3333333/a2bulgary/course/BulgarianA2CourseRepository.kt`
- Test: `app/src/test/java/com/carbit3333333/a2bulgary/course/BulgarianA2CourseRepositoryTest.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.carbit3333333.a2bulgary.course

import org.junit.Assert.assertEquals
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
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `.\\gradlew.bat testDebugUnitTest --tests "*BulgarianA2CourseRepositoryTest"`
Expected: FAIL because repository and model files do not exist yet.

- [ ] **Step 3: Write minimal implementation**

```kotlin
data class LessonUnit(
    val numberLabel: String,
    val title: String,
    val page: Int?,
    val goals: List<String>,
    val grammar: List<String>,
    val vocabulary: List<String>,
    val culture: String,
)

object BulgarianA2CourseRepository {
    val units: List<LessonUnit> = listOf(
        LessonUnit(
            numberLabel = "Преговор",
            title = "Преговор A1",
            page = null,
            goals = listOf("Преговор на базови теми от A1"),
            grammar = listOf("Ключови структури от A1"),
            vocabulary = listOf("Всекидневна лексика"),
            culture = "Подготовка за преход към ниво A2",
        ),
        LessonUnit(
            numberLabel = "1",
            title = "Ало, ало!",
            page = 16,
            goals = listOf("Провеждат телефонни разговори"),
            grammar = listOf("Винителни форми на личните местоимения"),
            vocabulary = listOf("Телефонни разговори"),
            culture = "Разговори по телефона",
        ),
    )
}
```

- [ ] **Step 4: Expand implementation to the full textbook outline**

Add the remaining 9 A2 lessons with textbook-derived page numbers and lists for goals, grammar, vocabulary, and culture.

- [ ] **Step 5: Run test to verify it passes**

Run: `.\\gradlew.bat testDebugUnitTest --tests "*BulgarianA2CourseRepositoryTest"`
Expected: PASS

### Task 2: Replace Starter Screen With Course Overview

**Files:**
- Modify: `app/src/main/java/com/carbit3333333/a2bulgary/MainActivity.kt`
- Create: `app/src/main/java/com/carbit3333333/a2bulgary/ui/course/A2CourseScreen.kt`
- Modify: `app/src/main/res/values/strings.xml`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.carbit3333333.a2bulgary.course

import org.junit.Assert.assertTrue
import org.junit.Test

class BulgarianA2CourseRepositoryTest {

    @Test
    fun eachUnitProvidesAtLeastOneGoalAndOneGrammarPoint() {
        assertTrue(BulgarianA2CourseRepository.units.all { unit ->
            unit.goals.isNotEmpty() && unit.grammar.isNotEmpty()
        })
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `.\\gradlew.bat testDebugUnitTest --tests "*BulgarianA2CourseRepositoryTest"`
Expected: FAIL until all seeded units are fully populated.

- [ ] **Step 3: Write minimal implementation**

```kotlin
@Composable
fun A2CourseApp() {
    A2CourseScreen(units = BulgarianA2CourseRepository.units)
}
```

```xml
<string name="app_name">A2 Bulgarski</string>
```

- [ ] **Step 4: Implement the screen**

Create a scrollable Compose screen with:
- a top course summary header
- one card per lesson unit
- readable grouping for goals, grammar, vocabulary, and culture

Update `MainActivity` to call the new screen instead of `Greeting`.

- [ ] **Step 5: Run unit tests and compile**

Run:
- `.\\gradlew.bat testDebugUnitTest --tests "*BulgarianA2CourseRepositoryTest"`
- `.\\gradlew.bat :app:compileDebugKotlin`

Expected:
- repository tests PASS
- Kotlin compile succeeds

### Task 3: Polish Theme and Naming

**Files:**
- Modify: `app/src/main/java/com/carbit3333333/a2bulgary/ui/theme/Color.kt`
- Modify: `app/src/main/java/com/carbit3333333/a2bulgary/ui/theme/Theme.kt`
- Modify: `app/src/main/java/com/carbit3333333/a2bulgary/ui/theme/Type.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.carbit3333333.a2bulgary.course

import org.junit.Assert.assertTrue
import org.junit.Test

class BulgarianA2CourseRepositoryTest {

    @Test
    fun lessonTitlesStayDistinctAcrossTheCourse() {
        val titles = BulgarianA2CourseRepository.units.map { it.title }
        assertTrue(titles.distinct().size == titles.size)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `.\\gradlew.bat testDebugUnitTest --tests "*BulgarianA2CourseRepositoryTest"`
Expected: FAIL if any seeded title is duplicated or placeholder text remains.

- [ ] **Step 3: Write minimal implementation**

```kotlin
private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    secondary = DeepTeal,
    tertiary = ClayRed,
    background = Paper,
    surface = SurfaceWarm,
)
```

- [ ] **Step 4: Finalize theme cleanup**

Rename the mojibake theme function to an ASCII-safe identifier and replace the starter purple palette with a course-appropriate palette. Keep typography simple and readable.

- [ ] **Step 5: Run final verification**

Run:
- `.\\gradlew.bat testDebugUnitTest --tests "*BulgarianA2CourseRepositoryTest"`
- `.\\gradlew.bat :app:compileDebugKotlin`

Expected:
- tests PASS
- compile PASS
