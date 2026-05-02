package com.carbit3333333.a2bulgary.ui.lessons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carbit3333333.a2bulgary.R
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
    val colorScheme = MaterialTheme.colorScheme
    val titleText = if (result.isPassed) {
        stringResource(R.string.lesson_result_passed_title)
    } else {
        stringResource(R.string.lesson_result_failed_title)
    }
    val titleColor = if (result.isPassed) colorScheme.tertiary else colorScheme.error
    val scoreText = String.format(Locale.US, "%.1f", result.score)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = result.lessonTitle,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = titleText,
            style = MaterialTheme.typography.headlineSmall,
            color = titleColor,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ResultRow(
                    label = stringResource(R.string.lesson_result_total_exercises),
                    value = result.totalExercises.toString()
                )
                ResultRow(
                    label = stringResource(R.string.lesson_result_correct),
                    value = result.correctCount.toString(),
                    valueColor = colorScheme.tertiary
                )
                ResultRow(
                    label = stringResource(R.string.lesson_result_wrong),
                    value = result.wrongCount.toString(),
                    valueColor = colorScheme.error
                )
                ResultRow(
                    label = stringResource(R.string.lesson_result_score_label),
                    value = scoreText
                )
                ResultRow(
                    label = stringResource(R.string.lesson_result_passing_score),
                    value = "4.5"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (result.isPassed) {
                if (hasNextLesson) {
                    stringResource(R.string.lesson_result_next_opened)
                } else {
                    stringResource(R.string.lesson_result_last_available)
                }
            } else {
                stringResource(R.string.lesson_result_need_higher_score)
            },
            style = MaterialTheme.typography.bodyLarge,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.weight(1f))

        if (result.isPassed) {
            if (hasNextLesson) {
                Button(
                    onClick = onNextLessonClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.tertiary)
                ) {
                    Text(stringResource(R.string.lesson_result_next_lesson))
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = onBackToLessonsClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.secondary)
            ) {
                Text(stringResource(R.string.lesson_result_back_to_list))
            }
        } else {
            Button(
                onClick = onRetryClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.tertiary)
            ) {
                Text(stringResource(R.string.lesson_result_retry))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBackToLessonsClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.secondary)
            ) {
                Text(stringResource(R.string.lesson_result_back_to_list))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = valueColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LessonResultPassedPreview() {
    A2BulgaryTheme {
        Surface {
            LessonResultScreen(
                result = LessonResult(
                    lessonId = 2,
                    lessonTitle = "Ало, ало!",
                    totalExercises = 10,
                    correctCount = 9,
                    wrongCount = 1,
                    score = 4.5f,
                    isPassed = true,
                ),
                hasNextLesson = true,
                onRetryClick = {},
                onNextLessonClick = {},
                onBackToLessonsClick = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LessonResultFailedPreview() {
    A2BulgaryTheme {
        Surface {
            LessonResultScreen(
                result = LessonResult(
                    lessonId = 2,
                    lessonTitle = "Ало, ало!",
                    totalExercises = 10,
                    correctCount = 6,
                    wrongCount = 4,
                    score = 3.0f,
                    isPassed = false,
                ),
                hasNextLesson = true,
                onRetryClick = {},
                onNextLessonClick = {},
                onBackToLessonsClick = {},
            )
        }
    }
}
