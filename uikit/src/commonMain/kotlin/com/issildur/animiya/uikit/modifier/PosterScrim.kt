package com.issildur.animiya.uikit.modifier

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush

/**
 * Градиент-скрим для читаемости текста поверх постера/бэкдропа.
 *
 * Линейный к фону: снизу плотный фон → прозрачный к верху (плотность в нижней
 * трети). Значения из токена gradient-scrim.
 */
fun Modifier.posterScrim(): Modifier = composed {
    val bg = MaterialTheme.colorScheme.background
    background(
        Brush.verticalGradient(
            0.28f to bg.copy(alpha = 0f),
            0.58f to bg.copy(alpha = 0.5f),
            1f to bg.copy(alpha = 0.96f),
        ),
    )
}

/** Тот же скрим как готовая кисть — когда нужен Brush, а не Modifier. */
@Composable
fun rememberPosterScrimBrush(): Brush {
    val bg = MaterialTheme.colorScheme.background
    return Brush.verticalGradient(
        0.28f to bg.copy(alpha = 0f),
        0.58f to bg.copy(alpha = 0.5f),
        1f to bg.copy(alpha = 0.96f),
    )
}
