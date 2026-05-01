# A2 to A1 Architecture Alignment Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Refactor `D:\2Bulgary` so A2 lessons use the same lesson architecture as `D:\OIiglot_Bulgary`, backed by JSON assets, repositories, viewmodels, and navigation.

**Architecture:** Replace the temporary hardcoded Compose prototype with an A1-style lesson stack: `assets -> model -> data -> viewmodel -> ui -> navigation`. Keep the scope limited to lesson browsing and lesson detail so the app structure aligns with A1 now, while leaving dictionary, billing, and exercise-session subsystems for later slices.

**Tech Stack:** Kotlin, Jetpack Compose Material 3, Navigation Compose, Kotlinx Serialization, JUnit4, AndroidX lifecycle/viewmodel

---

### Task 1: Add A1-Compatible Lesson Models and Asset Contract

**Files:**
- Create: `app/src/main/java/com/carbit3333333/a2bulgary/model/TheoryBlock.kt`
- Create: `app/src/main/java/com/carbit3333333/a2bulgary/model/Lesson.kt`
- Create: `app/src/main/assets/lessons_ru.json`
- Modify: `app/build.gradle.kts`
- Test: `app/src/test/java/com/carbit3333333/a2bulgary/LessonJsonAssetsTest.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.carbit3333333.a2bulgary

import androidx.test.core.app.ApplicationProvider
import com.carbit3333333.a2bulgary.model.Lesson
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LessonJsonAssetsTest {

    @Test
    fun lessonsAssetContainsReviewAndTenA2Lessons() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val jsonText = context.assets.open("lessons_ru.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
        val lessons = Json { ignoreUnknownKeys = true }.decodeFromString<List<Lesson>>(jsonText)

        assertEquals(11, lessons.size)
        assertEquals(1, lessons.first().id)
        assertEquals("Преговор A1", lessons.first().title)
        assertTrue(lessons.any { it.title == "Ало, ало!" })
        assertTrue(lessons.any { it.title == "Интервю за работа" })
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `.\\gradlew.bat testDebugUnitTest --tests "*LessonJsonAssetsTest"`
Expected: FAIL because `Lesson` model, serialization dependency, and `lessons_ru.json` do not exist yet.

- [ ] **Step 3: Write minimal implementation**

```kotlin
package com.carbit3333333.a2bulgary.model

import kotlinx.serialization.Serializable

@Serializable
data class TheoryBlock(
    val title: String,
    val text: String,
)
```

```kotlin
package com.carbit3333333.a2bulgary.model

