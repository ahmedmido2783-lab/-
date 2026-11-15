package com.example.audio

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.data.catalog.RecitersCatalog
import com.example.data.model.Reciter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RecitationPlaybackState(
    val sessionTag: String = "",
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val isCompleted: Boolean = false,
    val currentSurahNumber: Int = 0,
    val currentAyahNumber: Int = 0,
    val currentIndex: Int = -1,
    val totalItems: Int = 0,
    val repeatSingle: Boolean = false,
    val playbackSpeed: Float = 1.0f,
    val errorMessage: String? = null
)

data class PlayableVerseRef(
    val surahNumber: Int,
    val ayahNumber: Int
)

class RecitationPlayerManager(context: Context) {

    private val player: ExoPlayer = ExoPlayer.Builder(context.applicationContext)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
                .build(),
            true
        )
        .build()

    private val _state = MutableStateFlow(RecitationPlaybackState())
    val state: StateFlow<RecitationPlaybackState> = _state.asStateFlow()

    private var currentItems: List<PlayableVerseRef> = emptyList()

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.update { it.copy(isPlaying = isPlaying) }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        _state.update { it.copy(isBuffering = true, isCompleted = false, errorMessage = null) }
                    }
                    Player.STATE_READY -> {
                        _state.update { it.copy(isBuffering = false, isCompleted = false, errorMessage = null) }
                    }
                    Player.STATE_ENDED -> {
                        _state.update {
                            it.copy(
                                isPlaying = false,
                                isBuffering = false,
                                isCompleted = true
                            )
                        }
                    }
                    Player.STATE_IDLE -> {
                        _state.update { it.copy(isBuffering = false) }
                    }
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                syncCurrentIndexFromPlayer()
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                syncCurrentIndexFromPlayer()
            }

            override fun onPlayerError(error: PlaybackException) {
                _state.update {
                    it.copy(
                        isPlaying = false,
                        isBuffering = false,
                        errorMessage = "تعذّر تشغيل الصوت. تأكد من الاتصال بالإنترنت أو جرّب قارئاً آخر."
                    )
                }
            }
        })
    }

    private fun syncCurrentIndexFromPlayer() {
        val index = player.currentMediaItemIndex
        val ref = currentItems.getOrNull(index)
        _state.update {
            it.copy(
                currentIndex = index,
                currentSurahNumber = ref?.surahNumber ?: it.currentSurahNumber,
                currentAyahNumber = ref?.ayahNumber ?: it.currentAyahNumber,
                errorMessage = null
            )
        }
    }

    fun playSurah(
        surahNumber: Int,
        ayahCount: Int,
        startAyah: Int,
        reciter: Reciter,
        speed: Float = 1.0f,
        repeatSingle: Boolean = false
    ) {
        val refs = (1..ayahCount).map { ayah -> PlayableVerseRef(surahNumber, ayah) }
        val startIndex = (startAyah - 1).coerceIn(0, (refs.size - 1).coerceAtLeast(0))
        playVerses(
            sessionTag = "surah_$surahNumber",
            verses = refs,
            startIndex = startIndex,
            reciter = reciter,
            speed = speed,
            repeatSingle = repeatSingle
        )
    }

    fun playVerses(
        sessionTag: String,
        verses: List<PlayableVerseRef>,
        startIndex: Int,
        reciter: Reciter,
        speed: Float = 1.0f,
        repeatSingle: Boolean = false
    ) {
        if (verses.isEmpty()) return
        currentItems = verses
        val safeStart = startIndex.coerceIn(0, verses.lastIndex)

        val mediaItems = verses.map { ref ->
            val url = RecitersCatalog.buildAyahAudioUrl(reciter, ref.surahNumber, ref.ayahNumber)
            MediaItem.Builder()
                .setMediaId("${ref.surahNumber}:${ref.ayahNumber}")
                .setUri(url)
                .build()
        }

        player.repeatMode = if (repeatSingle) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
        player.setPlaybackSpeed(speed.coerceIn(0.5f, 2.0f))
        player.setMediaItems(mediaItems, safeStart, 0L)
        player.prepare()
        player.play()

        val initialRef = verses[safeStart]
        _state.update {
            RecitationPlaybackState(
                sessionTag = sessionTag,
                isPlaying = true,
                isBuffering = true,
                isCompleted = false,
                currentSurahNumber = initialRef.surahNumber,
                currentAyahNumber = initialRef.ayahNumber,
                currentIndex = safeStart,
                totalItems = verses.size,
                repeatSingle = repeatSingle,
                playbackSpeed = speed,
                errorMessage = null
            )
        }
    }

    fun playSingleSample(reciter: Reciter) {
        playVerses(
            sessionTag = "sample_${reciter.id}",
            verses = listOf(PlayableVerseRef(1, 2)), // Al-Hamdu lillahi Rabbil-Alamin
            startIndex = 0,
            reciter = reciter,
            speed = 1.0f,
            repeatSingle = false
        )
    }

    fun seekToIndex(index: Int) {
        if (index in currentItems.indices) {
            player.seekTo(index, 0L)
            if (!player.isPlaying) {
                player.play()
            }
            syncCurrentIndexFromPlayer()
        }
    }

    fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
        } else {
            if (player.playbackState == Player.STATE_ENDED) {
                player.seekTo(0, 0L)
            }
            player.play()
        }
    }

    fun next() {
        if (player.hasNextMediaItem()) {
            player.seekToNextMediaItem()
            player.play()
        }
    }

    fun previous() {
        if (player.hasPreviousMediaItem()) {
            player.seekToPreviousMediaItem()
            player.play()
        }
    }

    fun setRepeatSingle(repeat: Boolean) {
        player.repeatMode = if (repeat) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
        _state.update { it.copy(repeatSingle = repeat) }
    }

    fun setPlaybackSpeed(speed: Float) {
        val clamped = speed.coerceIn(0.75f, 1.5f)
        player.setPlaybackSpeed(clamped)
        _state.update { it.copy(playbackSpeed = clamped) }
    }

    fun stop() {
        player.stop()
        currentItems = emptyList()
        _state.value = RecitationPlaybackState()
    }

    fun release() {
        player.release()
    }
}
