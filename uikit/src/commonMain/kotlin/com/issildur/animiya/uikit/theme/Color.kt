package com.issildur.animiya.uikit.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Палитра из токенов дизайн-системы (exports/animiya-tokens.json).
 * Тёмная тема — основная. Elevation передаётся осветлением surface-контейнеров.
 */
internal object AnimiyaPalette {
    val AppBackdrop = Color(0xFF0F0D13)
    val Background = Color(0xFF141218)
    val SurfaceContainerLow = Color(0xFF1D1B20)
    val SurfaceContainer = Color(0xFF211F26)
    val SurfaceContainerHigh = Color(0xFF2B2930)
    val SurfaceContainerHighest = Color(0xFF36343B)
    val Outline = Color(0xFF48454E)
    val Primary = Color(0xFF7C5CFF)
    val PrimaryContainer = Color(0xFFD0BCFF)
    val OnSurface = Color(0xFFE6E0E9)
    val OnSurfaceVariant = Color(0xFFCAC4D0)
    val OnSurfaceMuted = Color(0xFF928E99)
    val Success = Color(0xFF4ADE80)
    val Warning = Color(0xFFF5A623) // янтарь для статуса «онгоинг N/M»; в токенах нет, добавлен под бейджи
    val Error = Color(0xFFF2564D)

    val White = Color(0xFFFFFFFF)
}

/**
 * ColorScheme тёмной темы. Роли surfaceContainer* заполнены по шкале осветления
 * из токенов — на них строятся карточки и листы, а не на тенях.
 */
internal val AnimiyaDarkColorScheme: ColorScheme = darkColorScheme(
    primary = AnimiyaPalette.Primary,
    onPrimary = AnimiyaPalette.White,
    primaryContainer = AnimiyaPalette.PrimaryContainer,
    onPrimaryContainer = AnimiyaPalette.SurfaceContainerLow,

    // Вторичные роли токенами не заданы — держим нейтральными, они не в фокусе дизайна.
    secondary = AnimiyaPalette.OnSurfaceVariant,
    onSecondary = AnimiyaPalette.Background,
    secondaryContainer = AnimiyaPalette.SurfaceContainerHigh,
    onSecondaryContainer = AnimiyaPalette.OnSurface,
    tertiary = AnimiyaPalette.PrimaryContainer,
    onTertiary = AnimiyaPalette.SurfaceContainerLow,
    tertiaryContainer = AnimiyaPalette.SurfaceContainerHigh,
    onTertiaryContainer = AnimiyaPalette.OnSurface,

    background = AnimiyaPalette.Background,
    onBackground = AnimiyaPalette.OnSurface,
    surface = AnimiyaPalette.Background,
    onSurface = AnimiyaPalette.OnSurface,
    surfaceVariant = AnimiyaPalette.SurfaceContainerHigh,
    onSurfaceVariant = AnimiyaPalette.OnSurfaceVariant,

    surfaceContainerLowest = AnimiyaPalette.AppBackdrop,
    surfaceContainerLow = AnimiyaPalette.SurfaceContainerLow,
    surfaceContainer = AnimiyaPalette.SurfaceContainer,
    surfaceContainerHigh = AnimiyaPalette.SurfaceContainerHigh,
    surfaceContainerHighest = AnimiyaPalette.SurfaceContainerHighest,

    outline = AnimiyaPalette.Outline,
    outlineVariant = AnimiyaPalette.SurfaceContainerHighest,

    error = AnimiyaPalette.Error,
    onError = AnimiyaPalette.White,
)

/**
 * Цвета, которых нет в стандартном M3 [ColorScheme], но которые заданы токенами.
 * Доступ через [AnimiyaTheme.extraColors].
 */
@Immutable
data class AnimiyaExtraColors(
    /** Фон глубже основного — под градиенты-скримы и «провалы» за постером. */
    val appBackdrop: Color,
    /** Приглушённый текст третьего уровня (метаданные, подписи). */
    val onSurfaceMuted: Color,
    /** Позитивные состояния: «доступно», «загружено», прогресс. */
    val success: Color,
)

internal val AnimiyaDarkExtraColors = AnimiyaExtraColors(
    appBackdrop = AnimiyaPalette.AppBackdrop,
    onSurfaceMuted = AnimiyaPalette.OnSurfaceMuted,
    success = AnimiyaPalette.Success,
)

internal val LocalAnimiyaExtraColors = staticCompositionLocalOf { AnimiyaDarkExtraColors }
