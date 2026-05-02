package com.carbit3333333.a2bulgary.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.carbit3333333.a2bulgary.data.LessonRepository
import com.carbit3333333.a2bulgary.model.LessonResult
import com.carbit3333333.a2bulgary.ui.lessons.LessonScreen
import com.carbit3333333.a2bulgary.ui.lessons.LessonResultScreen
import com.carbit3333333.a2bulgary.ui.lessons.LessonSessionScreen
import com.carbit3333333.a2bulgary.ui.lessons.LessonsScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.LESSONS,
    ) {
        composable(Destinations.LESSONS) {
            LessonsScreen(
                onLessonClick = { lessonId ->
                    navController.navigate(Destinations.lessonDetailsRoute(lessonId))
                },
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
