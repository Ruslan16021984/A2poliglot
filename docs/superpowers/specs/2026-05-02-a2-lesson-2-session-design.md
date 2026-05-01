# A2 Lesson 2 Session Design

## Goal

Add the first A1-style lesson session flow to `D:\2Bulgary`, limited to lesson 2 (`Ало, ало!`), so the A2 app gains a real exercise layer without overbuilding the full course at once.

## Scope

This slice includes:
- a lesson-session model for word-constructor exercises
- an asset file for lesson 2 textbook exercises
- a repository/factory layer that loads lesson 2 exercises from assets
- a `LessonSessionViewModel`
- a `LessonSessionScreen`
- navigation from lesson details into the session screen

This slice does not include:
- sessions for all 11 lessons
- TTS
- voice input
- result screen
- progress persistence and unlock logic
- dictionary integration

## Why This Slice

The A1 app already proves the architecture for sessions, but porting the entire session subsystem at once would add too much surface area. Lesson 2 gives us one real A2 lesson to anchor the architecture, verify the flow, and then scale lesson-by-lesson.

## Architecture

Follow the same structural direction as A1:
- asset-backed exercise content
- dedicated lesson-session model objects
- repository/factory to transform assets into runtime exercises
- viewmodel-driven session state
- dedicated session screen routed by navigation

For this first pass, the runtime flow should stay intentionally narrow:
- only constructor-style exercises
- only lesson 2 asset loading
- only in-memory per-screen progress

## Data Shape

Add one lesson-session asset for lesson 2 with enough structured content to build a constructor session:
- lesson title
- source metadata
- 3 to 5 exercise items
- each item should include Bulgarian source text, Russian support text, correct words, distractors, and optional hint

The format should be close to the A1 textbook exercise assets so future lessons can follow the same pattern.

## Runtime Flow

From `LessonScreen` for lesson 2:
1. user opens lesson details
2. taps a button like `Начать упражнения`
3. app navigates to `lesson_session/2`
4. session screen loads constructor exercises from the lesson 2 asset
5. user taps words to build the correct sentence
6. `LessonSessionViewModel` checks correctness and advances to the next item

For other lessons, the start button may stay hidden or disabled for now.

## UI Boundaries

Keep these files separate:
- `model/` for session exercise model
- `data/lesson_session/` for asset loading and factory logic
- `ui/lessons/` for session UI state and screen
- `viewmodel/` for session state transitions
- `navigation/` for route entry

Do not merge session behavior into `LessonScreen` directly beyond the navigation trigger.

## Testing

Add narrow tests that prove the session layer works:
- asset parsing test for lesson 2 exercises
- repository/factory test that lesson 2 builds runtime exercises
- viewmodel/session-state test for loading and simple answer progression if feasible

## Success Criteria

This slice is complete when:
- lesson 2 has an asset-backed exercise set
- the app can navigate from lesson details to a session screen for lesson 2
- the session shows constructor exercises loaded from assets
- tests verify the asset and loading path
- the app still compiles cleanly
