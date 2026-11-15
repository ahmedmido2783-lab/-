package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.catalog.RecitersCatalog
import com.example.data.model.Reciter
import com.example.ui.theme.LocalNoorColors

fun Int.toArabicDigits(): String {
    val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return toString().map { ch ->
        if (ch in '0'..'9') arabicDigits[ch - '0'] else ch
    }.joinToString("")
}

@Composable
fun SurahNumberMedallion(
    number: Int,
    modifier: Modifier = Modifier,
    size: Dp = 46.dp
) {
    val primary = MaterialTheme.colorScheme.primary
    val gold = LocalNoorColors.current.goldAccent
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val side = this.size.minDimension * 0.68f
            val topLeft = Offset((this.size.width - side) / 2f, (this.size.height - side) / 2f)
            drawRect(
                color = gold.copy(alpha = 0.35f),
                topLeft = topLeft,
                size = Size(side, side),
                style = Stroke(width = 2.6f)
            )
            rotate(45f) {
                drawRect(
                    color = primary.copy(alpha = 0.78f),
                    topLeft = topLeft,
                    size = Size(side, side),
                    style = Stroke(width = 2.4f)
                )
            }
        }
        Text(
            text = number.toArabicDigits(),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            ),
            color = primary
        )
    }
}

@Composable
fun MushafBookPageFrame(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val noorColors = LocalNoorColors.current
    val primary = MaterialTheme.colorScheme.primary
    val gold = noorColors.goldAccent

    Surface(
        shape = RoundedCornerShape(
            topStart = 18.dp,
            bottomStart = 18.dp,
            topEnd = 8.dp,
            bottomEnd = 8.dp
        ),
        shadowElevation = 8.dp,
        tonalElevation = 2.dp,
        border = BorderStroke(1.2.dp, gold.copy(alpha = 0.55f)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        0.0f to noorColors.paperEdge,
                        0.04f to noorColors.paperLight,
                        0.93f to noorColors.paperLight,
                        1.0f to noorColors.paperEdge
                    )
                )
        ) {
            // Subtle book spine shadow on the binding side (Right edge in RTL)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(18.dp)
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(noorColors.spineShadow, Color.Transparent)
                        )
                    )
            )

            // Double gilded manuscript frame & corner rosettes
            Canvas(modifier = Modifier.fillMaxSize()) {
                val outerInset = 10.dp.toPx()
                val innerInset = 15.dp.toPx()

                drawRoundRect(
                    color = gold.copy(alpha = 0.55f),
                    topLeft = Offset(outerInset, outerInset),
                    size = Size(size.width - outerInset * 2f, size.height - outerInset * 2f),
                    cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                    style = Stroke(width = 1.6.dp.toPx())
                )
                drawRoundRect(
                    color = primary.copy(alpha = 0.35f),
                    topLeft = Offset(innerInset, innerInset),
                    size = Size(size.width - innerInset * 2f, size.height - innerInset * 2f),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                    style = Stroke(width = 0.9.dp.toPx())
                )

                // Corner rosettes
                val cornerRadius = 5.5.dp.toPx()
                val corners = listOf(
                    Offset(innerInset, innerInset),
                    Offset(size.width - innerInset, innerInset),
                    Offset(innerInset, size.height - innerInset),
                    Offset(size.width - innerInset, size.height - innerInset)
                )
                corners.forEach { pt ->
                    drawCircle(
                        color = gold.copy(alpha = 0.75f),
                        radius = cornerRadius,
                        center = pt
                    )
                    drawCircle(
                        color = primary.copy(alpha = 0.85f),
                        radius = cornerRadius * 0.45f,
                        center = pt
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp, vertical = 20.dp),
                content = content
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReciterPickerSheet(
    selectedReciterId: String,
    onSelectReciter: (Reciter) -> Unit,
    onDismiss: () -> Unit
) {
    val noorColors = LocalNoorColors.current
    var query by remember { mutableStateOf("") }

    val filteredReciters = remember(query) {
        val q = query.trim()
        if (q.isEmpty()) RecitersCatalog.reciters
        else RecitersCatalog.reciters.filter {
            it.arabicName.contains(q) || it.styleArabic.contains(q) || it.countryArabic.contains(q)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "اختر القارئ (${RecitersCatalog.reciters.size.toArabicDigits()} شيخاً)",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "نخبة من كبار قراء العالم الإسلامي ومشاهير الأئمة",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("ابحث باسم الشيخ...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Filled.Close, contentDescription = "مسح")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                items(filteredReciters, key = { it.id }) { reciter ->
                    val isSelected = reciter.id == selectedReciterId
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else noorColors.cardElevated,
                        border = BorderStroke(
                            width = if (isSelected) 1.4.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else noorColors.goldAccent.copy(alpha = 0.25f)
                        ),
                        tonalElevation = if (isSelected) 4.dp else 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                onSelectReciter(reciter)
                                onDismiss()
                            }
                            .testTag("sheet_reciter_${reciter.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = reciter.arabicName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${reciter.styleArabic} • ${reciter.countryArabic}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                contentDescription = if (isSelected) "مختار" else "غير مختار",
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
