package com.carbit3333333.a2bulgary.ui.lessons

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carbit3333333.a2bulgary.model.Lesson
import com.carbit3333333.a2bulgary.model.TheoryBlock
import com.carbit3333333.a2bulgary.ui.theme.A2BulgaryTheme
import com.carbit3333333.a2bulgary.viewmodel.LessonViewModel

@Composable
fun LessonScreen(
    lessonId: Int,
    onBackClick: () -> Unit,
    onStartExerciseClick: (Int) -> Unit = {},
    viewModel: LessonViewModel = viewModel(
        factory = LessonViewModel.provideFactory(
            LocalContext.current.applicationContext as Application
        )
    ),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
    }

    LessonScreenContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onStartExerciseClick = onStartExerciseClick,
    )
}

@Composable
fun LessonScreenContent(
    uiState: LessonUiState,
    onBackClick: () -> Unit,
    onStartExerciseClick: (Int) -> Unit = {},
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
        ) {
            Button(onClick = onBackClick) {
                Text("Назад")
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    Text("Зареждане...")
                }

                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                uiState.lesson != null -> {
                    val lesson = uiState.lesson

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        item {
                            Text(
                                text = lesson.title,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = lesson.subtitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )

                            if (lesson.id == 2) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { onStartExerciseClick(lesson.id) },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text("Начать упражнения")
                                }
                            }
                        }

                        items(lesson.theory) { block ->
                            TheoryCard(block = block)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TheoryCard(block: TheoryBlock) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
        ) {
            Text(
                text = block.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = block.text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LessonScreenPreview() {
    A2BulgaryTheme {
        LessonScreenContent(
            uiState = LessonUiState(
                lesson = Lesson(
                    id = 2,
                    title = "Ало, ало!",
                    subtitle = "Телефонни разговори и уговорка на среща",
                    theory = listOf(
                        TheoryBlock(
                            title = "Телефонни разговори",
                            text = "Ало. Удобно ли е да говорим сега?",
                        ),
                    ),
                ),
            ),
            onBackClick = {},
            onStartExerciseClick = {},
        )
    }
}
