package com.issildur.animiya.uikit.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

/**
 * Корневая тема приложения.
 *
 * Осознанно НЕ наследуемся от MaterialExpressiveTheme: на нашей версии Compose
 * Multiplatform (1.9.0) material3 ещё не stable, а Expressive официально не
 * поддержан. Берём числа токенов в собственный слой поверх обычного [MaterialTheme].
 *
 * Расширенные цвета (appBackdrop/onSurfaceMuted/success) — через [AnimiyaTheme.extraColors].
 */
@Composable
fun AnimiyaTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalAnimiyaExtraColors provides AnimiyaDarkExtraColors,
    ) {
        MaterialTheme(
            colorScheme = AnimiyaDarkColorScheme,
            typography = animiyaTypography(
                display = AnimiyaFonts.display(),
                body = AnimiyaFonts.body(),
            ),
            shapes = AnimiyaShapes,
            content = content,
        )
    }
}

/**
 * Доступ к токенам темы в стиле MaterialTheme: `AnimiyaTheme.extraColors.success`.
 */
object AnimiyaTheme {
    val extraColors: AnimiyaExtraColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAnimiyaExtraColors.current
}
