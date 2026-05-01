package com.carbit3333333.a2bulgary.ui.lessons

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carbit3333333.a2bulgary.model.Lesson
import com.carbit3333333.a2bulgary.ui.theme.A2BulgaryTheme
import com.carbit3333333.a2bulgary.viewmodel.LessonsViewModel

@Composable
fun LessonsScreen(
    onLessonClick: (Int) -> Unit,
    viewModel: LessonsViewModel = viewModel(
        factory = LessonsViewModel.provideFactory(
            LocalContext.current.applicationContext as Application
        )
    ),
) {
    val uiState by viewModel.uiState.collectAsState()

    LessonsScreenContent(
        uiState = uiState,
        onLessonClick = onLessonClick,
    )
}

@Composable
fun LessonsScreenContent(
    uiState: LessonsUiState,
    onLessonClick: (Int) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when {
            uiState.isLoading -> {
                Text(
                    text = "Зареждане...",
                    modifier = Modifier.padding(24.dp),
                )
            }

            uiState.errorMessage != null -> {
                Text(
                    text = uiState.errorMessage,
                    modifier = Modifier.padding(24.dp),
                    color = MaterialTheme.colorScheme.error,
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    item {
                        CourseHeader(lessonCount = uiState.lessons.size)
                    }

                    items(uiState.lessons) { lesson ->
                        LessonListItem(
                            lesson = lesson,
                            onClick = { onLessonClick(lesson.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseHeader(lessonCount: Int) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
        ) {
            Text(
                text = "Български A2",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Курс по учебнику A2: $lessonCount уроков в A1-совместимой архитектуре.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun LessonListItem(
    lesson: Lesson,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
        ) {
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = lesson.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Теория: ${lesson.theory.size} блока",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LessonsScreenPreview() {
    A2BulgaryTheme {
        LessonsScreenContent(
            uiState = LessonsUiState(
                lessons = listOf(
                    Lesson(
                        id = 1,
                        title = "Преговор A1",
                        subtitle = "Повторение перед переходом к уровню A2",
                    ),
                    Lesson(
                        id = 2,
                        title = "Ало, ало!",
                        subtitle = "Телефонни разговори и уговорка на среща",
                    ),
                ),
            ),
            onLessonClick = {},
        )
    }
}
