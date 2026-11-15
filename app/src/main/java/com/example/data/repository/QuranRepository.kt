package com.example.data.repository

import com.example.data.catalog.OfflineSeedVerses
import com.example.data.catalog.SurahCatalog
import com.example.data.local.BookmarkEntity
import com.example.data.local.QuranDao
import com.example.data.local.VerseEntity
import com.example.data.model.AyahItem
import com.example.data.remote.QuranApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class QuranRepository(
    private val dao: QuranDao,
    private val api: QuranApiService
) {

    fun observeCachedSurahCount(): Flow<Int> = dao.observeCachedSurahCount()

    fun observeBookmarks(): Flow<List<BookmarkEntity>> = dao.observeBookmarks()

    fun observeIsBookmarked(surahNumber: Int): Flow<Boolean> =
        dao.observeIsBookmarked(surahNumber)

    suspend fun toggleBookmark(surahNumber: Int, ayahNumber: Int, currentlyBookmarked: Boolean) {
        withContext(Dispatchers.IO) {
            if (currentlyBookmarked) {
                dao.deleteBookmark(surahNumber)
            } else {
                dao.upsertBookmark(
                    BookmarkEntity(
                        surahNumber = surahNumber,
                        ayahNumber = ayahNumber,
                        createdAtMillis = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    suspend fun getSurahVerses(surahNumber: Int): Result<List<AyahItem>> = withContext(Dispatchers.IO) {
        val expectedCount = SurahCatalog.getByNumber(surahNumber).ayahCount
        val cached = dao.getVersesForSurah(surahNumber)
        if (cached.size >= expectedCount) {
            return@withContext Result.success(
                cached.map {
                    AyahItem(
                        surahNumber = it.surahNumber,
                        ayahNumber = it.ayahNumber,
                        textUthmani = cleanLeadingBasmalah(surahNumber, it.ayahNumber, it.textUthmani),
                        juzNumber = it.juzNumber,
                        pageNumber = it.pageNumber
                    )
                }
            )
        }

        // Check bundled offline seed first
        OfflineSeedVerses.seedSurahs[surahNumber]?.let { seed ->
            if (seed.size >= expectedCount) {
                dao.insertVerses(seed.map {
                    VerseEntity(
                        id = it.surahNumber * 1000 + it.ayahNumber,
                        surahNumber = it.surahNumber,
                        ayahNumber = it.ayahNumber,
                        textUthmani = it.textUthmani,
                        juzNumber = it.juzNumber,
                        pageNumber = it.pageNumber
                    )
                })
                return@withContext Result.success(seed)
            }
        }

        // Fetch from AlQuran Cloud API and persist into Room
        try {
            val response = api.getSurahUthmani(surahNumber)
            val verses = response.data.ayahs.map { dto ->
                val cleaned = cleanLeadingBasmalah(surahNumber, dto.numberInSurah, dto.text)
                VerseEntity(
                    id = surahNumber * 1000 + dto.numberInSurah,
                    surahNumber = surahNumber,
                    ayahNumber = dto.numberInSurah,
                    textUthmani = cleaned,
                    juzNumber = dto.juz,
                    pageNumber = dto.page
                )
            }
            dao.insertVerses(verses)
            Result.success(
                verses.map {
                    AyahItem(
                        surahNumber = it.surahNumber,
                        ayahNumber = it.ayahNumber,
                        textUthmani = it.textUthmani,
                        juzNumber = it.juzNumber,
                        pageNumber = it.pageNumber
                    )
                }
            )
        } catch (e: Exception) {
            if (cached.isNotEmpty()) {
                Result.success(
                    cached.map {
                        AyahItem(it.surahNumber, it.ayahNumber, it.textUthmani, it.juzNumber, it.pageNumber)
                    }
                )
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun downloadEntireQuran(onProgress: (Float) -> Unit): Result<Int> = withContext(Dispatchers.IO) {
        try {
            onProgress(0.15f)
            val response = api.getFullQuranUthmani()
            onProgress(0.65f)
            val allEntities = ArrayList<VerseEntity>(6250)
            for (surah in response.data.surahs) {
                for (dto in surah.ayahs) {
                    val cleaned = cleanLeadingBasmalah(surah.number, dto.numberInSurah, dto.text)
                    allEntities.add(
                        VerseEntity(
                            id = surah.number * 1000 + dto.numberInSurah,
                            surahNumber = surah.number,
                            ayahNumber = dto.numberInSurah,
                            textUthmani = cleaned,
                            juzNumber = dto.juz,
                            pageNumber = dto.page
                        )
                    )
                }
            }
            dao.insertVerses(allEntities)
            onProgress(1.0f)
            Result.success(response.data.surahs.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearOfflineCache() {
        withContext(Dispatchers.IO) {
            dao.clearAllVerses()
        }
    }

    private fun cleanLeadingBasmalah(surahNumber: Int, ayahNumber: Int, text: String): String {
        if (surahNumber == 1 || surahNumber == 9 || ayahNumber != 1) return text.trim()
        val trimmed = text.trim()
        // AlQuran Cloud Uthmani prepends Basmalah on Ayah 1 for surahs 2..114 (except 9)
        val prefixes = listOf(
            "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ ",
            "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ ",
            "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ",
            "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيْمِ "
        )
        for (prefix in prefixes) {
            if (trimmed.startsWith(prefix)) {
                return trimmed.removePrefix(prefix).trim()
            }
        }
        // Generic regex guard in case of slight diacritic differences on the first 4 words
        val regex = Regex("^بِسْمِ\\s+\\S+\\s+\\S+\\s+\\S+\\s+")
        val match = regex.find(trimmed)
        if (match != null && trimmed.length > match.value.length + 2) {
            val prefixWords = match.value
            if (prefixWords.contains("بِسْمِ") && prefixWords.contains("حِيم")) {
                return trimmed.substring(match.value.length).trim()
            }
        }
        return trimmed
    }
}
