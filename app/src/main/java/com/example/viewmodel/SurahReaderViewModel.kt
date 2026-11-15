package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.AppContainer
import com.example.ai.TafsirUiState
import com.example.audio.RecitationPlaybackState
import com.example.data.catalog.RecitersCatalog
import com.example.data.catalog.SurahCatalog
import com.example.data.model.AyahItem
import com.example.data.model.SurahMeta
import com.example.data.model.UserSettings
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface SurahVersesUiState {
    data object Loading : SurahVersesUiState
    data class Success(val surah: SurahMeta, val verses: List<AyahItem>) : SurahVersesUiState
    data class Error(val surah: SurahMeta, val message: String) : SurahVersesUiState
}

class SurahReaderViewModel(
    private val container: AppContainer,
    private val surahNumber: Int,
    private val initialAyah: Int
) : ViewModel() {

    private val surahMeta: SurahMeta = SurahCatalog.getByNumber(surahNumber)

    private val _uiState = MutableStateFlow<SurahVersesUiState>(SurahVersesUiState.Loading)
    val uiState: StateFlow<SurahVersesUiState> = _uiState.asStateFlow()

    private val _tafsirState = MutableStateFlow<TafsirUiState>(TafsirUiState.Idle)
    val tafsirState: StateFlow<TafsirUiState> = _tafsirState.asStateFlow()

    private var tafsirJob: Job? = null

    val isBookmarked: StateFlow<Boolean> =
        container.quranRepository.observeIsBookmarked(surahNumber)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val settings: StateFlow<UserSettings> = container.settingsRepository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    val playbackState: StateFlow<RecitationPlaybackState> = container.playerManager.state

    init {
        loadSurah()
        viewModelScope.launch {
            container.settingsRepository.updateLastRead(surahNumber, initialAyah)
        }
    }

    fun loadSurah() {
        viewModelScope.launch {
            _uiState.value = SurahVersesUiState.Loading
            val res = container.quranRepository.getSurahVerses(surahNumber)
            res.onSuccess { verses ->
                _uiState.value = SurahVersesUiState.Success(surahMeta, verses)
            }.onFailure {
                _uiState.value = SurahVersesUiState.Error(
                    surah = surahMeta,
                    message = "تعذّر تحميل نص السورة. تأكد من اتصالك بالإنترنت ثم أعد المحاولة."
                )
            }
        }
    }

    fun explainActiveOrFirstVerse() {
        val currentVerses = (_uiState.value as? SurahVersesUiState.Success)?.verses ?: return
        val activeNum = if (playbackState.value.sessionTag == "surah_$surahNumber" &&
            playbackState.value.currentAyahNumber > 0
        ) {
            playbackState.value.currentAyahNumber
        } else {
            initialAyah.coerceIn(1, currentVerses.size)
        }
        val ayah = currentVerses.firstOrNull { it.ayahNumber == activeNum } ?: currentVerses.firstOrNull() ?: return
        tafsirJob?.cancel()
        tafsirJob = viewModelScope.launch {
            container.geminiAssistant.streamVerseTafsir(
                surahName = surahMeta.arabicName,
                ayahNumber = ayah.ayahNumber,
                ayahText = ayah.textUthmani
            ).collect { state ->
                _tafsirState.value = state
            }
        }
    }

    fun askQuranQuestion(question: String) {
        val trimmed = question.trim()
        if (trimmed.isEmpty()) return
        tafsirJob?.cancel()
        tafsirJob = viewModelScope.launch {
            container.geminiAssistant.streamScholarlyAnswer(
                "بخصوص سورة ${surahMeta.arabicName}: $trimmed"
            ).collect { state ->
                _tafsirState.value = state
            }
        }
    }

    fun dismissTafsir() {
        tafsirJob?.cancel()
        _tafsirState.value = TafsirUiState.Idle
    }

    fun playFromAyah(ayahNumber: Int = 1) {
        container.ttsReader.stop()
        val currentSettings = settings.value
        val reciter = RecitersCatalog.getById(currentSettings.selectedReciterId)
        val expectedSession = "surah_$surahNumber"
        val currentPlayback = playbackState.value

        if (currentPlayback.sessionTag == expectedSession &&
            !currentPlayback.isCompleted &&
            currentPlayback.currentAyahNumber == ayahNumber &&
            currentPlayback.errorMessage == null
        ) {
            container.playerManager.togglePlayPause()
        } else {
            container.playerManager.playSurah(
                surahNumber = surahNumber,
                ayahCount = surahMeta.ayahCount,
                startAyah = ayahNumber,
                reciter = reciter,
                speed = currentSettings.playbackSpeed,
                repeatSingle = currentSettings.repeatSingleAyah
            )
        }
        saveReadingProgress(ayahNumber)
    }

    fun togglePlayPause() {
        val currentPlayback = playbackState.value
        val expectedSession = "surah_$surahNumber"
        if (currentPlayback.sessionTag == expectedSession && currentPlayback.errorMessage == null) {
            container.playerManager.togglePlayPause()
        } else {
            playFromAyah(initialAyah.coerceAtLeast(1))
        }
    }

    fun switchReciterAndResume(reciterId: String) {
        viewModelScope.launch {
            container.settingsRepository.setReciter(reciterId)
            val currentPlayback = playbackState.value
            val expectedSession = "surah_$surahNumber"
            val ayahToResume = if (currentPlayback.sessionTag == expectedSession && currentPlayback.currentAyahNumber > 0) {
                currentPlayback.currentAyahNumber
            } else {
                1
            }
            val reciter = RecitersCatalog.getById(reciterId)
            val currentSettings = settings.value
            container.playerManager.playSurah(
                surahNumber = surahNumber,
                ayahCount = surahMeta.ayahCount,
                startAyah = ayahToResume,
                reciter = reciter,
                speed = currentSettings.playbackSpeed,
                repeatSingle = currentSettings.repeatSingleAyah
            )
        }
    }

    fun nextAyah() = container.playerManager.next()

    fun previousAyah() = container.playerManager.previous()

    fun toggleRepeatAyah() {
        val nextValue = !settings.value.repeatSingleAyah
        viewModelScope.launch {
            container.settingsRepository.setRepeatSingleAyah(nextValue)
            container.playerManager.setRepeatSingle(nextValue)
        }
    }

    fun cyclePlaybackSpeed() {
        val speeds = listOf(0.75f, 1.0f, 1.25f, 1.5f)
        val current = settings.value.playbackSpeed
        val idx = speeds.indexOfFirst { kotlin.math.abs(it - current) < 0.05f }
        val nextSpeed = speeds[(idx + 1) % speeds.size]
        viewModelScope.launch {
            container.settingsRepository.setPlaybackSpeed(nextSpeed)
            container.playerManager.setPlaybackSpeed(nextSpeed)
        }
    }

    fun toggleBookmark() {
        val activeAyah = if (playbackState.value.sessionTag == "surah_$surahNumber" &&
            playbackState.value.currentAyahNumber > 0
        ) {
            playbackState.value.currentAyahNumber
        } else {
            initialAyah
        }
        viewModelScope.launch {
            container.quranRepository.toggleBookmark(surahNumber, activeAyah, isBookmarked.value)
        }
    }

    fun saveReadingProgress(ayahNumber: Int) {
        viewModelScope.launch {
            container.settingsRepository.updateLastRead(surahNumber, ayahNumber)
        }
    }

    class Factory(
        private val container: AppContainer,
        private val surahNumber: Int,
        private val initialAyah: Int
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SurahReaderViewModel(container, surahNumber, initialAyah) as T
    }
}
