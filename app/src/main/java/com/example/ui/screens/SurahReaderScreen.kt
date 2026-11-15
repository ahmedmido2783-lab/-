package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VerticalAlignCenter
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.catalog.RecitersCatalog
import com.example.data.catalog.SurahCatalog
import com.example.data.model.AyahItem
import com.example.data.model.MushafDisplayMode
import com.example.data.model.SurahMeta
import com.example.ui.components.MushafBookPageFrame
import com.example.ui.components.ReciterPickerSheet
import com.example.ui.components.toArabicDigits
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.AmiriQuranFontFamily
import com.example.ui.theme.LocalNoorColors
import com.example.viewmodel.SurahReaderViewModel
import com.example.viewmodel.SurahVersesUiState
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

private data class MushafPageData(
    val pageIndex: Int,
    val mushafPageNumber: Int,
    val juzNumber: Int,
    val verses: List<AyahItem>,
    val isFirstPageOfSurah: Boolean,
    val isLastPageOfSurah: Boolean
)

private fun buildMushafPages(verses: List<AyahItem>): List<MushafPageData> {
    if (verses.isEmpty()) return emptyList()
    val grouped = mutableListOf<List<AyahItem>>()
    val byPage = verses.groupBy { it.pageNumber }.toSortedMap()
    for ((_, pageVerses) in byPage) {
        if (pageVerses.size <= 14) {
            grouped.add(pageVerses)
        } else {
            grouped.addAll(pageVerses.chunked(12))
        }
    }
    return grouped.mapIndexed { idx, list ->
        MushafPageData(
            pageIndex = idx,
            mushafPageNumber = list.first().pageNumber,
            juzNumber = list.first().juzNumber,
            verses = list,
            isFirstPageOfSurah = idx == 0,
            isLastPageOfSurah = idx == grouped.lastIndex
        )
    }
}

