package com.example.data.model

enum class RevelationType(val arabicLabel: String) {
    MAKKI("مكية"),
    MADANI("مدنية")
}

data class SurahMeta(
    val number: Int,
    val arabicName: String,
    val transliteration: String,
    val ayahCount: Int,
    val revelationType: RevelationType,
    val juzStart: Int
)

data class AyahItem(
    val surahNumber: Int,
    val ayahNumber: Int,
    val textUthmani: String,
    val juzNumber: Int = 1,
    val pageNumber: Int = 1
)

data class Reciter(
    val id: String,
    val arabicName: String,
    val styleArabic: String,
    val countryArabic: String,
    val everyAyahFolder: String
)
