package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDao {
    @Query("SELECT * FROM verses WHERE surahNumber = :surahNumber ORDER BY ayahNumber ASC")
    suspend fun getVersesForSurah(surahNumber: Int): List<VerseEntity>

    @Query("SELECT COUNT(*) FROM verses WHERE surahNumber = :surahNumber")
    suspend fun getSurahVerseCount(surahNumber: Int): Int

    @Query("SELECT COUNT(DISTINCT surahNumber) FROM verses")
    fun observeCachedSurahCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerses(verses: List<VerseEntity>)

    @Query("DELETE FROM verses")
    suspend fun clearAllVerses()

    @Query("SELECT * FROM bookmarks ORDER BY createdAtMillis DESC")
    fun observeBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE surahNumber = :surahNumber)")
    fun observeIsBookmarked(surahNumber: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE surahNumber = :surahNumber")
    suspend fun deleteBookmark(surahNumber: Int)
}
