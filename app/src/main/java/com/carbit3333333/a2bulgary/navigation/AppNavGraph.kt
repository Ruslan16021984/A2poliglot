package com.carbit3333333.a2bulgary.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.carbit3333333.a2bulgary.data.LessonRepository
import com.carbit3333333.a2bulgary.data.dictionary.FlashcardTrainingPreferencesStore
import com.carbit3333333.a2bulgary.data.dictionary.PersonalDictionaryRepository
import com.carbit3333333.a2bulgary.model.LessonResult
import com.carbit3333333.a2bulgary.ui.dictionary.DictionaryScreen
import com.carbit3333333.a2bulgary.ui.dictionary.FlashcardTrainingScreen
import com.carbit3333333.a2bulgary.ui.dictionary.WordEditorScreen
import com.carbit3333333.a2bulgary.ui.lessons.LessonScreen
import com.carbit3333333.a2bulgary.ui.lessons.LessonResultScreen
import com.carbit3333333.a2bulgary.ui.lessons.LessonSessionScreen
import com.carbit3333333.a2bulgary.ui.lessons.LessonsScreen
import com.carbit3333333.a2bulgary.ui.settings.SettingsScreen
import com.carbit3333333.a2bulgary.viewmodel.AppSettingsViewModel
import com.carbit3333333.a2bulgary.viewmodel.FlashcardTrainingViewModel
import com.carbit3333333.a2bulgary.viewmodel.WordEditorViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.LESSONS,
    ) {
        composable(Destinations.LESSONS) {
            LessonsScreen(
                onDictionaryClick = {
                    navController.navigate(Destinations.DICTIONARY_LIST)
                },
                onSettingsClick = {
                    navController.navigate(Destinations.SETTINGS)
                },
                onLessonClick = { lessonId ->
                    navController.navigate(Destinations.lessonDetailsRoute(lessonId))
                },
            )
        }

        composable(Destinations.SETTINGS) {
            val context = LocalContext.current
            val application = context.applicationContext as Application
            val appSettingsViewModel: AppSettingsViewModel = viewModel(
                factory = AppSettingsViewModel.provideFactory(application)
            )

            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = appSettingsViewModel,
            )
        }

        composable(Destinations.DICTIONARY_LIST) {
            DictionaryScreen(
                onBackClick = { navController.popBackStack() },
                onAddWordClick = {
                    navController.navigate(Destinations.dictionaryEditRoute())
                },
                onTrainAllClick = {
                    navController.navigate(Destinations.dictionaryTrainingRoute())
                },
                onTrainGroupClick = { group ->
                    navController.navigate(
                        Destinations.dictionaryTrainingRoute(
                            groupId = group.id,
                            groupName = group.name,
                        )
                    )
                },
                onWordClick = { wordId ->
                    navController.navigate(Destinations.dictionaryEditRoute(wordId))
                },
                onOpenLessonClick = { lessonId ->
                    navController.navigate(Destinations.lessonDetailsRoute(lessonId))
                },
            )
        }

        composable(
            route = Destinations.DICTIONARY_EDIT,
            arguments = listOf(
                navArgument("wordId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val context = LocalContext.current
            val application = context.applicationContext as Application
            val repository = remember(context) { PersonalDictionaryRepository(context) }
            val wordId = backStackEntry.arguments?.getLong("wordId")?.takeIf { it > 0L }
            val wordEditorViewModel: WordEditorViewModel = viewModel(
                factory = WordEditorViewModel.provideFactory(
                    application = application,
                    repository = repository,
                    wordId = wordId,
                )
            )

            WordEditorScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = wordEditorViewModel,
            )
        }

        composable(
            route = Destinations.DICTIONARY_TRAINING,
            arguments = listOf(
                navArgument("groupId") {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument("groupName") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val context = LocalContext.current
            val application = context.applicationContext as Application
            val repository = remember(context) { PersonalDictionaryRepository(context) }
            val preferencesStore = remember(context) { FlashcardTrainingPreferencesStore(context) }
            val groupId = backStackEntry.arguments?.getLong("groupId")?.takeIf { it > 0L }
            val groupName = backStackEntry.arguments?.getString("groupName")?.takeIf { it.isNotBlank() }
            val flashcardTrainingViewModel: FlashcardTrainingViewModel = viewModel(
                factory = FlashcardTrainingViewModel.provideFactory(
                    application = application,
                    repository = repository,
                    preferencesStore = preferencesStore,
                    groupId = groupId,
                    groupName = groupName,
                )
            )

            FlashcardTrainingScreen(
                onBackClick = { navController.popBackStack() },
                onFinishClick = {
                    navController.navigate(Destinations.DICTIONARY_LIST) {
                        popUpTo(Destinations.DICTIONARY_LIST) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                viewModel = flashcardTrainingViewModel,
            )
        }

        composable(
            route = "${Destinations.LESSON_DETAILS}/{lessonId}",
            arguments = listOf(
                navArgument("lessonId") {
                    type = NavType.IntType
                }
            ),
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: 0

            LessonScreen(
                lessonId = lessonId,
                onBackClick = { navController.popBackStack() },
                onStartExerciseClick = { id ->
                    navController.navigate(Destinations.lessonSessionRoute(id))
                },
            )
        }

        composable(
            route = "${Destinations.LESSON_SESSION}/{lessonId}",
            arguments = listOf(
                navArgument("lessonId") {
                    type = NavType.IntType
                }
            ),
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: 0

            LessonSessionScreen(
                lessonId = lessonId,
                onBackClick = { navController.popBackStack() },
                onLessonFinished = { correctCount, wrongCount ->
                    navController.navigate(
                        Destinations.lessonResultRoute(
                            lessonId = lessonId,
                            correctCount = correctCount,
                            wrongCount = wrongCount,
                        )
                    )
                },
            )
        }

        composable(
            route = "${Destinations.LESSON_RESULT}/{lessonId}/{correctCount}/{wrongCount}",
            arguments = listOf(
                navArgument("lessonId") { type = NavType.IntType },
                navArgument("correctCount") { type = NavType.IntType },
                navArgument("wrongCount") { type = NavType.IntType },
            ),
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: 0
            val correctCount = backStackEntry.arguments?.getInt("correctCount") ?: 0
            val wrongCount = backStackEntry.arguments?.getInt("wrongCount") ?: 0
            val context = LocalContext.current
            val repository = remember(context) { LessonRepository(context) }
            val lesson = repository.getLessonById(lessonId)
            val totalExercises = correctCount + wrongCount
            val score = if (totalExercises > 0) {
                (correctCount.toFloat() / totalExercises.toFloat()) * 5f
            } else {
                0f
            }

            LessonResultScreen(
                result = LessonResult(
                    lessonId = lessonId,
                    lessonTitle = lesson?.title ?: "Урок",
                    totalExercises = totalExercises,
                    correctCount = correctCount,
                    wrongCount = wrongCount,
                    score = score,
                    isPassed = score >= 4.5f,
                ),
                hasNextLesson = repository.hasNextLesson(lessonId),
                onRetryClick = {
                    navController.navigate(Destinations.lessonSessionRoute(lessonId))
                },
                onNextLessonClick = {
                    val nextLessonId = repository.getNextLessonId(lessonId) ?: return@LessonResultScreen
                    navController.navigate(Destinations.lessonDetailsRoute(nextLessonId))
                },
                onBackToLessonsClick = {
                    navController.navigate(Destinations.LESSONS) {
                        popUpTo(Destinations.LESSONS) {
                            inclusive = false
                        }
                    }
                },
            )
        }
    }
}
