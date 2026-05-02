package com.carbit3333333.a2bulgary.ui.lessons

import android.app.Activity
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carbit3333333.a2bulgary.R
import com.carbit3333333.a2bulgary.model.ExerciseResult
import com.carbit3333333.a2bulgary.model.LessonExercise
import com.carbit3333333.a2bulgary.ui.theme.A2BulgaryTheme
import com.carbit3333333.a2bulgary.utils.AppTextToSpeech
import com.carbit3333333.a2bulgary.viewmodel.LessonSessionViewModel
import java.util.Locale
import kotlin.math.ceil

@Composable
fun LessonSessionScreen(
    lessonId: Int,
    onBackClick: () -> Unit,
    onLessonFinished: (correctCount: Int, wrongCount: Int) -> Unit = { _, _ -> },
    viewModel: LessonSessionViewModel = viewModel(
        factory = LessonSessionViewModel.provideFactory(
            LocalContext.current.applicationContext as Application
        )
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val textToSpeech = remember { AppTextToSpeech(context) }
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val spokenText = result.data
            ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            ?.firstOrNull()
            ?.trim()
            .orEmpty()

        if (result.resultCode == Activity.RESULT_OK && spokenText.isNotBlank()) {
            val exercise = uiState.currentExercise ?: return@rememberLauncherForActivityResult
            val matchedWords = mapRecognizedWords(
                spokenText = spokenText,
                availableWords = exercise.availableWords
            )
            if (matchedWords.isNotEmpty()) {
                matchedWords.forEach(viewModel::selectWord)
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.lesson_session_voice_no_match),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.lesson_session_voice_no_match),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(lessonId) {
        viewModel.loadLessonSession(lessonId)
    }

    LaunchedEffect(uiState.lessonResult) {
        val result = uiState.lessonResult ?: return@LaunchedEffect
        onLessonFinished(result.correctCount, result.wrongCount)
    }

    LaunchedEffect(uiState.currentExercise?.id, uiState.currentResult) {
        val currentExercise = uiState.currentExercise ?: return@LaunchedEffect
        if (
            uiState.currentResult == ExerciseResult.CORRECT ||
            uiState.currentResult == ExerciseResult.WRONG
        ) {
            textToSpeech.speak(currentExercise.correctAnswerWords.joinToString(" "))
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            textToSpeech.shutdown()
        }
    }

    LessonSessionScreenContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onWordClick = viewModel::selectWord,
        onSelectedWordClick = viewModel::removeSelectedWord,
        onCheckClick = viewModel::checkAnswer,
        onWrongAnswerScreenTap = viewModel::continueAfterAnswer,
        onSpeakClick = textToSpeech::speak,
        onVoiceInputClick = {
            val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bg-BG")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "bg-BG")
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "bg-BG")
                putExtra(
                    RecognizerIntent.EXTRA_PROMPT,
                    context.getString(R.string.lesson_session_voice_prompt)
                )
            }

            try {
                speechRecognizerLauncher.launch(speechIntent)
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(
                    context,
                    context.getString(R.string.lesson_session_voice_unavailable),
                    Toast.LENGTH_SHORT
                ).show()
            }
        },
    )
}

