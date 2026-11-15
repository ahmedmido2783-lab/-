package com.example

import android.content.Context
import com.example.audio.ArabicTtsReader
import com.example.audio.RecitationPlayerManager
import com.example.data.local.QuranDatabase
import com.example.data.local.SettingsRepository
import com.example.data.remote.QuranApiService
import com.example.data.repository.QuranRepository

class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    val database: QuranDatabase by lazy { QuranDatabase.getInstance(appContext) }
    val apiService: QuranApiService by lazy { QuranApiService.create() }
    val quranRepository: QuranRepository by lazy {
        QuranRepository(database.quranDao(), apiService)
    }
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(appContext) }
    val playerManager: RecitationPlayerManager by lazy { RecitationPlayerManager(appContext) }
    val ttsReader: ArabicTtsReader by lazy { ArabicTtsReader(appContext) }

    fun release() {
        playerManager.release()
        ttsReader.release()
    }
}
