package com.example.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SurahResponse(
    val code: Int,
    val status: String,
    val data: SurahDataDto
)

@JsonClass(generateAdapter = true)
data class FullQuranResponse(
    val code: Int,
    val status: String,
    val data: FullQuranDataDto
)

@JsonClass(generateAdapter = true)
data class FullQuranDataDto(
    val surahs: List<SurahDataDto>
)

@JsonClass(generateAdapter = true)
data class SurahDataDto(
    val number: Int,
    val name: String,
    val ayahs: List<AyahDto>
)

@JsonClass(generateAdapter = true)
data class AyahDto(
    val number: Int,
    val text: String,
    val numberInSurah: Int,
    val juz: Int,
    val page: Int
)
