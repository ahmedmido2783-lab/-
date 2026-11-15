package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.AppContainer
import com.example.data.catalog.QuizCatalog
import com.example.data.model.QuizCategory
import com.example.data.model.QuizQuestion
import com.example.data.model.UserSettings
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizSessionUiState(
    val category: QuizCategory = QuizCategory.ALL,
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isRevealed: Boolean = false,
    val timeRemainingSeconds: Int = 30,
    val totalTimeSeconds: Int = 30,
    val isTimerPaused: Boolean = false,
    val score: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val isFinished: Boolean = false
) {
    val currentQuestion: QuizQuestion? get() = questions.getOrNull(currentIndex)
}

class QuizViewModel(
    private val container: AppContainer,
    private val categoryId: String,
    private val questionLimit: Int
) : ViewModel() {

    val settings: StateFlow<UserSettings> = container.settingsRepository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    val ttsSpeaking: StateFlow<Boolean> = container.ttsReader.isSpeaking
    val ttsArabicSupported: StateFlow<Boolean> = container.ttsReader.arabicSupported

    private val _uiState = MutableStateFlow(QuizSessionUiState())
    val uiState: StateFlow<QuizSessionUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        startNewQuiz()
    }

    fun startNewQuiz() {
        container.playerManager.stop()
        container.ttsReader.stop()
        val category = QuizCategory.entries.firstOrNull { it.id == categoryId } ?: QuizCategory.ALL
        val pool = QuizCatalog.getForCategory(category).shuffled()
        val selected = if (questionLimit > 0) pool.take(questionLimit) else pool

        viewModelScope.launch {
            val currentSettings = container.settingsRepository.settingsFlow.first()
            val timeLimit = currentSettings.quizTimeSeconds
            _uiState.value = QuizSessionUiState(
                category = category,
                questions = selected,
                currentIndex = 0,
                selectedOptionIndex = null,
                isRevealed = false,
                timeRemainingSeconds = timeLimit,
                totalTimeSeconds = timeLimit,
                isTimerPaused = false,
                score = 0,
                currentStreak = 0,
                bestStreak = 0,
                isFinished = false
            )
            beginQuestion(currentSettings)
        }
    }

    private fun beginQuestion(currentSettings: UserSettings) {
        timerJob?.cancel()
        val state = _uiState.value
        val question = state.currentQuestion ?: return
        if (currentSettings.quizAutoReadQuestion) {
            speakCurrentQuestion(includeOptions = true, speechRate = currentSettings.ttsSpeechRate)
        }
        if (state.totalTimeSeconds > 0) {
            timerJob = viewModelScope.launch {
                while (_uiState.value.timeRemainingSeconds > 0 && !_uiState.value.isRevealed) {
                    delay(1000L)
                    if (!_uiState.value.isTimerPaused && !_uiState.value.isRevealed) {
                        val remaining = _uiState.value.timeRemainingSeconds - 1
                        _uiState.update { it.copy(timeRemainingSeconds = remaining) }
                        if (remaining <= 0) {
                            // Time expired: reveal correct answer so viewers learn
                            _uiState.update {
                                it.copy(
                                    isRevealed = true,
                                    currentStreak = 0
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun speakCurrentQuestion(includeOptions: Boolean = true, speechRate: Float = settings.value.ttsSpeechRate) {
        val question = _uiState.value.currentQuestion ?: return
        val letters = listOf("أ", "ب", "ج", "د")
        val fullSpeech = buildString {
            append(question.questionArabic)
            if (includeOptions) {
                append(". الخيارات هي: ")
                question.options.forEachIndexed { idx, opt ->
                    val label = letters.getOrElse(idx) { "${idx + 1}" }
                    append(" $label: $opt. ")
                }
            }
        }
        container.ttsReader.speak(
            text = fullSpeech,
            utteranceId = "quiz_q_${question.id}_${System.currentTimeMillis()}",
            speechRate = speechRate
        )
    }

    fun stopSpeaking() {
        container.ttsReader.stop()
    }

    fun toggleTimerPause() {
        _uiState.update { it.copy(isTimerPaused = !it.isTimerPaused) }
    }

    fun selectAnswer(optionIndex: Int) {
        val current = _uiState.value
        if (current.isRevealed) return
        val question = current.currentQuestion ?: return
        timerJob?.cancel()
        container.ttsReader.stop()

        val isCorrect = optionIndex == question.correctIndex
        val newScore = if (isCorrect) current.score + 1 else current.score
        val newStreak = if (isCorrect) current.currentStreak + 1 else 0
        val newBestStreak = maxOf(current.bestStreak, newStreak)

        _uiState.update {
            it.copy(
                selectedOptionIndex = optionIndex,
                isRevealed = true,
                score = newScore,
                currentStreak = newStreak,
                bestStreak = newBestStreak
            )
        }
    }

    fun nextQuestion() {
        container.ttsReader.stop()
        val current = _uiState.value
        val nextIdx = current.currentIndex + 1
        if (nextIdx >= current.questions.size) {
            timerJob?.cancel()
            _uiState.update { it.copy(isFinished = true) }
            viewModelScope.launch {
                container.settingsRepository.updateHighScoreIfBetter(current.score)
            }
        } else {
            val timeLimit = current.totalTimeSeconds
            _uiState.update {
                it.copy(
                    currentIndex = nextIdx,
                    selectedOptionIndex = null,
                    isRevealed = false,
                    timeRemainingSeconds = timeLimit,
                    isTimerPaused = false
                )
            }
            beginQuestion(settings.value)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        container.ttsReader.stop()
    }

    class Factory(
        private val container: AppContainer,
        private val categoryId: String,
        private val questionLimit: Int
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            QuizViewModel(container, categoryId, questionLimit) as T
    }
}
