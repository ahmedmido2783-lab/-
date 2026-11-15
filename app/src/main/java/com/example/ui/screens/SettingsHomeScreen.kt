package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.catalog.RecitersCatalog
import com.example.data.model.SettingDetailType
import com.example.data.model.UserSettings
import com.example.ui.components.toArabicDigits
import kotlin.math.roundToInt

@Composable
fun SettingsHomeScreen(
    settings: UserSettings,
    cachedSurahCount: Int,
    onOpenSettingDetail: (SettingDetailType) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentReciter = RecitersCatalog.getById(settings.selectedReciterId)

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 720.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(bottom = 4.dp)) {
                    Text(
                        text = "إعدادات التطبيق",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "اضغط على أي خيار لفتحه في شاشة إعدادات مستقلة وكاملة",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                SettingCategoryCard(
                    icon = Icons.Filled.Mic,
                    title = "اختيار القارئ الافتراضي",
                    subtitle = currentReciter.arabicName,
                    testTag = "setting_reciter",
                    onClick = { onOpenSettingDetail(SettingDetailType.RECITER) }
                )
            }

            item {
                SettingCategoryCard(
                    icon = Icons.Filled.FormatSize,
                    title = "حجم الخط ونمط المصحف",
                    subtitle = "حجم الخط: ${settings.quranFontSizeSp.roundToInt().toArabicDigits()} • ${settings.mushafDisplayMode.arabicName}",
                    testTag = "setting_typography",
                    onClick = { onOpenSettingDetail(SettingDetailType.TYPOGRAPHY) }
                )
            }

            item {
                SettingCategoryCard(
                    icon = Icons.Filled.Palette,
                    title = "مظهر التطبيق والوضع الليلي",
                    subtitle = settings.themeMode.arabicName,
                    testTag = "setting_theme",
                    onClick = { onOpenSettingDetail(SettingDetailType.THEME) }
                )
            }

            item {
                SettingCategoryCard(
                    icon = Icons.Filled.PlayCircle,
                    title = "إعدادات التلاوة والتشغيل",
                    subtitle = "السرعة ${settings.playbackSpeed}x • التمرير التلقائي: ${if (settings.autoScrollToRedVerse) "مفعّل" else "معطّل"}",
                    testTag = "setting_recitation",
                    onClick = { onOpenSettingDetail(SettingDetailType.RECITATION) }
                )
            }

            item {
                SettingCategoryCard(
                    icon = Icons.Filled.Quiz,
                    title = "إعدادات المسابقة وقارئ الأسئلة",
                    subtitle = "المؤقت: ${if (settings.quizTimeSeconds > 0) "${settings.quizTimeSeconds.toArabicDigits()} ثانية" else "مفتوح"} • القارئ التلقائي: ${if (settings.quizAutoReadQuestion) "مفعّل" else "معطّل"}",
                    testTag = "setting_quiz",
                    onClick = { onOpenSettingDetail(SettingDetailType.QUIZ) }
                )
            }

            item {
                SettingCategoryCard(
                    icon = Icons.Filled.CloudDownload,
                    title = "التحميل والقراءة بدون إنترنت",
                    subtitle = "السور المحفوظة محلياً: ${cachedSurahCount.toArabicDigits()} من ١١٤ سورة",
                    testTag = "setting_offline",
                    onClick = { onOpenSettingDetail(SettingDetailType.OFFLINE) }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun SettingCategoryCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
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
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "فتح",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
