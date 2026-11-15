package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.data.model.AppThemeMode

@Immutable
data class NoorExtendedColors(
    val activeVerseRed: Color,
    val activeVerseContainer: Color,
    val goldAccent: Color,
    val goldBright: Color,
    val goldContainer: Color,
    val onGoldContainer: Color,
    val correctGreen: Color,
    val correctContainer: Color,
    val wrongRed: Color,
    val wrongContainer: Color,
    val cardElevated: Color,
    val paperLight: Color,
    val paperEdge: Color,
    val spineShadow: Color,
    val headerGradientStart: Color,
    val headerGradientEnd: Color,
    val isDark: Boolean
)

val LocalNoorColors = staticCompositionLocalOf {
    NoorExtendedColors(
        activeVerseRed = ActiveVerseRedLight,
        activeVerseContainer = ActiveVerseRedContainerLight,
        goldAccent = GoldAccent,
        goldBright = GoldBright,
        goldContainer = GoldContainer,
        onGoldContainer = GoldOnContainer,
        correctGreen = CorrectEmerald,
        correctContainer = CorrectContainerLight,
        wrongRed = WrongCrimson,
        wrongContainer = WrongContainerLight,
        cardElevated = ParchmentSurface,
        paperLight = ParchmentPaperLight,
        paperEdge = ParchmentPaperEdge,
        spineShadow = Color(0x285B4620),
        headerGradientStart = EmeraldDeep,
        headerGradientEnd = EmeraldPrimary,
        isDark = false
    )
}

private val ParchmentColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldPrimaryContainer,
    onPrimaryContainer = EmeraldOnPrimaryContainer,
    secondary = GoldAccent,
    onSecondary = Color.White,
    secondaryContainer = GoldContainer,
    onSecondaryContainer = GoldOnContainer,
    tertiary = ActiveVerseRedLight,
    onTertiary = Color.White,
    background = ParchmentBackground,
    onBackground = ParchmentInk,
    surface = ParchmentSurface,
    onSurface = ParchmentInk,
    surfaceVariant = ParchmentSurfaceHigh,
    onSurfaceVariant = ParchmentInkSecondary,
    outline = Color(0xFFD2C4A8)
)

private val SanctuaryColorScheme = lightColorScheme(
    primary = SapphirePrimary,
    onPrimary = Color.White,
    primaryContainer = SapphireContainer,
    onPrimaryContainer = SapphireOnContainer,
    secondary = GoldAccent,
    onSecondary = Color.White,
    secondaryContainer = GoldContainer,
    onSecondaryContainer = GoldOnContainer,
    tertiary = ActiveVerseRedLight,
    onTertiary = Color.White,
    background = SanctuaryBackground,
    onBackground = ParchmentInk,
    surface = SanctuarySurface,
    onSurface = ParchmentInk,
    surfaceVariant = SanctuarySurfaceHigh,
    onSurfaceVariant = ParchmentInkSecondary,
    outline = Color(0xFFBBD3D0)
)

private val NightColorScheme = darkColorScheme(
    primary = NightEmeraldPrimary,
    onPrimary = Color(0xFF04291E),
    primaryContainer = NightEmeraldContainer,
    onPrimaryContainer = Color(0xFFA7F0D3),
    secondary = NightGold,
    onSecondary = Color(0xFF2B2006),
    secondaryContainer = NightGoldContainer,
    onSecondaryContainer = Color(0xFFFBE2A2),
    tertiary = ActiveVerseRedDark,
    onTertiary = Color(0xFF3E0507),
    background = NightBackground,
    onBackground = NightInk,
    surface = NightSurface,
    onSurface = NightInk,
    surfaceVariant = NightSurfaceHigh,
    onSurfaceVariant = NightInkSecondary,
    outline = Color(0xFF2B4339)
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

@Composable
fun NoorAlQuranTheme(
    themeMode: AppThemeMode = AppThemeMode.CLASSIC_PARCHMENT,
    content: @Composable () -> Unit
) {
    val isDark = themeMode == AppThemeMode.NIGHT_MUSHAF
    val colorScheme = when (themeMode) {
        AppThemeMode.CLASSIC_PARCHMENT -> ParchmentColorScheme
        AppThemeMode.EMERALD_SANCTUARY -> SanctuaryColorScheme
        AppThemeMode.NIGHT_MUSHAF -> NightColorScheme
    }

    val extendedColors = when (themeMode) {
        AppThemeMode.NIGHT_MUSHAF -> NoorExtendedColors(
            activeVerseRed = ActiveVerseRedDark,
            activeVerseContainer = ActiveVerseRedContainerDark,
            goldAccent = NightGold,
            goldBright = Color(0xFFF2D078),
            goldContainer = NightGoldContainer,
            onGoldContainer = Color(0xFFFBE2A2),
            correctGreen = Color(0xFF4ADE80),
            correctContainer = CorrectContainerDark,
            wrongRed = ActiveVerseRedDark,
            wrongContainer = WrongContainerDark,
            cardElevated = NightSurfaceHigh,
            paperLight = NightPaperDark,
            paperEdge = NightPaperEdge,
            spineShadow = Color(0x66000000),
            headerGradientStart = Color(0xFF08221B),
            headerGradientEnd = Color(0xFF0F3B2E),
            isDark = true
        )

        AppThemeMode.EMERALD_SANCTUARY -> NoorExtendedColors(
            activeVerseRed = ActiveVerseRedLight,
            activeVerseContainer = ActiveVerseRedContainerLight,
            goldAccent = GoldAccent,
            goldBright = GoldBright,
            goldContainer = GoldContainer,
            onGoldContainer = GoldOnContainer,
            correctGreen = CorrectEmerald,
            correctContainer = CorrectContainerLight,
            wrongRed = WrongCrimson,
            wrongContainer = WrongContainerLight,
            cardElevated = SanctuarySurface,
            paperLight = Color(0xFFFBFEFD),
            paperEdge = Color(0xFFE3EFEC),
            spineShadow = Color(0x260A4D5C),
            headerGradientStart = Color(0xFF073A47),
            headerGradientEnd = SapphirePrimary,
            isDark = false
        )

        AppThemeMode.CLASSIC_PARCHMENT -> NoorExtendedColors(
            activeVerseRed = ActiveVerseRedLight,
            activeVerseContainer = ActiveVerseRedContainerLight,
            goldAccent = GoldAccent,
            goldBright = GoldBright,
            goldContainer = GoldContainer,
            onGoldContainer = GoldOnContainer,
            correctGreen = CorrectEmerald,
            correctContainer = CorrectContainerLight,
            wrongRed = WrongCrimson,
            wrongContainer = WrongContainerLight,
            cardElevated = ParchmentSurface,
            paperLight = ParchmentPaperLight,
            paperEdge = ParchmentPaperEdge,
            spineShadow = Color(0x2C5C451C),
            headerGradientStart = EmeraldDeep,
            headerGradientEnd = EmeraldPrimary,
            isDark = false
        )
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl,
        LocalNoorColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}