@Composable
fun SurahReaderScreen(
    surahNumber: Int,
    viewModel: SurahReaderViewModel,
    onChangeFontSize: (Float) -> Unit,
    onChangeDisplayMode: (MushafDisplayMode) -> Unit,
    onToggleAutoScroll: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val isBookmarked by viewModel.isBookmarked.collectAsStateWithLifecycle()

    val noorColors = LocalNoorColors.current
    val surahMeta = remember(surahNumber) { SurahCatalog.getByNumber(surahNumber) }
    val currentReciter = remember(settings.selectedReciterId) {
        RecitersCatalog.getById(settings.selectedReciterId)
    }

    val isCurrentSession = playbackState.sessionTag == "surah_$surahNumber"
    val activeAyahNumber = if (isCurrentSession && playbackState.currentAyahNumber > 0) {
        playbackState.currentAyahNumber
    } else {
        0
    }

    var showReciterSheet by remember { mutableStateOf(false) }
    var showFontBar by remember { mutableStateOf(false) }

    if (showReciterSheet) {
        ReciterPickerSheet(
            selectedReciterId = settings.selectedReciterId,
            onSelectReciter = { reciter -> viewModel.switchReciterAndResume(reciter.id) },
            onDismiss = { showReciterSheet = false }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.statusBarsPadding()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("surah_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع"
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "سورة ${surahMeta.arabicName}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = AmiriFontFamily,
                                    fontSize = 23.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${surahMeta.revelationType.arabicLabel} • ${surahMeta.ayahCount.toArabicDigits()} آية",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { showFontBar = !showFontBar },
                            modifier = Modifier.testTag("toggle_font_bar")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.TextFields,
                                contentDescription = "حجم الخط",
                                tint = if (showFontBar) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                val nextMode = if (settings.mushafDisplayMode == MushafDisplayMode.CONTINUOUS_MUSHAF) {
                                    MushafDisplayMode.VERSE_CARDS
                                } else {
                                    MushafDisplayMode.CONTINUOUS_MUSHAF
                                }
                                onChangeDisplayMode(nextMode)
                            },
                            modifier = Modifier.testTag("toggle_display_mode")
                        ) {
                            Icon(
                                imageVector = if (settings.mushafDisplayMode == MushafDisplayMode.CONTINUOUS_MUSHAF) {
                                    Icons.AutoMirrored.Filled.ViewList
                                } else {
                                    Icons.Filled.MenuBook
                                },
                                contentDescription = "تبديل بين صفحات الكتاب والبطاقات",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = { onToggleAutoScroll(!settings.autoScrollToRedVerse) },
                            modifier = Modifier.testTag("toggle_autoscroll")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.VerticalAlignCenter,
                                contentDescription = "تقليب الصفحات تلقائياً مع القارئ",
                                tint = if (settings.autoScrollToRedVerse) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleBookmark() },
                            modifier = Modifier.testTag("toggle_bookmark")
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = "حفظ السورة",
                                tint = if (isBookmarked) noorColors.goldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    AnimatedVisibility(visible = showFontBar) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "حجم الخط: ${settings.quranFontSizeSp.roundToInt().toArabicDigits()}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Slider(
                                value = settings.quranFontSizeSp,
                                onValueChange = onChangeFontSize,
                                valueRange = 18f..40f,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            SurahPlayerDock(
                surahMeta = surahMeta,
                reciterName = currentReciter.arabicName,
                isPlaying = isCurrentSession && playbackState.isPlaying,
                isBuffering = isCurrentSession && playbackState.isBuffering,
                activeAyahNumber = activeAyahNumber,
                repeatSingle = settings.repeatSingleAyah,
                playbackSpeed = settings.playbackSpeed,
                errorMessage = if (isCurrentSession) playbackState.errorMessage else null,
                onTogglePlayPause = { viewModel.togglePlayPause() },
                onNext = { viewModel.nextAyah() },
                onPrevious = { viewModel.previousAyah() },
                onToggleRepeat = { viewModel.toggleRepeatAyah() },
                onCycleSpeed = { viewModel.cyclePlaybackSpeed() },
                onOpenReciterPicker = { showReciterSheet = true }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            when (val state = uiState) {
                is SurahVersesUiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "جاري فتح صفحات سورة ${surahMeta.arabicName}...",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                is SurahVersesUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadSurah() }) {
                            Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "إعادة المحاولة")
                        }
                    }
                }

                is SurahVersesUiState.Success -> {
                    if (settings.mushafDisplayMode == MushafDisplayMode.CONTINUOUS_MUSHAF) {
                        MushafBookPagerView(
                            surah = state.surah,
                            verses = state.verses,
                            activeAyahNumber = activeAyahNumber,
                            fontSizeSp = settings.quranFontSizeSp,
                            autoFlipPages = settings.autoScrollToRedVerse,
                            onAyahClick = { ayahNum -> viewModel.playFromAyah(ayahNum) }
                        )
                    } else {
                        val listState = rememberLazyListState()
                        LaunchedEffect(activeAyahNumber, settings.autoScrollToRedVerse) {
                            if (activeAyahNumber > 0 && settings.autoScrollToRedVerse) {
                                val headerOffset = if (surahNumber != 1 && surahNumber != 9) 2 else 1
                                listState.animateScrollToItem(headerOffset + (activeAyahNumber - 1), -80)
                            }
                        }

                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 760.dp)
                                .testTag("surah_verses_list"),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item(key = "surah_header") {
                                SurahOrnamentalHeader(
                                    surah = state.surah,
                                    activeAyahNumber = activeAyahNumber,
                                    reciterName = currentReciter.arabicName
                                )
                            }

                            if (surahNumber != 1 && surahNumber != 9) {
                                item(key = "basmalah") {
                                    Text(
                                        text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontFamily = AmiriQuranFontFamily,
                                            fontSize = (settings.quranFontSizeSp + 2f).sp,
                                            lineHeight = (settings.quranFontSizeSp * 1.9f).sp
                                        ),
                                        color = MaterialTheme.colorScheme.primary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                    )
                                }
                            }

                            itemsIndexed(
                                items = state.verses,
                                key = { _, item -> "ayah_${item.ayahNumber}" }
                            ) { _, verse ->
                                VerseCardItem(
                                    verse = verse,
                                    isActive = verse.ayahNumber == activeAyahNumber,
                                    fontSizeSp = settings.quranFontSizeSp,
                                    onClick = { viewModel.playFromAyah(verse.ayahNumber) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MushafBookPagerView(
    surah: SurahMeta,
    verses: List<AyahItem>,
    activeAyahNumber: Int,
    fontSizeSp: Float,
    autoFlipPages: Boolean,
    onAyahClick: (Int) -> Unit
) {
    val pages = remember(verses) { buildMushafPages(verses) }
    val pagerState = rememberPagerState(pageCount = { pages.size.coerceAtLeast(1) })
    val scope = rememberCoroutineScope()
    val noorColors = LocalNoorColors.current

    // Automatic page-flipping when the Sheikh recites an Ayah on another page
    LaunchedEffect(activeAyahNumber, autoFlipPages, pages) {
        if (activeAyahNumber > 0 && autoFlipPages && pages.isNotEmpty()) {
            val targetIdx = pages.indexOfFirst { page ->
                page.verses.any { it.ayahNumber == activeAyahNumber }
            }
            if (targetIdx >= 0 && targetIdx != pagerState.currentPage) {
                pagerState.animateScrollToPage(targetIdx)
            }
        }
    }

    if (pages.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 780.dp)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("mushaf_book_pager"),
            pageSpacing = 12.dp
        ) { pageIdx ->
            val pageData = pages[pageIdx]
            val pageOffset = (pagerState.currentPage - pageIdx) + pagerState.currentPageOffsetFraction

            MushafBookPageFrame(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        cameraDistance = 18f * density
                        val clamped = pageOffset.coerceIn(-1f, 1f)
                        rotationY = clamped * -26f
                        val scale = 1f - (abs(clamped) * 0.05f)
                        scaleX = scale
                        scaleY = scale
                        alpha = 1f - (abs(clamped) * 0.22f)
                    }
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Top of Book Page Metadata Ribbon
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "سورة ${surah.arabicName}",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = AmiriFontFamily,
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = noorColors.goldContainer
                        ) {
                            Text(
                                text = "الجزء ${pageData.juzNumber.toArabicDigits()} • صفحة ${(pageIdx + 1).toArabicDigits()} من ${pages.size.toArabicDigits()}",
                                style = MaterialTheme.typography.labelMedium,
                                color = noorColors.onGoldContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }

                    HorizontalDivider(
                        color = noorColors.goldAccent.copy(alpha = 0.45f),
                        thickness = 1.dp
                    )

                    // Scrollable body inside the page frame so large fonts never clip
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (pageData.isFirstPageOfSurah) {
                            // Ornate Surah Banner inside the first page
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, noorColors.goldAccent.copy(alpha = 0.65f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                            ) {
                                Text(
                                    text = "سُورَةُ ${surah.arabicName}",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontFamily = AmiriFontFamily,
                                        fontSize = 26.sp
                                    ),
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            if (surah.number != 1 && surah.number != 9) {
                                Text(
                                    text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontFamily = AmiriQuranFontFamily,
                                        fontSize = (fontSizeSp + 1f).sp,
                                        lineHeight = (fontSizeSp * 1.9f).sp
                                    ),
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 10.dp)
                                )
                            }
                        }

                        BookPageVersesText(
                            verses = pageData.verses,
                            activeAyahNumber = activeAyahNumber,
                            fontSizeSp = fontSizeSp,
                            onAyahClick = onAyahClick
                        )

                        if (pageData.isLastPageOfSurah) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "۞ صَدَقَ اللَّهُ الْعَظِيمُ ۞",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = AmiriFontFamily,
                                    fontSize = 21.sp
                                ),
                                color = noorColors.goldAccent,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    HorizontalDivider(
                        color = noorColors.goldAccent.copy(alpha = 0.35f),
                        thickness = 0.8.dp
                    )

                    // Page-turn footer bar inside the paper leaf
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val hasPrev = pageIdx > 0
                        val hasNext = pageIdx < pages.lastIndex

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (hasPrev) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f) else Color.Transparent,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable(enabled = hasPrev) {
                                    scope.launch { pagerState.animateScrollToPage(pageIdx - 1) }
                                }
                                .testTag("prev_page_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "الصفحة السابقة",
                                    tint = if (hasPrev) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "السابقة",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (hasPrev) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                                )
                            }
                        }

                        Text(
                            text = "اسحب لتورق الصفحة • ${(pageIdx + 1).toArabicDigits()} / ${pages.size.toArabicDigits()}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (hasNext) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f) else Color.Transparent,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable(enabled = hasNext) {
                                    scope.launch { pagerState.animateScrollToPage(pageIdx + 1) }
                                }
                                .testTag("next_page_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "التالية",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (hasNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "الصفحة التالية",
                                    tint = if (hasNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookPageVersesText(
    verses: List<AyahItem>,
    activeAyahNumber: Int,
    fontSizeSp: Float,
    onAyahClick: (Int) -> Unit
) {
    val noorColors = LocalNoorColors.current
    val baseColor = MaterialTheme.colorScheme.onBackground
    val goldColor = noorColors.goldAccent
    val activeRed = noorColors.activeVerseRed
    val activeBg = noorColors.activeVerseContainer

    val annotated = remember(verses, activeAyahNumber, baseColor, activeRed, activeBg, goldColor) {
        buildAnnotatedString {
            verses.forEachIndexed { index, verse ->
                val isPlayingThisVerse = verse.ayahNumber == activeAyahNumber
                val link = LinkAnnotation.Clickable(
                    tag = "ayah_${verse.ayahNumber}",
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = if (isPlayingThisVerse) activeRed else baseColor
                        )
                    ),
                    linkInteractionListener = { onAyahClick(verse.ayahNumber) }
                )
                withLink(link) {
                    withStyle(
                        SpanStyle(
                            color = if (isPlayingThisVerse) activeRed else baseColor,
                            background = if (isPlayingThisVerse) activeBg else Color.Transparent,
                            fontWeight = if (isPlayingThisVerse) FontWeight.Bold else FontWeight.Normal
                        )
                    ) {
                        append(verse.textUthmani)
                        append(" ")
                    }
                    withStyle(
                        SpanStyle(
                            color = if (isPlayingThisVerse) activeRed else goldColor,
                            background = if (isPlayingThisVerse) activeBg else Color.Transparent,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("﴿${verse.ayahNumber.toArabicDigits()}﴾")
                    }
                }
                if (index < verses.lastIndex) {
                    append("  ")
                }
            }
        }
    }

    Text(
        text = annotated,
        style = MaterialTheme.typography.bodyLarge.copy(
            fontFamily = AmiriQuranFontFamily,
            fontSize = fontSizeSp.sp,
            lineHeight = (fontSizeSp * 2.18f).sp,
            textAlign = TextAlign.Justify
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SurahOrnamentalHeader(
    surah: SurahMeta,
    activeAyahNumber: Int,
    reciterName: String
) {
    val noorColors = LocalNoorColors.current
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.2.dp, noorColors.goldAccent.copy(alpha = 0.55f)),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "سُورَةُ ${surah.arabicName}",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontFamily = AmiriFontFamily,
                        fontSize = 30.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${surah.revelationType.arabicLabel} • ${surah.ayahCount.toArabicDigits()} آية • الجزء ${surah.juzStart.toArabicDigits()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (activeAyahNumber > 0) noorColors.activeVerseContainer else noorColors.goldContainer
                ) {
                    Text(
                        text = if (activeAyahNumber > 0) {
                            "جاري التلاوة: الآية ${activeAyahNumber.toArabicDigits()} — اضغط على أي آية للانتقال"
                        } else {
                            "القارئ: $reciterName • اضغط على أي آية للتلاوة باللون الأحمر"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = if (activeAyahNumber > 0) noorColors.activeVerseRed else noorColors.onGoldContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun VerseCardItem(
    verse: AyahItem,
    isActive: Boolean,
    fontSizeSp: Float,
    onClick: () -> Unit
) {
    val noorColors = LocalNoorColors.current
    val containerColor by animateColorAsState(
        targetValue = if (isActive) noorColors.activeVerseContainer else MaterialTheme.colorScheme.surface,
        label = "verse_card_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isActive) noorColors.activeVerseRed else MaterialTheme.colorScheme.onSurface,
        label = "verse_card_text"
    )

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = containerColor,
        border = BorderStroke(
            width = if (isActive) 1.6.dp else 1.dp,
            color = if (isActive) noorColors.activeVerseRed else noorColors.goldAccent.copy(alpha = 0.35f)
        ),
        tonalElevation = if (isActive) 4.dp else 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("verse_card_${verse.ayahNumber}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isActive) noorColors.activeVerseRed else MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "آية ${verse.ayahNumber.toArabicDigits()}",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isActive) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                if (isActive) {
                    Text(
                        text = "يُقرأ الآن",
                        style = MaterialTheme.typography.labelMedium,
                        color = noorColors.activeVerseRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "${verse.textUthmani} ﴿${verse.ayahNumber.toArabicDigits()}﴾",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = AmiriQuranFontFamily,
                    fontSize = fontSizeSp.sp,
                    lineHeight = (fontSizeSp * 2.0f).sp
                ),
                color = textColor,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SurahPlayerDock(
    surahMeta: SurahMeta,
    reciterName: String,
    isPlaying: Boolean,
    isBuffering: Boolean,
    activeAyahNumber: Int,
    repeatSingle: Boolean,
    playbackSpeed: Float,
    errorMessage: String?,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleRepeat: () -> Unit,
    onCycleSpeed: () -> Unit,
    onOpenReciterPicker: () -> Unit
) {
    val noorColors = LocalNoorColors.current
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 10.dp,
        border = BorderStroke(0.8.dp, noorColors.goldAccent.copy(alpha = 0.35f)),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            if (errorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = noorColors.wrongContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.labelMedium,
                        color = noorColors.wrongRed,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = BorderStroke(1.dp, noorColors.goldAccent.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .clickable(onClick = onOpenReciterPicker)
                        .testTag("player_reciter_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "تغيير القارئ",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = reciterName,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (activeAyahNumber > 0) {
                        "الآية ${activeAyahNumber.toArabicDigits()} / ${surahMeta.ayahCount.toArabicDigits()}"
                    } else {
                        "سورة ${surahMeta.arabicName}"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = if (activeAyahNumber > 0) noorColors.activeVerseRed else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable(onClick = onCycleSpeed)
                        .testTag("speed_chip")
                ) {
                    Text(
                        text = "${playbackSpeed}x",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }

                IconButton(
                    onClick = onPrevious,
                    modifier = Modifier.testTag("prev_ayah_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipPrevious,
                        contentDescription = "الآية السابقة"
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    border = BorderStroke(1.5.dp, noorColors.goldBright.copy(alpha = 0.65f)),
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onTogglePlayPause)
                        .testTag("surah_play_pause_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isBuffering) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "إيقاف مؤقت" else "تشغيل التلاوة",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onNext,
                    modifier = Modifier.testTag("next_ayah_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = "الآية التالية"
                    )
                }

                IconButton(
                    onClick = onToggleRepeat,
                    modifier = Modifier.testTag("repeat_ayah_button")
                ) {
                    Icon(
                        imageVector = if (repeatSingle) Icons.Filled.RepeatOne else Icons.Filled.Repeat,
                        contentDescription = "تكرار الآية",
                        tint = if (repeatSingle) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
