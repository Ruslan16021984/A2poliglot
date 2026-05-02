package com.carbit3333333.a2bulgary.ui.lessons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carbit3333333.a2bulgary.model.LessonResult
import com.carbit3333333.a2bulgary.ui.theme.A2BulgaryTheme
import java.util.Locale

@Composable
fun LessonResultScreen(
    result: LessonResult,
    hasNextLesson: Boolean,
    onRetryClick: () -> Unit,
    onNextLessonClick: () -> Unit,
    onBackToLessonsClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = result.lessonTitle,
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (result.isPassed) "Урок пройден" else "Нужно повторить урок",
                style = MaterialTheme.typography.titleLarge,
                color = if (result.isPassed) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text("Всего упражнений: ${result.totalExercises}")
                    Text("Правильно: ${result.correctCount}")
                    Text("Ошибки: ${result.wrongCount}")
                    Text("Оценка: ${String.format(Locale.US, "%.1f", result.score)}")
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            if (result.isPassed && hasNextLesson) {
                Button(
                    onClick = onNextLessonClick,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Следующий урок")
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            Button(
                onClick = onRetryClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Пройти снова")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onBackToLessonsClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("К списку уроков")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LessonResultPreview() {
    A2BulgaryTheme {
        LessonResultScreen(
            result = LessonResult(
                lessonId = 2,
                lessonTitle = "Ало, ало!",
                totalExercises = 3,
                correctCount = 3,
                wrongCount = 0,
                score = 5.0f,
                isPassed = true,
            ),
            hasNextLesson = true,
            onRetryClick = {},
            onNextLessonClick = {},
            onBackToLessonsClick = {},
        )
    }
}