import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    val id: Int,
    val title: String,
    val subtitle: String,
    val theory: List<TheoryBlock> = emptyList(),
    val isCompleted: Boolean = false,
    val isLocked: Boolean = false,
    val bestScore: Float? = null,
    val currentScore: Float? = null,
    val currentProgress: Int = 0,
    val totalProgress: Int = 0,
)
```

```json
[
  {
    "id": 1,
    "title": "Преговор A1",
    "subtitle": "Повторение перед уровнем A2",
    "theory": [
      {
        "title": "Повторение",
        "text": "Краткий обзор тем A1 перед переходом к учебнику A2."
      }
    ]
  }
]
```

- [ ] **Step 4: Expand the asset to the full A2 textbook outline**

Replace the stub JSON with 11 lessons total:
- lesson 1 is `Преговор A1`
- lesson 2 is `Ало, ало!`
- last lesson is `Интервю за работа`

For each lesson, include:
- `id`
- `title`
- `subtitle`
- at least 3 `theory` blocks for real lesson entries

Use UTF-8 and keep the file shape compatible with A1 `LessonRepository`.

- [ ] **Step 5: Add build support and rerun the test**

Add to `app/build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    testImplementation("androidx.test:core-ktx:1.6.1")
    testImplementation("org.robolectric:robolectric:4.13")
}
```

Run: `.\\gradlew.bat testDebugUnitTest --tests "*LessonJsonAssetsTest"`
Expected: PASS

### Task 2: Add Asset-Backed Lesson Repository

**Files:**
- Create: `app/src/main/java/com/carbit3333333/a2bulgary/data/LessonRepository.kt`
- Test: `app/src/test/java/com/carbit3333333/a2bulgary/LessonRepositoryTest.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.carbit3333333.a2bulgary

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.carbit3333333.a2bulgary.data.LessonRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LessonRepositoryTest {

    @Test
    fun loadsLessonsAndSupportsLookup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = LessonRepository(context)

        val lessons = repository.getLessons()

        assertEquals(11, lessons.size)
        assertEquals("Преговор A1", lessons.first().title)
        assertNotNull(repository.getLessonById(2))
        assertEquals("Ало, ало!", repository.getLessonById(2)?.title)
        assertTrue(repository.hasNextLesson(2))
        assertEquals(3, repository.getNextLessonId(2))
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `.\\gradlew.bat testDebugUnitTest --tests "*LessonRepositoryTest"`
Expected: FAIL because `LessonRepository` does not exist yet.

- [ ] **Step 3: Write minimal implementation**

```kotlin
package com.carbit3333333.a2bulgary.data

import android.content.Context
import com.carbit3333333.a2bulgary.model.Lesson
import kotlinx.serialization.json.Json

class LessonRepository(
    context: Context,
) {
    private val appContext = context.applicationContext

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val lessonsCache: List<Lesson> by lazy {
        appContext.assets
            .open("lessons_ru.json")
            .bufferedReader(Charsets.UTF_8)
            .use { reader -> json.decodeFromString<List<Lesson>>(reader.readText()) }
    }

    fun getLessons(): List<Lesson> = lessonsCache

    fun getLessonById(lessonId: Int): Lesson? = lessonsCache.find { it.id == lessonId }

    fun hasNextLesson(currentLessonId: Int): Boolean = lessonsCache.any { it.id == currentLessonId + 1 }

    fun getNextLessonId(currentLessonId: Int): Int? = lessonsCache.find { it.id == currentLessonId + 1 }?.id
}
```

- [ ] **Step 4: Run repository tests**

Run: `.\\gradlew.bat testDebugUnitTest --tests "*LessonRepositoryTest"`
Expected: PASS

### Task 3: Add A1-Style UI State and ViewModels

**Files:**
- Create: `app/src/main/java/com/carbit3333333/a2bulgary/ui/lessons/LessonsUiState.kt`
- Create: `app/src/main/java/com/carbit3333333/a2bulgary/ui/lessons/LessonUiState.kt`
- Create: `app/src/main/java/com/carbit3333333/a2bulgary/viewmodel/LessonsViewModel.kt`
- Create: `app/src/main/java/com/carbit3333333/a2bulgary/viewmodel/LessonViewModel.kt`
- Test: `app/src/test/java/com/carbit3333333/a2bulgary/LessonViewModelContractTest.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.carbit3333333.a2bulgary

import com.carbit3333333.a2bulgary.model.Lesson
import com.carbit3333333.a2bulgary.model.TheoryBlock
import com.carbit3333333.a2bulgary.ui.lessons.LessonUiState
import com.carbit3333333.a2bulgary.ui.lessons.LessonsUiState
import org.junit.Assert.assertEquals
import org.junit.Test

class LessonViewModelContractTest {

    @Test
    fun uiStateDefaultsArePredictable() {
        val lessonsState = LessonsUiState()
        val lessonState = LessonUiState()

        assertEquals(emptyList<Lesson>(), lessonsState.lessons)
        assertEquals(null, lessonState.lesson)
        assertEquals(false, lessonsState.isLoading)
        assertEquals(false, lessonState.isLoading)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `.\\gradlew.bat testDebugUnitTest --tests "*LessonViewModelContractTest"`
Expected: FAIL because the UI state classes do not exist yet.

- [ ] **Step 3: Write minimal implementation**

```kotlin
package com.carbit3333333.a2bulgary.ui.lessons

import com.carbit3333333.a2bulgary.model.Lesson

data class LessonsUiState(
    val lessons: List<Lesson> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
```

```kotlin
package com.carbit3333333.a2bulgary.ui.lessons

import com.carbit3333333.a2bulgary.model.Lesson

data class LessonUiState(
    val lesson: Lesson? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
```

- [ ] **Step 4: Add viewmodels with repository-backed loading**

Create `LessonsViewModel.kt` and `LessonViewModel.kt` with:
- constructor taking `Application`
- internal `LessonRepository`
- `MutableStateFlow`
- initial content load in `init`
- `provideFactory(application)` companion

Use this shape for `LessonsViewModel`:

```kotlin
class LessonsViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val repository = LessonRepository(application)
    private val _uiState = MutableStateFlow(LessonsUiState(isLoading = true))
    val uiState: StateFlow<LessonsUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = LessonsUiState(lessons = repository.getLessons())
    }
}
```

Use the same pattern for `LessonViewModel`, with `loadLesson(lessonId: Int)`.

- [ ] **Step 5: Run the state contract test**

Run: `.\\gradlew.bat testDebugUnitTest --tests "*LessonViewModelContractTest"`
Expected: PASS

### Task 4: Add A1-Style Navigation Shell

**Files:**
- Create: `app/src/main/java/com/carbit3333333/a2bulgary/navigation/Destinations.kt`
- Create: `app/src/main/java/com/carbit3333333/a2bulgary/navigation/AppNavGraph.kt`
- Modify: `app/src/main/java/com/carbit3333333/a2bulgary/MainActivity.kt`
- Modify: `gradle/libs.versions.toml`

- [ ] **Step 1: Write the failing test**

```kotlin
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
```

- [ ] **Step 2: Run test to verify it fails**

Run: `.\\gradlew.bat testDebugUnitTest --tests "*DestinationsTest"`
Expected: FAIL because navigation files do not exist yet.

- [ ] **Step 3: Add navigation dependency and destination helpers**

Add to `gradle/libs.versions.toml`:

```toml
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version = "2.8.0" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version = "2.8.4" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version = "2.8.4" }
```

Add to `app/build.gradle.kts` dependencies:

```kotlin
implementation(libs.androidx.navigation.compose)
implementation(libs.androidx.lifecycle.viewmodel.compose)
implementation(libs.androidx.lifecycle.runtime.compose)
```

Create `Destinations.kt`:

```kotlin
package com.carbit3333333.a2bulgary.navigation

