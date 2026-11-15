package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.catalog.RecitersCatalog
import com.example.data.model.RuqyahAudioSource
import com.example.data.model.RuqyahSegment
import com.example.ui.components.ReciterPickerSheet
import com.example.ui.components.toArabicDigits
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.AmiriQuranFontFamily
import com.example.ui.theme.LocalNoorColors
import com.example.viewmodel.RuqyahPlayerViewModel

@Composable
fun RuqyahPlayerScreen(
    viewModel: RuqyahPlayerViewModel,
    onBack: () -> Unit
) {
    val program = viewModel.program
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val activeIndex by viewModel.activeSegmentIndex.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val isBuffering by viewModel.isBuffering.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    val noorColors = LocalNoorColors.current
    val currentReciter = remember(settings.selectedReciterId) {
        RecitersCatalog.getById(settings.selectedReciterId)
    }
    val isQuranBased = remember(program) {
        program.segments.all { it.audioSource is RuqyahAudioSource.QuranVerse }
    }
    var showReciterSheet by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    LaunchedEffect(activeIndex, settings.autoScrollToRedVerse) {
        if (activeIndex >= 0 && settings.autoScrollToRedVerse) {
            listState.animateScrollToItem(activeIndex, scrollOffset = -60)
        }
    }

    if (showReciterSheet) {
        ReciterPickerSheet(
            selectedReciterId = settings.selectedReciterId,
            onSelectReciter = { viewModel.switchReciterAndResume(it.id) },
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("ruqyah_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع"
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = program.titleArabic,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = AmiriFontFamily,
                                fontSize = 21.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (isQuranBased) "بصوت ${currentReciter.arabicName}" else "قراءة كاملة بالصوت العربي",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (isQuranBased) {
                        IconButton(onClick = { showReciterSheet = true }) {
                            Icon(
                                imageVector = Icons.Filled.Mic,
                                contentDescription = "تغيير القارئ",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 10.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
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
                                text = errorMessage ?: "",
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
                        Text(
                            text = if (activeIndex >= 0) {
                                "المقطع ${(activeIndex + 1).toArabicDigits()} من ${program.segments.size.toArabicDigits()}"
                            } else {
                                "اضغط تشغيل لقراءة الرقية كاملة"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = if (activeIndex >= 0) noorColors.activeVerseRed else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (isQuranBased) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .clickable { showReciterSheet = true }
                            ) {
                                Text(
                                    text = currentReciter.arabicName.removePrefix("الشيخ "),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(onClick = { viewModel.previousSegment() }) {
                            Icon(imageVector = Icons.Filled.SkipPrevious, contentDescription = "السابق")
                        }

                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .clickable { viewModel.togglePlayPause() }
                                .testTag("ruqyah_play_pause_button")
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
                                        contentDescription = if (isPlaying) "إيقاف مؤقت" else "تشغيل الرقية كاملة",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }
                        }

                        IconButton(onClick = { viewModel.nextSegment() }) {
                            Icon(imageVector = Icons.Filled.SkipNext, contentDescription = "التالي")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 760.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(program.segments, key = { _, seg -> seg.id }) { index, segment ->
                    RuqyahSegmentCard(
                        index = index,
                        segment = segment,
                        isActive = index == activeIndex,
                        fontSizeSp = settings.quranFontSizeSp,
                        onClick = { viewModel.playFromSegment(index) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RuqyahSegmentCard(
    index: Int,
    segment: RuqyahSegment,
    isActive: Boolean,
    fontSizeSp: Float,
    onClick: () -> Unit
) {
    val noorColors = LocalNoorColors.current
    val bg by animateColorAsState(
        targetValue = if (isActive) noorColors.activeVerseContainer else MaterialTheme.colorScheme.surface,
        label = "ruqyah_seg_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isActive) noorColors.activeVerseRed else MaterialTheme.colorScheme.onSurface,
        label = "ruqyah_seg_txt"
    )
    val isQuran = segment.audioSource is RuqyahAudioSource.QuranVerse

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bg,
        border = BorderStroke(
            width = if (isActive) 1.6.dp else 1.dp,
            color = if (isActive) noorColors.activeVerseRed else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        ),
        tonalElevation = if (isActive) 4.dp else 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag("ruqyah_segment_${index + 1}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isActive) noorColors.activeVerseRed else MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${(index + 1).toArabicDigits()}. ${segment.titleArabic}",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isActive) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = segment.referenceArabic,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isActive) noorColors.activeVerseRed else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = segment.textArabic,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = if (isQuran) AmiriQuranFontFamily else AmiriFontFamily,
                    fontSize = fontSizeSp.sp,
                    lineHeight = (fontSizeSp * 1.95f).sp
                ),
                color = textColor,
                modifier = Modifier.fillMaxWidth()
            )
            if (segment.repeatHint.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• ${segment.repeatHint}",
                    style = MaterialTheme.typography.labelMedium,
                    color = noorColors.goldAccent
                )
            }
        }
    }
}
