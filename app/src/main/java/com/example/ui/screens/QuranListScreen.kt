package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.catalog.RecitersCatalog
import com.example.data.catalog.SurahCatalog
import com.example.data.local.BookmarkEntity
import com.example.data.model.RevelationType
import com.example.data.model.SurahMeta
import com.example.data.model.UserSettings
import com.example.ui.components.ReciterPickerSheet
import com.example.ui.components.SurahNumberMedallion
import com.example.ui.components.toArabicDigits
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.LocalNoorColors

private enum class SurahFilter(val label: String) {
    ALL("جميع السور (١١٤)"),
    MAKKI("مكية"),
    MADANI("مدنية"),
    BOOKMARKED("المحفوظة")
}

@Composable
fun QuranListScreen(
    settings: UserSettings,
    bookmarks: List<BookmarkEntity>,
    onOpenSurah: (surahNumber: Int, initialAyah: Int) -> Unit,
    onSelectReciter: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val noorColors = LocalNoorColors.current
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var activeFilter by rememberSaveable { mutableStateOf(SurahFilter.ALL) }
    var showReciterSheet by remember { mutableStateOf(false) }

    val bookmarkedNumbers = remember(bookmarks) { bookmarks.map { it.surahNumber }.toSet() }
    val currentReciter = remember(settings.selectedReciterId) {
        RecitersCatalog.getById(settings.selectedReciterId)
    }
    val lastReadSurahMeta = remember(settings.lastReadSurah) {
        SurahCatalog.getByNumber(settings.lastReadSurah)
    }

    val filteredSurahs = remember(searchQuery, activeFilter, bookmarkedNumbers) {
        SurahCatalog.allSurahs.filter { surah ->
            val matchesFilter = when (activeFilter) {
                SurahFilter.ALL -> true
                SurahFilter.MAKKI -> surah.revelationType == RevelationType.MAKKI
                SurahFilter.MADANI -> surah.revelationType == RevelationType.MADANI
                SurahFilter.BOOKMARKED -> surah.number in bookmarkedNumbers
            }
            val q = searchQuery.trim()
            val matchesSearch = q.isEmpty() ||
                surah.arabicName.contains(q) ||
                surah.transliteration.contains(q, ignoreCase = true) ||
                surah.number.toString() == q ||
                surah.number.toArabicDigits() == q
            matchesFilter && matchesSearch
        }
    }

    if (showReciterSheet) {
        ReciterPickerSheet(
            selectedReciterId = settings.selectedReciterId,
            onSelectReciter = { onSelectReciter(it.id) },
            onDismiss = { showReciterSheet = false }
        )
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 720.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Hero Banner
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    tonalElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(176.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_quran_banner),
                            contentDescription = "القرآن الكريم",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xE0072E22),
                                            Color(0x990B4F3A),
                                            Color(0x440B4F3A)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color(0x33FFFFFF)
                                ) {
                                    Text(
                                        text = "المصحف الشريف كاملاً",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFFFBE2A2),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Filled.MenuBook,
                                    contentDescription = null,
                                    tint = Color(0xFFFBE2A2),
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "نور القرآن الكريم",
                                    style = MaterialTheme.typography.displayLarge.copy(
                                        fontFamily = AmiriFontFamily,
                                        fontSize = 30.sp
                                    ),
                                    color = Color.White
                                )
                                Text(
                                    text = "تورق صفحات المصحف الشريف • تظليل الآية المقروءة بالأحمر • ٢٠ قارئاً",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFE7F2EC),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Resume Last Read + Active Sheikh Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable {
                                onOpenSurah(settings.lastReadSurah, settings.lastReadAyah)
                            }
                            .testTag("resume_reading_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.PlayArrow,
                                        contentDescription = "متابعة القراءة",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "متابعة القراءة",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f)
                                )
                                Text(
                                    text = "سورة ${lastReadSurahMeta.arabicName} • آية ${settings.lastReadAyah.toArabicDigits()}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = noorColors.goldContainer,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { showReciterSheet = true }
                            .testTag("active_reciter_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = noorColors.goldAccent,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.Mic,
                                        contentDescription = "تغيير القارئ",
                                        tint = Color.White
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "القارئ الحالي",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = noorColors.onGoldContainer.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = currentReciter.arabicName.removePrefix("الشيخ "),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = noorColors.onGoldContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("surah_search_input"),
                    placeholder = {
                        Text(
                            text = "ابحث باسم السورة أو رقمها...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "مسح البحث")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }

            // Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(SurahFilter.entries) { filter ->
                        val selected = filter == activeFilter
                        FilterChip(
                            selected = selected,
                            onClick = { activeFilter = filter },
                            label = {
                                Text(
                                    text = filter.label,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("filter_${filter.name.lowercase()}")
                        )
                    }
                }
            }

            // Empty state if bookmark or search empty
            if (filteredSurahs.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "لا توجد سور مطابقة",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "جرّب تغيير كلمة البحث أو التصفية لعرض جميع السور الـ ١١٤.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredSurahs, key = { it.number }) { surah ->
                    SurahListCard(
                        surah = surah,
                        isBookmarked = surah.number in bookmarkedNumbers,
                        onClick = { onOpenSurah(surah.number, 1) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun SurahListCard(
    surah: SurahMeta,
    isBookmarked: Boolean,
    onClick: () -> Unit
) {
    val noorColors = LocalNoorColors.current
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, noorColors.goldAccent.copy(alpha = 0.28f)),
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("surah_card_${surah.number}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SurahNumberMedallion(number = surah.number)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "سورة ${surah.arabicName}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = AmiriFontFamily,
                            fontSize = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isBookmarked) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Filled.Bookmark,
                            contentDescription = "سورة محفوظة",
                            tint = noorColors.goldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${surah.revelationType.arabicLabel} • ${surah.ayahCount.toArabicDigits()} آية • الجزء ${surah.juzStart.toArabicDigits()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = surah.transliteration,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "فتح وقراءة ${surah.arabicName}",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