object Destinations {
    const val LESSONS = "lessons"
    const val LESSON_DETAILS = "lesson_details"

    fun lessonDetailsRoute(lessonId: Int): String = "$LESSON_DETAILS/$lessonId"
}
```

- [ ] **Step 4: Add `AppNavGraph` and rewire `MainActivity`**

Create `AppNavGraph.kt` with:
- `NavHost`
- start destination `Destinations.LESSONS`
- list route rendering `LessonsScreen`
- detail route rendering `LessonScreen`

Update `MainActivity.kt` so `setContent` calls:

```kotlin
A2BulgaryTheme {
    AppNavGraph()
}
```

Remove the prototype `remember`-based selection state.

- [ ] **Step 5: Run the route test**

Run: `.\\gradlew.bat testDebugUnitTest --tests "*DestinationsTest"`
Expected: PASS

### Task 5: Replace Prototype Screens With A1-Style Lesson Screens

**Files:**
- Create: `app/src/main/java/com/carbit3333333/a2bulgary/ui/lessons/LessonsScreen.kt`
- Create: `app/src/main/java/com/carbit3333333/a2bulgary/ui/lessons/LessonScreen.kt`
- Modify: `app/src/main/java/com/carbit3333333/a2bulgary/ui/theme/Theme.kt`
- Modify: `app/src/main/java/com/carbit3333333/a2bulgary/ui/theme/Type.kt`
- Delete: `app/src/main/java/com/carbit3333333/a2bulgary/ui/course/A2CourseScreen.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.carbit3333333.a2bulgary

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.carbit3333333.a2bulgary.data.LessonRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class Lesson1TextbookContentTest {

    @Test
    fun firstA2TopicKeepsPhoneCallContent() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = LessonRepository(context)
        val lesson = repository.getLessonById(2)

        assertEquals("Ало, ало!", lesson?.title)
        assertEquals("Телефонни разговори и уговорка на среща", lesson?.subtitle)
        assertEquals("Телефонни разговори", lesson?.theory?.first()?.title)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `.\\gradlew.bat testDebugUnitTest --tests "*Lesson1TextbookContentTest"`
Expected: FAIL until `lessons_ru.json` and the screen contract are aligned to the expected A2 lesson detail content.

- [ ] **Step 3: Implement the A1-style screens**

Create `LessonsScreen.kt` with:
- `LessonsViewModel`
- state collected via `collectAsStateWithLifecycle`
- list of `Lesson` cards
- click handler calling `onLessonClick(lesson.id)`

Create `LessonScreen.kt` with:
- `LessonViewModel`
- `lessonId` argument
- theory blocks rendered from `lesson.theory`
- back action callback

Use the existing warm A2 palette and typography, but keep the file boundaries and state flow aligned to A1.

- [ ] **Step 4: Remove the prototype-only lesson UI**

Delete `ui/course/A2CourseScreen.kt` and remove all references to:
- `LessonUnit`
- `BulgarianA2CourseRepository`
- manual selected-lesson state in `MainActivity`

- [ ] **Step 5: Run the content test and compile**

Run:
- `.\\gradlew.bat testDebugUnitTest --tests "*Lesson1TextbookContentTest"`
- `.\\gradlew.bat :app:compileDebugKotlin`

Expected:
- content test PASS
- compile PASS

### Task 6: Clean Up the Temporary Prototype and Verify Alignment

**Files:**
- Delete: `app/src/main/java/com/carbit3333333/a2bulgary/course/CourseModels.kt`
- Delete: `app/src/main/java/com/carbit3333333/a2bulgary/course/BulgarianA2CourseRepository.kt`
- Delete or rewrite: `app/src/test/java/com/carbit3333333/a2bulgary/course/BulgarianA2CourseRepositoryTest.kt`
- Modify: `docs/superpowers/specs/2026-05-01-a2-course-foundation-design.md`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.carbit3333333.a2bulgary

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.carbit3333333.a2bulgary.data.LessonRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class ArchitectureAlignmentSmokeTest {

    @Test
    fun lessonTitlesComeFromJsonBackedRepository() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = LessonRepository(context)

        assertEquals("Преговор A1", repository.getLessonById(1)?.title)
        assertEquals("Ало, ало!", repository.getLessonById(2)?.title)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `.\\gradlew.bat testDebugUnitTest --tests "*ArchitectureAlignmentSmokeTest"`
Expected: FAIL until the old prototype-only tests and files are removed or rewritten to target the new architecture.

- [ ] **Step 3: Remove temporary prototype files and rewrite old tests**

Delete:
- `app/src/main/java/com/carbit3333333/a2bulgary/course/CourseModels.kt`
- `app/src/main/java/com/carbit3333333/a2bulgary/course/BulgarianA2CourseRepository.kt`

Replace the old prototype repository test with JSON/repository-based tests under `app/src/test/java/com/carbit3333333/a2bulgary/`.

Update the older foundation spec with a short note that the initial prototype has been superseded by A1-aligned architecture.

- [ ] **Step 4: Run the focused verification suite**

Run:
- `.\\gradlew.bat testDebugUnitTest --tests "*LessonJsonAssetsTest"`
- `.\\gradlew.bat testDebugUnitTest --tests "*LessonRepositoryTest"`
- `.\\gradlew.bat testDebugUnitTest --tests "*Lesson1TextbookContentTest"`
- `.\\gradlew.bat testDebugUnitTest --tests "*ArchitectureAlignmentSmokeTest"`
- `.\\gradlew.bat :app:compileDebugKotlin`

Expected:
- all tests PASS
- compile PASS

- [ ] **Step 5: Commit**

```bash
git add app/build.gradle.kts gradle/libs.versions.toml app/src/main assets docs
git commit -m "refactor: align A2 lesson architecture with A1 app"
```
