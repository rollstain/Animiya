package com.issildur.animiya.composeapp

import androidx.compose.runtime.Composable

/**
 * Перехват системной кнопки «назад».
 *
 * В Compose Multiplatform 1.9.0 общего BackHandler нет, поэтому expect/actual:
 * на Android это системная кнопка, на iOS аппаратной кнопки не существует —
 * там возврат идёт через кнопку в шапке и свайп.
 */
@Composable
expect fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit)