@Composable
fun LessonSessionScreenContent(
    uiState: LessonSessionUiState,
    onBackClick: () -> Unit,
    onWordClick: (String) -> Unit,
    onSelectedWordClick: (String) -> Unit,
    onCheckClick: () -> Unit,
    onWrongAnswerScreenTap: () -> Unit,
    onSpeakClick: (String) -> Unit,
    onVoiceInputClick: () -> Unit,
) {
    val currentExercise = uiState.currentExercise
    val palette = rememberLessonSessionPalette()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.pageBackground)
            .clickable(
                enabled = uiState.currentResult == ExerciseResult.WRONG,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onWrongAnswerScreenTap() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            SessionTopBar(
                lessonTitle = uiState.lessonTitle,
                currentScore = formatSessionScore(
                    correctCount = uiState.correctCount,
                    totalExercises = uiState.exercises.size
                ),
                correctCount = uiState.correctCount,
                wrongCount = uiState.wrongCount,
                onBackClick = onBackClick,
                containerColor = palette.topBar,
                onPrimaryColor = palette.topBarText,
                correctAccent = palette.counterCorrect,
                wrongAccent = palette.counterWrong,
            )

            when {
                uiState.errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }

                currentExercise != null -> {
                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = palette.cardSurface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, palette.cardBorder),
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Text(
                                text = currentExercise.sourceText,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.titleText,
                            )

                            currentExercise.hint?.let {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = palette.bodyText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when (uiState.currentResult) {
                        ExerciseResult.NONE -> InstructionBlock(currentExercise.instruction)
                        ExerciseResult.CORRECT -> CorrectAnswerBlock(
                            answerText = currentExercise.correctAnswerWords.joinToString(" "),
                            praiseText = uiState.praiseText,
                            onSpeakClick = onSpeakClick
                        )
                        ExerciseResult.WRONG -> WrongAnswerBlock(
                            selectedText = uiState.selectedWords.joinToString(" "),
                            correctText = currentExercise.correctAnswerWords.joinToString(" "),
                            onSpeakClick = onSpeakClick
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (uiState.currentResult == ExerciseResult.NONE) {
                        AnswerArea(
                            selectedWords = uiState.selectedWords,
                            onWordClick = onSelectedWordClick,
                            containerColor = palette.cardSurface,
                            chipColor = palette.selectedChipSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        WordGrid(
                            words = currentExercise.availableWords,
                            selectedWords = uiState.selectedWords,
                            onWordClick = onWordClick,
                            cardColor = palette.cardSurface,
                            cardBorderColor = palette.cardBorder,
                            textColor = palette.titleText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            when (uiState.currentResult) {
                                ExerciseResult.CORRECT -> Text(
                                    text = uiState.praiseText.orEmpty(),
                                    style = MaterialTheme.typography.displayMedium,
                                    color = palette.counterCorrect
                                )
                                ExerciseResult.WRONG -> Text(
                                    text = stringResource(R.string.lesson_session_wrong_continue),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = palette.bodyText,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                                ExerciseResult.NONE -> Unit
                            }
                        }
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.lesson_session_no_exercises),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }

            if (uiState.currentResult == ExerciseResult.NONE && currentExercise != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onCheckClick,
                        enabled = uiState.selectedWords.isNotEmpty(),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = palette.primaryButton,
                            contentColor = palette.primaryButtonText,
                            disabledContainerColor = palette.disabledButton,
                            disabledContentColor = palette.disabledButtonText,
                        )
                    ) {
                        Text(stringResource(R.string.common_check))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Surface(
                        modifier = Modifier
                            .width(42.dp)
                            .height(42.dp)
                            .clickable(onClick = onVoiceInputClick),
                        shape = CircleShape,
                        color = palette.primaryButton,
                        tonalElevation = 0.dp,
                        shadowElevation = 2.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Mic,
                                contentDescription = stringResource(R.string.lesson_session_voice_input),
                                tint = palette.primaryButtonText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            ProgressStrip(
                results = uiState.results,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun SessionTopBar(
    lessonTitle: String,
    currentScore: String,
    correctCount: Int,
    wrongCount: Int,
    onBackClick: () -> Unit,
    containerColor: Color,
    onPrimaryColor: Color,
    correctAccent: Color,
    wrongAccent: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerColor)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.common_back),
            tint = onPrimaryColor,
            modifier = Modifier.clickable(onClick = onBackClick)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = lessonTitle,
            color = onPrimaryColor,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        CounterItem(
            label = currentScore,
            circleColor = onPrimaryColor,
            textColor = containerColor
        )

        Spacer(modifier = Modifier.width(10.dp))

        CounterItem(
            label = correctCount.toString(),
            circleColor = onPrimaryColor,
            textColor = correctAccent
        )

        Spacer(modifier = Modifier.width(10.dp))

        CounterItem(
            label = wrongCount.toString(),
            circleColor = onPrimaryColor,
            textColor = wrongAccent
        )
    }
}

@Composable
private fun CounterItem(
    label: String,
    circleColor: Color,
    textColor: Color,
) {
    Box(
        modifier = Modifier
            .background(circleColor, CircleShape)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun formatSessionScore(correctCount: Int, totalExercises: Int): String {
    if (totalExercises <= 0) return "0.0"
    val score = (correctCount.toFloat() / totalExercises.toFloat()) * 5f
    return String.format(Locale.US, "%.1f", score)
}

@Composable
private fun InstructionBlock(text: String) {
    val palette = rememberLessonSessionPalette()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.instructionSurface)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            color = palette.instructionText,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun WrongAnswerBlock(
    selectedText: String,
    correctText: String,
    onSpeakClick: (String) -> Unit,
) {
    val palette = rememberLessonSessionPalette()
    val colorScheme = MaterialTheme.colorScheme

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.errorContainer)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = if (selectedText.isBlank()) " " else selectedText,
                color = colorScheme.onErrorContainer,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(palette.instructionSurface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = correctText,
                color = palette.instructionText,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            IconButton(onClick = { onSpeakClick(correctText) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = stringResource(R.string.common_listen),
                    tint = palette.instructionText,
                )
            }
        }
    }
}

@Composable
private fun CorrectAnswerBlock(
    answerText: String,
    praiseText: String?,
    onSpeakClick: (String) -> Unit,
) {
    val palette = rememberLessonSessionPalette()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(palette.instructionSurface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = answerText,
                color = palette.instructionText,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            IconButton(onClick = { onSpeakClick(answerText) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = stringResource(R.string.common_listen),
                    tint = palette.instructionText,
                )
            }
        }

        if (!praiseText.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AnswerArea(
    selectedWords: List<String>,
    onWordClick: (String) -> Unit,
    containerColor: Color,
    chipColor: Color,
    modifier: Modifier = Modifier,
) {
    val palette = rememberLessonSessionPalette()

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.cardBorder),
    ) {
        if (selectedWords.isEmpty()) {
            Text(
                text = stringResource(R.string.lesson_session_select_words),
                modifier = Modifier.padding(16.dp),
                color = palette.bodyText,
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            FlowRow(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedWords.forEach { word ->
                    SelectedWordChip(
                        text = word,
                        onClick = { onWordClick(word) },
                        containerColor = chipColor,
                        textColor = palette.selectedChipText,
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectedWordChip(
    text: String,
    onClick: () -> Unit,
    containerColor: Color,
    textColor: Color,
) {
    Box(
        modifier = Modifier
            .background(containerColor, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
        )
    }
}

@Composable
private fun WordGrid(
    words: List<String>,
    selectedWords: List<String>,
    onWordClick: (String) -> Unit,
    cardColor: Color,
    cardBorderColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    val hiddenWords = selectedWords.toMutableList()
    val visibleWords = words.filter { word -> hiddenWords.remove(word).not() }
    val gridVerticalPadding = 6.dp
    val gridBottomPadding = 18.dp
    val gridHorizontalPadding = 4.dp

    BoxWithConstraints(modifier = modifier) {
        val spacing = 12.dp
        val availableWidth = (maxWidth - gridHorizontalPadding * 2).coerceAtLeast(0.dp)
        val columns = when {
            maxWidth < 220.dp -> 1
            maxWidth < 840.dp -> 2
            else -> 3
        }
        val cardWidth = ((availableWidth - spacing * (columns - 1)) / columns)
            .coerceAtLeast(110.dp)
            .coerceAtMost(220.dp)
        val baseCardHeight = 88.dp
        val minRows = 2
        val currentRows = ceil(visibleWords.size / columns.toFloat()).toInt().coerceAtLeast(minRows)
        val minGridHeight = (baseCardHeight * currentRows) +
            (spacing * (currentRows - 1)) +
            (gridVerticalPadding * 2)

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minGridHeight)
                .animateContentSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = gridHorizontalPadding,
                top = gridVerticalPadding,
                end = gridHorizontalPadding,
                bottom = gridVerticalPadding + gridBottomPadding,
            ),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing),
            userScrollEnabled = true
        ) {
            items(visibleWords) { word ->
                WordButton(
                    text = word,
                    onClick = { onWordClick(word) },
                    containerColor = cardColor,
                    borderColor = cardBorderColor,
                    textColor = textColor,
                    width = cardWidth,
                )
            }
        }
    }
}

@Composable
private fun WordButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color,
    borderColor: Color,
    textColor: Color,
    width: Dp,
) {
    val compact = width < 140.dp
    val textStyle = if (compact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineSmall
    val horizontalPadding = if (compact) 10.dp else 12.dp
    val verticalPadding = if (compact) 12.dp else 10.dp

    Card(
        modifier = Modifier
            .widthIn(max = width)
            .heightIn(min = 80.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = textColor,
                style = textStyle,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )
        }
    }
}

@Composable
private fun ProgressStrip(
    results: List<ExerciseResult>,
    modifier: Modifier = Modifier,
) {
    val palette = rememberLessonSessionPalette()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        results.forEach { result ->
            val color = when (result) {
                ExerciseResult.NONE -> palette.progressIdle
                ExerciseResult.CORRECT -> palette.progressCorrect
                ExerciseResult.WRONG -> palette.progressWrong
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .background(color)
            )
        }
    }
}

private fun mapRecognizedWords(
    spokenText: String,
    availableWords: List<String>,
): List<String> {
    val spokenTokens = spokenText
        .split(Regex("\\s+"))
        .map(::normalizeRecognizedToken)
        .filter(String::isNotBlank)

    if (spokenTokens.isEmpty()) return emptyList()

    val available = availableWords.toMutableList()
    val matchedWords = mutableListOf<String>()

    spokenTokens.forEach { spokenToken ->
        val matchedIndex = available.indexOfFirst { availableWord ->
            normalizeRecognizedToken(availableWord) == spokenToken
        }

        if (matchedIndex >= 0) {
            matchedWords += available.removeAt(matchedIndex)
        }
    }

    return matchedWords
}

private fun normalizeRecognizedToken(token: String): String {
    return token
        .lowercase(Locale.ROOT)
        .replace("й", "и")
        .replace(Regex("[^\\p{L}\\p{Nd}]"), "")
}

@Preview(showBackground = true)
@Composable
private fun LessonSessionPreview() {
    A2BulgaryTheme {
        Surface {
            LessonSessionScreenContent(
                uiState = LessonSessionUiState(
                    lessonTitle = "Ало, ало!",
                    exercises = listOf(
                        LessonExercise(
                            id = 1,
                            sourceText = "Обаждам се за срещата утре.",
                            instruction = "Соберите фразу: Я звоню насчёт встречи завтра.",
                            correctAnswerWords = listOf("Обаждам", "се", "за", "срещата", "утре"),
                            availableWords = listOf("Обаждам", "се", "за", "срещата", "утре", "днес")
                        )
                    ),
                    results = listOf(ExerciseResult.NONE)
                ),
                onBackClick = {},
                onWordClick = {},
                onSelectedWordClick = {},
                onCheckClick = {},
                onWrongAnswerScreenTap = {},
                onSpeakClick = {},
                onVoiceInputClick = {},
            )
        }
    }
}
