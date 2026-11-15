package com.example.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object QuranListRoute

@Serializable
object RuqyahListRoute

@Serializable
object QuizHomeRoute

@Serializable
object SettingsHomeRoute

@Serializable
data class SurahReaderRoute(
    val surahNumber: Int,
    val initialAyah: Int = 1
)

@Serializable
data class RuqyahPlayerRoute(
    val programId: String
)

@Serializable
data class QuizSessionRoute(
    val categoryId: String,
    val questionLimit: Int = 10
)

@Serializable
data class SettingDetailRoute(
    val settingKey: String
)
