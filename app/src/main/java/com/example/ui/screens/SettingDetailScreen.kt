package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.catalog.RecitersCatalog
import com.example.data.model.AppThemeMode
import com.example.data.model.MushafDisplayMode
import com.example.data.model.Reciter
import com.example.data.model.SettingDetailType
import com.example.data.model.UserSettings
import com.example.ui.components.toArabicDigits
import com.example.ui.theme.AmiriQuranFontFamily
import com.example.ui.theme.LocalNoorColors
import com.example.viewmodel.OfflineDownloadUiState
import kotlin.math.roundToInt

@Composable
fun SettingDetailScreen(
    settingType: SettingDetailType,
    settings: UserSettings,
    cachedSurahCount: Int,
    downloadState: OfflineDownloadUiState,
    onSelectReciter: (String) -> Unit,
    onPlaySample: (Reciter) -> Unit,
    onChangeFontSize: (Float) -> Unit,
    onChangeDisplayMode: (MushafDisplayMode) -> Unit,
    onChangeThemeMode: (AppThemeMode) -> Unit,
    onToggleAutoScroll: (Boolean) -> Unit,
    onToggleRepeatAyah: (Boolean) -> Unit,
    onChangePlaybackSpeed: (Float) -> Unit,
    onChangeQuizTime: (Int) -> Unit,
    onToggleQuizAutoRead: (Boolean) -> Unit,
    onChangeTtsRate: (Float) -> Unit,
    onTestTtsVoice: () -> Unit,
    onDownloadFullQuran: () -> Unit,
    onClearOfflineCache: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
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
                        modifier = Modifier.testTag("setting_detail_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع"
                        )
                    }
                    Text(
                        text = settingType.arabicTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 720.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (settingType) {
                    SettingDetailType.RECITER -> {
                        Text(
                            text = "اختر الشيخ القارئ الافتراضي لتلاوة السور والرقية الشرعية:",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        RecitersCatalog.reciters.forEach { reciter ->
                            val isSelected = reciter.id == settings.selectedReciterId
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable { onSelectReciter(reciter.id) }
                                    .testTag("full_reciter_${reciter.id}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = reciter.arabicName,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${reciter.styleArabic} • ${reciter.countryArabic}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    OutlinedButton(onClick = { onPlaySample(reciter) }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = "استماع تجريبي",
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "تجربة")
                                    }
                                }
                            }
                        }
                    }

                    SettingDetailType.TYPOGRAPHY -> {
                        val noorColors = LocalNoorColors.current
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "معاينة حية للخط وتلوين الآية المقروءة بالأحمر:",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                val preview = buildAnnotatedString {
                                    append("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ﴿١﴾ ")
                                    withStyle(
                                        SpanStyle(
                                            color = noorColors.activeVerseRed,
                                            background = noorColors.activeVerseContainer,
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append("الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ ﴿٢﴾")
                                    }
                                    append(" الرَّحْمَٰنِ الرَّحِيمِ ﴿٣﴾")
                                }
                                Text(
                                    text = preview,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontFamily = AmiriQuranFontFamily,
                                        fontSize = settings.quranFontSizeSp.sp,
                                        lineHeight = (settings.quranFontSizeSp * 2.0f).sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "حجم الخط القرآني: ${settings.quranFontSizeSp.roundToInt().toArabicDigits()} نقطة",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Slider(
                                    value = settings.quranFontSizeSp,
                                    onValueChange = onChangeFontSize,
                                    valueRange = 18f..40f
                                )
                            }
                        }

                        Text(
                            text = "نمط عرض الآيات في القراءة",
                            style = MaterialTheme.typography.titleMedium
                        )
                        MushafDisplayMode.entries.forEach { mode ->
                            val selected = mode == settings.mushafDisplayMode
                            SelectionCard(
                                title = mode.arabicName,
                                subtitle = mode.description,
                                selected = selected,
                                onClick = { onChangeDisplayMode(mode) }
                            )
                        }
                    }

                    SettingDetailType.THEME -> {
                        Text(
                            text = "اختر المظهر الأنسب لقراءة القرآن الكريم ليلاً ونهاراً:",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        AppThemeMode.entries.forEach { mode ->
                            SelectionCard(
                                title = mode.arabicName,
                                subtitle = mode.description,
                                selected = mode == settings.themeMode,
                                onClick = { onChangeThemeMode(mode) }
                            )
                        }
                    }

                    SettingDetailType.RECITATION -> {
                        ToggleCard(
                            title = "التمرير التلقائي للآية المقروءة",
                            subtitle = "توجيه الشاشة تلقائياً لمتابعة النص المظلل باللون الأحمر أثناء القراءة",
                            checked = settings.autoScrollToRedVerse,
                            onCheckedChange = onToggleAutoScroll
                        )
                        ToggleCard(
                            title = "تكرار الآية الحالية",
                            subtitle = "إعادة الآية الواحدة باستمرار للمساعدة في الحفظ والمراجعة",
                            checked = settings.repeatSingleAyah,
                            onCheckedChange = onToggleRepeatAyah
                        )
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "سرعة التلاوة الافتراضية",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf(0.75f, 1.0f, 1.25f, 1.5f).forEach { speed ->
                                        val isCurrent = kotlin.math.abs(settings.playbackSpeed - speed) < 0.05f
                                        FilterChip(
                                            selected = isCurrent,
                                            onClick = { onChangePlaybackSpeed(speed) },
                                            label = { Text("${speed}x") },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    SettingDetailType.QUIZ -> {
                        ToggleCard(
                            title = "القراءة التلقائية للأسئلة",
                            subtitle = "تشغيل قارئ السؤال والخيارات بصوت عربي تلقائياً عند ظهور كل سؤال",
                            checked = settings.quizAutoReadQuestion,
                            onCheckedChange = onToggleQuizAutoRead
                        )

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "مهلة التفكير للمشاهدين في كل سؤال",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf(15 to "١٥ ث", 30 to "٣٠ ث", 60 to "٦٠ ث", 0 to "مفتوحة").forEach { (sec, label) ->
                                        FilterChip(
                                            selected = settings.quizTimeSeconds == sec,
                                            onClick = { onChangeQuizTime(sec) },
                                            label = { Text(label) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "سرعة صوت قارئ الأسئلة والأدعية",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Slider(
                                    value = settings.ttsSpeechRate,
                                    onValueChange = onChangeTtsRate,
                                    valueRange = 0.65f..1.35f
                                )
                                Button(
                                    onClick = onTestTtsVoice,
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "اختبار قارئ الأسئلة")
                                }
                            }
                        }
                    }

                    SettingDetailType.OFFLINE -> {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "حالة المصحف المحلي",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "تم حفظ ${cachedSurahCount.toArabicDigits()} سورة من أصل ١١٤ سورة للقراءة بدون اتصال بالإنترنت.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                LinearProgressIndicator(
                                    progress = { (cachedSurahCount / 114f).coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(50))
                                )
                            }
                        }

                        if (downloadState.isDownloading) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = downloadState.message ?: "جاري التحميل...",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = { downloadState.progress },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        } else if (downloadState.message != null) {
                            Text(
                                text = downloadState.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (downloadState.isError) LocalNoorColors.current.wrongRed else MaterialTheme.colorScheme.primary
                            )
                        }

                        Button(
                            onClick = onDownloadFullQuran,
                            enabled = !downloadState.isDownloading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.CloudDownload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تحميل المصحف كاملاً (١١٤ سورة)",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        OutlinedButton(
                            onClick = onClearOfflineCache,
                            enabled = !downloadState.isDownloading && cachedSurahCount > 0,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.DeleteOutline, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "مسح السور المحفوظة", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectionCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (selected) 1.6.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (selected) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ToggleCard(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onCheckedChange(!checked) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}
