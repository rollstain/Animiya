package com.issildur.animiya.uikit.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Тонкая полоса прогресса просмотра/загрузки. Заполнение — фиолетовый акцент,
 * трек — приглушённый контейнер.
 */
@Composable
fun WatchProgressBar(
    fraction: Float,
    modifier: Modifier = Modifier,
    height: Dp = 3.dp,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    val clamped = fraction.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(clamped)
                .height(height)
                .clip(CircleShape)
                .background(color),
        )
    }
}

/**
 * Буквенный аватар студии озвучки («A», «К», «ЯП»…). Скруглённый квадрат.
 * [accent] выделяет предпочитаемую/выбранную студию фиолетовым.
 */
@Composable
fun StudioAvatar(
    text: String,
    modifier: Modifier = Modifier,
    accent: Boolean = false,
    size: Dp = 40.dp,
) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(10.dp))
            .background(if (accent) scheme.primary else scheme.surfaceContainerHigh),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (accent) scheme.onPrimary else scheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
