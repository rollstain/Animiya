package com.issildur.animiya.composeapp

import androidx.compose.runtime.Composable

/** На iOS системной кнопки «назад» нет — возврат через шапку и свайп. */
@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) = Unit
