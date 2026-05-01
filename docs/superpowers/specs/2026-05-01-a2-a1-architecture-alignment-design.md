# A2 to A1 Architecture Alignment Design

## Goal

Refactor `D:\2Bulgary` so its application structure matches the proven architecture from `D:\OIiglot_Bulgary`, while replacing A1 lesson content with A2 textbook content.

## Why

The current A2 project has a working prototype, but it is architecturally too shallow compared with A1. A1 already has the right long-term seams:
- JSON lesson assets
- serializable lesson models
- repository-based content loading
- viewmodel-driven UI state
- navigation-based screen flow
- content validation tests

If A2 keeps growing on the current prototype structure, the two apps will drift and later feature transfer will become expensive.

## Source Architecture to Mirror

The A1 project uses this backbone:
- `assets/lessons_*.json` for localized lesson data
- `model/` for serializable domain objects such as `Lesson` and `TheoryBlock`
- `data/LessonRepository.kt` for asset loading and lesson lookup
- `viewmodel/` for list/detail state and behavior
- `ui/lessons/` for screen rendering
- `navigation/` for route definitions and screen transitions
- JVM tests that validate content and asset integrity

The A2 app should adopt the same backbone unless a difference is clearly required by A2 content. For this alignment pass, no such divergence is needed.

## Scope

This alignment includes:
- replacing the current hardcoded Kotlin lesson content layer with asset-backed JSON lessons
- replacing local screen state navigation with A1-style navigation components
- introducing A1-style models, repository, viewmodels, and UI state classes
- restructuring lesson list and lesson detail screens into the same folders and responsibilities as A1
- introducing content tests comparable to A1 asset and textbook validation tests

This alignment does not yet include:
- dictionary subsystem
- billing
- lesson session engine and exercise runner
- settings and theme mode switching
- multilingual support beyond structuring the app so it can support localized lesson assets later

## Target Architecture

### Entry and Navigation

`MainActivity` should stop owning screen state directly. It should host the app theme and call `AppNavGraph()`.

`navigation/Destinations.kt` and `navigation/AppNavGraph.kt` should define at least:
- lessons list route
- lesson details route with `lessonId`

The current `remember { mutableStateOf(...) }` screen switching should be removed.

### Models

The ad-hoc `LessonUnit` model should be replaced with A1-compatible models:
- `model/Lesson.kt`
- `model/TheoryBlock.kt`

The A2 lesson content for now should fit into `Lesson(id, title, subtitle, theory, ...)` exactly like A1. If later A2 needs richer theory blocks or exercise metadata, that should be introduced as an additive extension after alignment, not before.

### Data Layer

A2 lessons should move to `app/src/main/assets/lessons_ru.json`.

`data/LessonRepository.kt` should follow the A1 pattern:
- load lessons from assets using Kotlin serialization
- cache them lazily
- expose `getLessons()`, `getLessonById()`, and next-lesson helpers

For this pass, only Russian content asset is required. The code should still be structured so localized assets can be added later without another architectural rewrite.

### ViewModels and UI State

The app should introduce:
- `viewmodel/LessonsViewModel.kt`
- `viewmodel/LessonViewModel.kt`
- `ui/lessons/LessonsUiState.kt`
- `ui/lessons/LessonUiState.kt`

The screens should read state from viewmodels rather than from direct repository calls inside composables.

### UI Structure

The UI package layout should match A1:
- `ui/lessons/LessonsScreen.kt`
- `ui/lessons/LessonScreen.kt`

The current visual language can stay, but the file boundaries and data flow should mirror A1.

### Testing

Add A1-style content tests so the architecture stays honest:
- repository test for lesson loading and lookup
- JSON asset test for valid lesson structure
- textbook content test for lesson 1 A2 content presence

The tests should validate real assets, not only hardcoded Kotlin objects.

## Migration Strategy

Do the refactor in one focused slice:
1. add A1-compatible models and asset-backed repository
2. move current A2 lesson content into JSON assets
3. add tests for assets and repository
4. add navigation and viewmodels
5. replace the current prototype UI wiring with A1-style screens
6. remove the temporary prototype-only files once the new flow compiles and tests pass

This keeps the architecture consistent and avoids leaving a half-prototype, half-production hybrid.

## Risks

### Risk: partial mirroring

If only filenames are matched but data flow stays prototype-style, the alignment will be cosmetic and future maintenance cost will stay high.

Response:
- match responsibilities, not just names

### Risk: premature overreach

Trying to port dictionary, billing, and session systems immediately would make this alignment noisy and risky.

Response:
- keep this pass limited to lesson browsing architecture

### Risk: content corruption

The app already had Cyrillic sensitivity earlier, and A1 relies on UTF-8 JSON assets.

Response:
- treat JSON asset edits as UTF-8-safe content work
- validate with tests immediately after changes

## Success Criteria

The alignment is complete when:
- `D:\2Bulgary` uses `assets -> model -> data -> viewmodel -> ui -> navigation` for lessons
- lesson list and lesson detail open through `AppNavGraph`
- lesson content comes from JSON assets, not hardcoded Kotlin lists
- A2 lesson 1 content still appears correctly in the app
- JVM tests verify repository loading and asset structure
