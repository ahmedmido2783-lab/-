package com.example.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.AppThemeMode
import com.example.data.model.MushafDisplayMode
import com.example.data.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "noor_settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val RECITER_ID = stringPreferencesKey("selected_reciter_id")
        val FONT_SIZE = floatPreferencesKey("quran_font_size_sp")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DISPLAY_MODE = stringPreferencesKey("mushaf_display_mode")
        val AUTO_SCROLL = booleanPreferencesKey("auto_scroll")
        val REPEAT_AYAH = booleanPreferencesKey("repeat_ayah")
        val PLAYBACK_SPEED = floatPreferencesKey("playback_speed")
        val QUIZ_TIME = intPreferencesKey("quiz_time_seconds")
        val QUIZ_AUTO_READ = booleanPreferencesKey("quiz_auto_read")
        val TTS_RATE = floatPreferencesKey("tts_speech_rate")
        val LAST_SURAH = intPreferencesKey("last_read_surah")
        val LAST_AYAH = intPreferencesKey("last_read_ayah")
        val HIGH_SCORE = intPreferencesKey("high_score")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            selectedReciterId = prefs[Keys.RECITER_ID] ?: "alafasy",
            quranFontSizeSp = prefs[Keys.FONT_SIZE] ?: 26f,
            themeMode = AppThemeMode.entries.firstOrNull { it.id == prefs[Keys.THEME_MODE] }
                ?: AppThemeMode.CLASSIC_PARCHMENT,
            mushafDisplayMode = MushafDisplayMode.entries.firstOrNull { it.id == prefs[Keys.DISPLAY_MODE] }
                ?: MushafDisplayMode.CONTINUOUS_MUSHAF,
            autoScrollToRedVerse = prefs[Keys.AUTO_SCROLL] ?: true,
            repeatSingleAyah = prefs[Keys.REPEAT_AYAH] ?: false,
            playbackSpeed = prefs[Keys.PLAYBACK_SPEED] ?: 1.0f,
            quizTimeSeconds = prefs[Keys.QUIZ_TIME] ?: 30,
            quizAutoReadQuestion = prefs[Keys.QUIZ_AUTO_READ] ?: true,
            ttsSpeechRate = prefs[Keys.TTS_RATE] ?: 0.95f,
            lastReadSurah = prefs[Keys.LAST_SURAH] ?: 1,
            lastReadAyah = prefs[Keys.LAST_AYAH] ?: 1,
            highScore = prefs[Keys.HIGH_SCORE] ?: 0
        )
    }

    suspend fun setReciter(id: String) {
        context.dataStore.edit { it[Keys.RECITER_ID] = id }
    }

    suspend fun setFontSize(sizeSp: Float) {
        context.dataStore.edit { it[Keys.FONT_SIZE] = sizeSp.coerceIn(18f, 40f) }
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.id }
    }

    suspend fun setMushafDisplayMode(mode: MushafDisplayMode) {
        context.dataStore.edit { it[Keys.DISPLAY_MODE] = mode.id }
    }

    suspend fun setAutoScroll(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_SCROLL] = enabled }
    }

    suspend fun setRepeatSingleAyah(enabled: Boolean) {
        context.dataStore.edit { it[Keys.REPEAT_AYAH] = enabled }
    }

    suspend fun setPlaybackSpeed(speed: Float) {
        context.dataStore.edit { it[Keys.PLAYBACK_SPEED] = speed.coerceIn(0.75f, 1.5f) }
    }

    suspend fun setQuizTimeSeconds(seconds: Int) {
        context.dataStore.edit { it[Keys.QUIZ_TIME] = seconds }
    }

    suspend fun setQuizAutoRead(enabled: Boolean) {
        context.dataStore.edit { it[Keys.QUIZ_AUTO_READ] = enabled }
    }

    suspend fun setTtsSpeechRate(rate: Float) {
        context.dataStore.edit { it[Keys.TTS_RATE] = rate.coerceIn(0.6f, 1.4f) }
    }

    suspend fun updateLastRead(surah: Int, ayah: Int) {
        context.dataStore.edit {
            it[Keys.LAST_SURAH] = surah
            it[Keys.LAST_AYAH] = ayah
        }
    }

    suspend fun updateHighScoreIfBetter(score: Int) {
        context.dataStore.edit {
            val current = it[Keys.HIGH_SCORE] ?: 0
            if (score > current) it[Keys.HIGH_SCORE] = score
        }
    }
}
