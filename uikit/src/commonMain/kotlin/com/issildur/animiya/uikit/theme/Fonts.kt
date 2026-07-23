package com.issildur.animiya.uikit.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily

/**
 * Семейства шрифтов дизайн-системы.
 *
 * СЕЙЧАС: файлов шрифтов в проекте нет, поэтому оба семейства — [FontFamily.Default].
 *
 * ЧТОБЫ ПОДКЛЮЧИТЬ Unbounded/Onest (OFL 1.1, кириллица):
 * 1. Положить .ttf в `uikit/src/commonMain/composeResources/font/`:
 *      unbounded_semibold.ttf, unbounded_bold.ttf,
 *      onest_regular.ttf, onest_medium.ttf, onest_semibold.ttf
 * 2. Раскомментировать реализацию ниже (Res.font.* сгенерируется автоматически).
 * 3. Больше нигде править не нужно — [AnimiyaTheme] уже берёт семейства отсюда.
 */
object AnimiyaFonts {

    @Composable
    fun display(): FontFamily = FontFamily.Default
    // = FontFamily(
    //     Font(Res.font.unbounded_semibold, FontWeight.SemiBold),
    //     Font(Res.font.unbounded_bold, FontWeight.Bold),
    // )

    @Composable
    fun body(): FontFamily = FontFamily.Default
    // = FontFamily(
    //     Font(Res.font.onest_regular, FontWeight.Normal),
    //     Font(Res.font.onest_medium, FontWeight.Medium),
    //     Font(Res.font.onest_semibold, FontWeight.SemiBold),
    // )
}
