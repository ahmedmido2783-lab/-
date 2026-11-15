package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.AppContainer
import com.example.audio.PlayableVerseRef
import com.example.audio.RecitationPlaybackState
import com.example.data.catalog.RecitersCatalog
import com.example.data.catalog.RuqyahCatalog
import com.example.data.model.RuqyahAudioSource
import com.example.data.model.RuqyahProgram
import com.example.data.model.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RuqyahPlayerViewModel(
    private val container: AppContainer,
    private val programId: String
) : ViewModel() {

    val program: RuqyahProgram = RuqyahCatalog.getById(programId)
    private val sessionTag = "ruqyah_$programId"

    val settings: StateFlow<UserSettings> = container.settingsRepository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    private val isQuranProgram: Boolean =
        program.segments.all { it.audioSource is RuqyahAudioSource.QuranVerse }

    private val _ttsActiveIndex = MutableStateFlow(-1)
    private val _ttsPlaying = MutableStateFlow(false)

    val activeSegmentIndex: StateFlow<Int> = combine(
        container.playerManager.state,
        _ttsActiveIndex
    ) { playback: RecitationPlaybackState, ttsIdx: Int ->
        if (isQuranProgram) {
            if (playback.sessionTag == sessionTag) playback.currentIndex else -1
        } else {
            ttsIdx
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), -1)

    val isPlaying: StateFlow<Boolean> = combine(
        container.playerManager.state,
        _ttsPlaying
    ) { playback, ttsActive ->
        if (isQuranProgram) {
            playback.sessionTag == sessionTag && playback.isPlaying
        } else {
            ttsActive
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isBuffering: StateFlow<Boolean> = combine(
        container.playerManager.state,
        _ttsPlaying
    ) { playback, _ ->
        isQuranProgram && playback.sessionTag == sessionTag && playback.isBuffering
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val errorMessage: StateFlow<String?> = combine(
        container.playerManager.state,
        _ttsPlaying
    ) { playback, _ ->
        if (isQuranProgram && playback.sessionTag == sessionTag) playback.errorMessage else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun playFromSegment(index: Int = 0) {
        val safeIdx = index.coerceIn(0, program.segments.lastIndex)
        if (isQuranProgram) {
            container.ttsReader.stop()
            _ttsPlaying.value = false
            val refs = program.segments.mapNotNull { seg ->
                (seg.audioSource as? RuqyahAudioSource.QuranVerse)?.let {
                    PlayableVerseRef(it.surahNumber, it.ayahNumber)
                }
            }
            val currentSettings = settings.value
            val reciter = RecitersCatalog.getById(currentSettings.selectedReciterId)
            container.playerManager.playVerses(
                sessionTag = sessionTag,
                verses = refs,
                startIndex = safeIdx,
                reciter = reciter,
                speed = currentSettings.playbackSpeed,
                repeatSingle = false
            )
        } else {
            container.playerManager.stop()
            speakSegmentRecursively(safeIdx)
        }
    }

    private fun speakSegmentRecursively(index: Int) {
        if (index !in program.segments.indices) {
            _ttsPlaying.value = false
            _ttsActiveIndex.value = -1
            return
        }
        val segment = program.segments[index]
        val spoken = (segment.audioSource as? RuqyahAudioSource.SpokenDua)?.ttsText ?: segment.textArabic
        _ttsActiveIndex.value = index
        _ttsPlaying.value = true
        container.ttsReader.speak(
            text = spoken,
            utteranceId = "ruqyah_${programId}_$index",
            speechRate = settings.value.ttsSpeechRate,
            onDone = {
                if (_ttsPlaying.value) {
                    val next = index + 1
                    if (next < program.segments.size) {
                        speakSegmentRecursively(next)
                    } else {
                        _ttsPlaying.value = false
                    }
                }
            }
        )
    }

    fun togglePlayPause() {
        if (isQuranProgram) {
            val pb = container.playerManager.state.value
            if (pb.sessionTag == sessionTag && pb.errorMessage == null) {
                container.playerManager.togglePlayPause()
            } else {
                playFromSegment(0)
            }
        } else {
            if (_ttsPlaying.value) {
                _ttsPlaying.value = false
                container.ttsReader.stop()
            } else {
                val startIdx = if (_ttsActiveIndex.value in program.segments.indices) _ttsActiveIndex.value else 0
                playFromSegment(startIdx)
            }
        }
    }

    fun nextSegment() {
        if (isQuranProgram) {
            container.playerManager.next()
        } else {
            val next = (_ttsActiveIndex.value + 1).coerceAtMost(program.segments.lastIndex)
            playFromSegment(next)
        }
    }

    fun previousSegment() {
        if (isQuranProgram) {
            container.playerManager.previous()
        } else {
            val prev = (_ttsActiveIndex.value - 1).coerceAtLeast(0)
            playFromSegment(prev)
        }
    }

    fun switchReciterAndResume(reciterId: String) {
        viewModelScope.launch {
            container.settingsRepository.setReciter(reciterId)
            if (isQuranProgram) {
                val currentIdx = activeSegmentIndex.value.coerceAtLeast(0)
                playFromSegment(currentIdx)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (!isQuranProgram) {
            container.ttsReader.stop()
        }
    }

    class Factory(
        private val container: AppContainer,
        private val programId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            RuqyahPlayerViewModel(container, programId) as T
    }
}
