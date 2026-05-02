package com.carbit3333333.a2bulgary.ui.lessons

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carbit3333333.a2bulgary.R
import com.carbit3333333.a2bulgary.model.Lesson
import com.carbit3333333.a2bulgary.model.TheoryBlock
import com.carbit3333333.a2bulgary.ui.common.HighlightedEndingText
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
    onStartExerciseClick: (Int) -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .navigationBarsPadding(),
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primaryContainer,
                    contentColor = colorScheme.onPrimaryContainer,
                )
            ) {
                Text(text = stringResource(R.string.common_back))
            }

            Spacer(modifier = Modifier.height(24.dp))

            when {
                uiState.isLoading -> {
                    Text(
                        text = stringResource(R.string.common_loading),
                        color = colorScheme.onBackground,
                    )
                }

                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage ?: stringResource(R.string.common_error),
                        color = colorScheme.error,
                    )
                }

                uiState.lesson != null -> {
                    val lesson = uiState.lesson

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        item {
                            Text(
                                text = lesson.title,
                                style = MaterialTheme.typography.headlineSmall,
                                color = colorScheme.onBackground,
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = lesson.subtitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = colorScheme.onBackground,
                            )
                        }

                        item {
                            Text(
                                text = stringResource(R.string.lesson_theory_title),
                                style = MaterialTheme.typography.titleLarge,
                                color = colorScheme.onBackground,
                            )
                        }

                        items(lesson.theory) { theoryBlock ->
                            TheoryBlockItem(theoryBlock = theoryBlock)
                        }

                        item {
                            Text(
                                text = stringResource(R.string.lesson_verb_endings_title),
                                style = MaterialTheme.typography.titleLarge,
                                color = colorScheme.onBackground,
                            )
                        }

                        item {
                            VerbEndingExamples()
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onStartExerciseClick(lesson.id) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary,
                        )
                    ) {
                        Text(text = stringResource(R.string.lesson_start_exercises))
                    }
                }
            }
        }
    }
}

@Composable
private fun VerbEndingExamples() {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.lesson_endings_attention),
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme.onSurface,
            )

            HighlightedEndingText(word = "правя", ending = "я")
            HighlightedEndingText(word = "правиш", ending = "иш")
            HighlightedEndingText(word = "прави", ending = "и")
            HighlightedEndingText(word = "правим", ending = "им")
            HighlightedEndingText(word = "правите", ending = "ите")
            HighlightedEndingText(word = "правят", ending = "ят")
        }
    }
}

@Composable
private fun TheoryBlockItem(
    theoryBlock: TheoryBlock,
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = theoryBlock.title,
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = theoryBlock.text,
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme.onSurface,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LessonScreenContentPreview() {
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
                            text = "Ало.\nУдобно ли е да говорим сега?\nОбаждам се за срещата утре."
                        ),
                        TheoryBlock(
                            title = "Когато не разбираме",
                            text = "Не разбрах.\nМоже ли да повторите?\nКажете пак, моля."
                        ),
                    ),
                )
            ),
            onBackClick = {},
            onStartExerciseClick = {},
        )
    }
}
