package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.AppContainer
import com.example.audio.RecitationPlaybackState
import com.example.data.catalog.RecitersCatalog
import com.example.data.local.BookmarkEntity
import com.example.data.model.AppThemeMode
import com.example.data.model.MushafDisplayMode
import com.example.data.model.Reciter
import com.example.data.model.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class OfflineDownloadUiState(
    val isDownloading: Boolean = false,
    val progress: Float = 0f,
    val message: String? = null,
    val isError: Boolean = false
)

class MainViewModel(private val container: AppContainer) : ViewModel() {

    val settings: StateFlow<UserSettings> = container.settingsRepository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    val bookmarks: StateFlow<List<BookmarkEntity>> = container.quranRepository.observeBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cachedSurahCount: StateFlow<Int> = container.quranRepository.observeCachedSurahCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val playbackState: StateFlow<RecitationPlaybackState> = container.playerManager.state

    val ttsSpeaking: StateFlow<Boolean> = container.ttsReader.isSpeaking

    private val _downloadState = MutableStateFlow(OfflineDownloadUiState())
    val downloadState: StateFlow<OfflineDownloadUiState> = _downloadState.asStateFlow()

    fun setReciter(reciterId: String) {
        viewModelScope.launch {
            container.settingsRepository.setReciter(reciterId)
        }
    }

    fun playReciterSample(reciter: Reciter) {
        container.ttsReader.stop()
        container.playerManager.playSingleSample(reciter)
    }

    fun stopAudio() {
        container.playerManager.stop()
        container.ttsReader.stop()
    }

    fun setFontSize(sizeSp: Float) {
        viewModelScope.launch {
            container.settingsRepository.setFontSize(sizeSp)
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            container.settingsRepository.setThemeMode(mode)
        }
    }

    fun setMushafDisplayMode(mode: MushafDisplayMode) {
        viewModelScope.launch {
            container.settingsRepository.setMushafDisplayMode(mode)
        }
    }

    fun setAutoScroll(enabled: Boolean) {
        viewModelScope.launch {
            container.settingsRepository.setAutoScroll(enabled)
        }
    }

    fun setRepeatSingleAyah(enabled: Boolean) {
        viewModelScope.launch {
            container.settingsRepository.setRepeatSingleAyah(enabled)
            container.playerManager.setRepeatSingle(enabled)
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        viewModelScope.launch {
            container.settingsRepository.setPlaybackSpeed(speed)
            container.playerManager.setPlaybackSpeed(speed)
        }
    }

    fun setQuizTimeSeconds(seconds: Int) {
        viewModelScope.launch {
            container.settingsRepository.setQuizTimeSeconds(seconds)
        }
    }

    fun setQuizAutoRead(enabled: Boolean) {
        viewModelScope.launch {
            container.settingsRepository.setQuizAutoRead(enabled)
        }
    }

    fun setTtsSpeechRate(rate: Float) {
        viewModelScope.launch {
            container.settingsRepository.setTtsSpeechRate(rate)
        }
    }

    fun testTtsVoice() {
        val rate = settings.value.ttsSpeechRate
        container.playerManager.stop()
        container.ttsReader.speak(
            text = "مرحباً بكم في تطبيق نور القرآن، هذا اختبار لسرعة قارئ الأسئلة والأدعية.",
            utteranceId = "tts_test_${System.currentTimeMillis()}",
            speechRate = rate
        )
    }

    fun downloadFullQuran() {
        if (_downloadState.value.isDownloading) return
        viewModelScope.launch {
            _downloadState.value = OfflineDownloadUiState(
                isDownloading = true,
                progress = 0.05f,
                message = "جاري الاتصال لتحميل المصحف الشريف كاملاً (١١٤ سورة)..."
            )
            val result = container.quranRepository.downloadEntireQuran { progress ->
                _downloadState.value = _downloadState.value.copy(
                    progress = progress,
                    message = if (progress < 0.6f) "جاري تحميل الآيات الكريمة..." else "جاري حفظ المصحف في الذاكرة المحلية..."
                )
            }
            result.onSuccess { count ->
                _downloadState.value = OfflineDownloadUiState(
                    isDownloading = false,
                    progress = 1f,
                    message = "تم تحميل وحفظ $count سورة بنجاح للقراءة بدون إنترنت.",
                    isError = false
                )
            }.onFailure {
                _downloadState.value = OfflineDownloadUiState(
                    isDownloading = false,
                    progress = 0f,
                    message = "تعذّر التحميل. يرجى التحقق من اتصال الإنترنت والمحاولة مرة أخرى.",
                    isError = true
                )
            }
        }
    }

    fun clearOfflineCache() {
        viewModelScope.launch {
            container.quranRepository.clearOfflineCache()
            _downloadState.value = OfflineDownloadUiState(
                message = "تم مسح السور المحفوظة محلياً."
            )
        }
    }

    fun currentReciter(): Reciter = RecitersCatalog.getById(settings.value.selectedReciterId)

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = MainViewModel(container) as T
    }
}
