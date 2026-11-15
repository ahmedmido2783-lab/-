package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.toArabicDigits
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.LocalNoorColors
import com.example.viewmodel.QuizViewModel

@Composable
fun QuizSessionScreen(
    viewModel: QuizViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val ttsSpeaking by viewModel.ttsSpeaking.collectAsStateWithLifecycle()
    val ttsArabicSupported by viewModel.ttsArabicSupported.collectAsStateWithLifecycle()
    val noorColors = LocalNoorColors.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("quiz_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "إنهاء المسابقة"
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.category.arabicName,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!state.isFinished && state.questions.isNotEmpty()) {
                            Text(
                                text = "السؤال ${(state.currentIndex + 1).toArabicDigits()} من ${state.questions.size.toArabicDigits()}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = noorColors.goldContainer
                    ) {
                        Text(
                            text = "النقاط: ${state.score.toArabicDigits()}",
                            style = MaterialTheme.typography.labelLarge,
                            color = noorColors.onGoldContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            if (state.isFinished) {
                QuizSummaryView(
                    total = state.questions.size,
                    score = state.score,
                    bestStreak = state.bestStreak,
                    onReplay = { viewModel.startNewQuiz() },
                    onBack = onBack
                )
            } else {
                val question = state.currentQuestion
                if (question != null) {
                    val optionLetters = listOf("أ", "ب", "ج", "د")
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 720.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Progress + Viewer Countdown Bar
                        if (state.totalTimeSeconds > 0) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Filled.Timer,
                                                contentDescription = null,
                                                tint = if (state.timeRemainingSeconds <= 7) noorColors.activeVerseRed else MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (state.isRevealed) {
                                                    "انتهت مهلة السؤال"
                                                } else {
                                                    "فرصة التفكير للمشاهدين: ${state.timeRemainingSeconds.toArabicDigits()} ثانية"
                                                },
                                                style = MaterialTheme.typography.labelLarge,
                                                color = if (state.timeRemainingSeconds <= 7) noorColors.activeVerseRed else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        if (!state.isRevealed) {
                                            IconButton(
                                                onClick = { viewModel.toggleTimerPause() },
                                                modifier = Modifier.size(34.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (state.isTimerPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                                                    contentDescription = if (state.isTimerPaused) "استئناف المؤقت" else "إيقاف المؤقت",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    val fraction = state.timeRemainingSeconds.toFloat() / state.totalTimeSeconds.toFloat()
                                    LinearProgressIndicator(
                                        progress = { fraction.coerceIn(0f, 1f) },
                                        color = if (state.timeRemainingSeconds <= 7) noorColors.activeVerseRed else MaterialTheme.colorScheme.primary,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(7.dp)
                                            .clip(RoundedCornerShape(50))
                                    )
                                }
                            }
                        }

                        // Question Card + Voice Reader Bar
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.2.dp, noorColors.goldAccent.copy(alpha = 0.45f)),
                            tonalElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = question.category.arabicName,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = if (ttsSpeaking) noorColors.activeVerseContainer else noorColors.goldContainer,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .clickable {
                                                if (ttsSpeaking) {
                                                    viewModel.stopSpeaking()
                                                } else {
                                                    viewModel.speakCurrentQuestion(includeOptions = true)
                                                }
                                            }
                                            .testTag("read_question_voice_button")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (ttsSpeaking) Icons.Filled.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                                contentDescription = "قراءة السؤال صوتياً",
                                                tint = if (ttsSpeaking) noorColors.activeVerseRed else noorColors.onGoldContainer,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (ttsSpeaking) "إيقاف القارئ" else "قراءة السؤال والخيارات",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = if (ttsSpeaking) noorColors.activeVerseRed else noorColors.onGoldContainer
                                            )
                                        }
                                    }
                                }

                                if (!ttsArabicSupported) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "ملاحظة: محرك الصوت العربي غير مثبّت في إعدادات هذا الجهاز.",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = question.questionArabic,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontFamily = AmiriFontFamily,
                                        fontSize = 25.sp,
                                        lineHeight = 36.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.testTag("quiz_question_text")
                                )
                            }
                        }

                        // 4 Answer Choices
                        question.options.forEachIndexed { index, optionText ->
                            val isSelected = state.selectedOptionIndex == index
                            val isCorrect = index == question.correctIndex
                            val containerColor by animateColorAsState(
                                targetValue = when {
                                    !state.isRevealed -> MaterialTheme.colorScheme.surface
                                    isCorrect -> noorColors.correctContainer
                                    isSelected && !isCorrect -> noorColors.wrongContainer
                                    else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                                },
                                label = "option_bg_$index"
                            )
                            val borderColor by animateColorAsState(
                                targetValue = when {
                                    !state.isRevealed -> MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                                    isCorrect -> noorColors.correctGreen
                                    isSelected && !isCorrect -> noorColors.wrongRed
                                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                                },
                                label = "option_border_$index"
                            )

                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = containerColor,
                                border = BorderStroke(if (state.isRevealed && (isCorrect || isSelected)) 1.8.dp else 1.dp, borderColor),
                                tonalElevation = 2.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable(enabled = !state.isRevealed) { viewModel.selectAnswer(index) }
                                    .testTag("quiz_option_$index")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = when {
                                            state.isRevealed && isCorrect -> noorColors.correctGreen
                                            state.isRevealed && isSelected && !isCorrect -> noorColors.wrongRed
                                            else -> MaterialTheme.colorScheme.primaryContainer
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = optionLetters.getOrElse(index) { "${index + 1}" },
                                                style = MaterialTheme.typography.titleMedium,
                                                color = if (state.isRevealed && (isCorrect || isSelected)) {
                                                    Color.White
                                                } else {
                                                    MaterialTheme.colorScheme.onPrimaryContainer
                                                }
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = optionText,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (state.isRevealed && isCorrect) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = "إجابة صحيحة",
                                            tint = noorColors.correctGreen
                                        )
                                    } else if (state.isRevealed && isSelected && !isCorrect) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "إجابة خاطئة",
                                            tint = noorColors.wrongRed
                                        )
                                    }
                                }
                            }
                        }

                        // Explanation & Next Button
                        AnimatedVisibility(visible = state.isRevealed) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "الفائدة العلمية",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = question.explanationArabic,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Button(
                                    onClick = { viewModel.nextQuestion() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("quiz_next_button"),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = if (state.currentIndex + 1 >= state.questions.size) {
                                            "عرض النتيجة النهائية"
                                        } else {
                                            "السؤال التالي"
                                        },
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizSummaryView(
    total: Int,
    score: Int,
    bestStreak: Int,
    onReplay: () -> Unit,
    onBack: () -> Unit
) {
    val noorColors = LocalNoorColors.current
    val percentage = if (total > 0) (score * 100) / total else 0
    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 600.dp)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = noorColors.goldContainer,
            modifier = Modifier.size(92.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = noorColors.goldAccent,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "انتهت المسابقة الدينية",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "أحسنت! نتيجة مشرفة ومعلومات نافعة",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatColumn("الإجابات الصحيحة", "${score.toArabicDigits()} / ${total.toArabicDigits()}")
                StatColumn("نسبة النجاح", "${percentage.toArabicDigits()}٪")
                StatColumn("أطول سلسلة", bestStreak.toArabicDigits())
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onReplay,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "جولة جديدة", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "العودة لقائمة الأقسام", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
