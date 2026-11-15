package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "verses",
    indices = [Index(value = ["surahNumber", "ayahNumber"], unique = true)]
)
data class VerseEntity(
    @PrimaryKey val id: Int, // surahNumber * 1000 + ayahNumber
    val surahNumber: Int,
    val ayahNumber: Int,
    val textUthmani: String,
    val juzNumber: Int,
    val pageNumber: Int
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val surahNumber: Int,
    val ayahNumber: Int,
    val createdAtMillis: Long
)
