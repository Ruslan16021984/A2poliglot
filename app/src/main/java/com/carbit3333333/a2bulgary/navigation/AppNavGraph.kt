package com.carbit3333333.a2bulgary.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.carbit3333333.a2bulgary.ui.lessons.LessonScreen
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
            )
        }
    }
}
