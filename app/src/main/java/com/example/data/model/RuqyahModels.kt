package com.example.data.model

sealed class RuqyahAudioSource {
    data class QuranVerse(val surahNumber: Int, val ayahNumber: Int) : RuqyahAudioSource()
    data class SpokenDua(val ttsText: String) : RuqyahAudioSource()
}

data class RuqyahSegment(
    val id: String,
    val titleArabic: String,
    val textArabic: String,
    val referenceArabic: String,
    val repeatHint: String = "",
    val audioSource: RuqyahAudioSource
)

data class RuqyahProgram(
    val id: String,
    val titleArabic: String,
    val subtitleArabic: String,
    val durationEstimate: String,
    val categoryBadge: String,
    val segments: List<RuqyahSegment>
)
