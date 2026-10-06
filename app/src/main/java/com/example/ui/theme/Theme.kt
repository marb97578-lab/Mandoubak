package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private fun parseHexColorOrNull(hex: String?): Color? {
    if (hex.isNullOrBlank()) return null
    return try {
        val cleanHex = hex.trim().removePrefix("#")
        val colorInt = when (cleanHex.length) {
            6 -> "FF$cleanHex".toLong(16)
            8 -> cleanHex.toLong(16)
            else -> return null
        }
        Color(colorInt)
    } catch (_: Exception) {
        null
    }
}

val MandoubakLightColorScheme = lightColorScheme(
    primary = MandoubakBlue,
    onPrimary = Color.White,
    primaryContainer = CardClientsBg,
    onPrimaryContainer = MandoubakNavy,
    secondary = MandoubakCyan,
    onSecondary = Color.White,
    background = MandoubakBackground,
    surface = Color.White,
    onBackground = MandoubakTextPrimary,
    onSurface = MandoubakTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = MandoubakTextSecondary,
    outline = MandoubakCardBorder
)

@Composable
fun MandoubakTheme(
    themeConfig: ThemeConfig = ThemeConfig(),
    isRtl: Boolean = true,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeConfig.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemDark
    }

    val selectedTheme = ThemeRepository.getThemeById(themeConfig.activeThemeId)
    val customAccent = parseHexColorOrNull(themeConfig.customAccentHex)

    val activePrimary = customAccent ?: selectedTheme.primary
    val activeSecondary = selectedTheme.secondary
    val activeAccent = customAccent ?: selectedTheme.accent

    val activeTextColor = if (isDark) {
        Color(0xFFF8FAFC)
    } else {
        themeConfig.textColorOption.primaryColor
    }

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = activePrimary,
            onPrimary = Color.White,
            primaryContainer = Color(0xFF1E293B),
            onPrimaryContainer = Color(0xFFF1F5F9),
            secondary = activeSecondary,
            onSecondary = Color.White,
            background = selectedTheme.darkBackground,
            surface = selectedTheme.darkSurface,
            onBackground = Color(0xFFF8FAFC),
            onSurface = Color(0xFFF8FAFC),
            surfaceVariant = Color(0xFF334155),
            onSurfaceVariant = Color(0xFF94A3B8),
            outline = Color(0xFF475569)
        )
    } else {
        if (themeConfig.activeThemeId == 1 && customAccent == null && themeConfig.textColorOption == TextColorOption.DEFAULT_NAVY) {
            // Exactly the approved Mandoubak Light Color Scheme
            MandoubakLightColorScheme
        } else {
            lightColorScheme(
                primary = activePrimary,
                onPrimary = Color.White,
                primaryContainer = activePrimary.copy(alpha = 0.12f),
                onPrimaryContainer = activeAccent,
                secondary = activeSecondary,
                onSecondary = Color.White,
                background = MandoubakBackground,
                surface = Color.White,
                onBackground = activeTextColor,
                onSurface = activeTextColor,
                surfaceVariant = Color(0xFFF1F5F9),
                onSurfaceVariant = MandoubakTextSecondary,
                outline = MandoubakCardBorder
            )
        }
    }

    val layoutDirection = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(
        LocalThemeConfig provides themeConfig,
        LocalLayoutDirection provides layoutDirection
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
