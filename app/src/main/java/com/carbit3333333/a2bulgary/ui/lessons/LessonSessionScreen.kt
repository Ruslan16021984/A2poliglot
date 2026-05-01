package com.carbit3333333.a2bulgary.ui.lessons

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carbit3333333.a2bulgary.model.ExerciseResult
import com.carbit3333333.a2bulgary.model.LessonExercise
import com.carbit3333333.a2bulgary.ui.theme.A2BulgaryTheme
import com.carbit3333333.a2bulgary.viewmodel.LessonSessionViewModel

@Composable
fun LessonSessionScreen(
    lessonId: Int,
    onBackClick: () -> Unit,
    viewModel: LessonSessionViewModel = viewModel(
        factory = LessonSessionViewModel.provideFactory(
            LocalContext.current.applicationContext as Application
        )
    ),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(lessonId) {
        viewModel.loadLessonSession(lessonId)
    }

    LessonSessionScreenContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onWordClick = viewModel::selectWord,
        onSelectedWordClick = viewModel::removeSelectedWord,
        onCheckClick = viewModel::checkAnswer,
        onContinueClick = viewModel::continueAfterAnswer,
    )
}

@Composable
fun LessonSessionScreenContent(
    uiState: LessonSessionUiState,
    onBackClick: () -> Unit,
    onWordClick: (String) -> Unit,
    onSelectedWordClick: (String) -> Unit,
    onCheckClick: () -> Unit,
    onContinueClick: () -> Unit,
) {
    val exercise = uiState.currentExercise

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
                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                uiState.isLessonFinished -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Упражнения завершены",
                                style = MaterialTheme.typography.headlineMedium,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Правильно: ${uiState.correctCount}, ошибки: ${uiState.wrongCount}",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }

                exercise != null -> {
                    Text(
                        text = uiState.lessonTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = exercise.sourceText,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = exercise.instruction,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    exercise.hint?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Подсказка: $it",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    WordArea(
                        words = uiState.selectedWords,
                        onWordClick = onSelectedWordClick,
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    WordArea(
                        words = exercise.availableWords.filterNotSelected(uiState.selectedWords),
                        onWordClick = onWordClick,
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    when (uiState.currentResult) {
                        ExerciseResult.NONE -> {
                            Button(
                                onClick = onCheckClick,
                                enabled = uiState.selectedWords.isNotEmpty(),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text("Проверить")
                            }
                        }

                        ExerciseResult.CORRECT -> {
                            ResultBlock(
                                text = "Верно",
                                actionText = "Следующее",
                                onClick = onContinueClick,
                                accent = MaterialTheme.colorScheme.tertiary,
                            )
                        }

                        ExerciseResult.WRONG -> {
                            ResultBlock(
                                text = "Нужно попробовать ещё раз. Правильный ответ: ${exercise.correctAnswerWords.joinToString(" ")}",
                                actionText = "Дальше",
                                onClick = onContinueClick,
                                accent = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WordArea(
    words: List<String>,
    onWordClick: (String) -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (words.isEmpty()) {
            Text(
                text = "Выберите слова",
                modifier = Modifier.padding(16.dp),
            )
        } else {
            FlowRow(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                words.forEach { word ->
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.medium,
                            )
                            .clickable { onWordClick(word) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = word,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }
        }
    }
}

private fun List<String>.filterNotSelected(selectedWords: List<String>): List<String> {
    val hidden = selectedWords.toMutableList()
    return filter { hidden.remove(it).not() }
}

@Composable
private fun ResultBlock(
    text: String,
    actionText: String,
    onClick: () -> Unit,
    accent: androidx.compose.ui.graphics.Color,
) {
    Column {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = accent,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(actionText)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LessonSessionPreview() {
    A2BulgaryTheme {
        LessonSessionScreenContent(
            uiState = LessonSessionUiState(
                lessonTitle = "Ало, ало!",
                exercises = listOf(
                    LessonExercise(
                        id = 1,
                        sourceText = "Обаждам се за срещата утре.",
                        instruction = "Соберите фразу: Я звоню насчёт встречи завтра.",
                        correctAnswerWords = listOf("Обаждам", "се", "за", "срещата", "утре"),
                        availableWords = listOf("Обаждам", "се", "за", "срещата", "утре", "днес"),
                    )
                ),
                results = listOf(ExerciseResult.NONE),
            ),
            onBackClick = {},
            onWordClick = {},
            onSelectedWordClick = {},
            onCheckClick = {},
            onContinueClick = {},
        )
    }
}
