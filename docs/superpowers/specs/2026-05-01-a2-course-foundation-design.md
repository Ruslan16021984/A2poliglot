# A2 Course Foundation Design

## Goal

Turn the fresh Android project into a usable A2 Bulgarian course shell backed by real lesson data from the uploaded textbook.

## Scope

This first slice covers:
- fixing visible encoding issues in app-facing names
- introducing a small in-app lesson model for the A2 textbook
- seeding the first course dataset from the textbook table of contents
- replacing the default Compose greeting screen with a course overview screen

This slice does not yet cover:
- lesson detail screens
- interactive exercises
- persistence, progress tracking, or audio

## Product Shape

The app opens to a course overview that presents the A2 learning path as a sequence of lesson cards. Each card shows the lesson number, title, textbook page, communicative goals, grammar, vocabulary, and cultural note. The screen should already feel like a language-learning product rather than a generated demo app.

## Architecture

Use a small in-memory content layer first. A dedicated `LessonUnit` model plus a `BulgarianA2CourseRepository` object gives us a stable seam for later migration to JSON, Room, or remote content without rewriting the UI contract.

Keep the UI simple and focused:
- `MainActivity` hosts one top-level `A2CourseApp`
- `A2CourseScreen` renders the course header and lesson list
- lesson cards are isolated in a reusable composable

## Data Model

The lesson model should be explicit rather than generic magic maps:
- lesson number
- title
- textbook page
- list of communicative goals
- list of grammar points
- list of vocabulary themes
- cultural note

Include the review block plus 10 lessons from the textbook-derived structure already extracted from the PDF.

## Visual Direction

Avoid the default purple Compose starter look. Use a warm paper-and-ink palette that fits a textbook-based language app:
- light parchment background
- deep green/teal primary accents
- warm red accent for progress or emphasis

The first screen should feel readable, calm, and intentional on mobile.

## Error Handling

The first slice has no external I/O, so failure modes are minimal. The main risk is corrupted localized text. All Cyrillic-facing edits should stay UTF-8 safe and limited to targeted files.

## Testing

Start with small JVM tests around the seeded repository data:
- course exposes expected lesson count
- first lesson title matches the textbook-derived content
- review block exists

This gives us a safe regression net before expanding the UI.
