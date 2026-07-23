package com.issildur.animiya.uikit.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import com.issildur.animiya.uikit.theme.AnimiyaPalette

/**
 * Тон бейджа. Значения подобраны под макеты sheet озвучки и карточки тайтла:
 * онгоинг-статус, «ПРЕДПОЧ.», «онгоинг N/M», «18+ БЕЗ ЦЕНЗ.» и нейтральные метки.
 */
enum class BadgeTone { Neutral, Accent, Success, Warning, Danger }

/**
 * Компактная метка-пилюля. Фон — приглушённый тон, текст — насыщенный того же тона.
 * [leadingDot] — точка перед текстом (как «● Онгоинг» на карточке тайтла).
 */
@Composable
fun Badge(
    text: String,
    modifier: Modifier = Modifier,
    tone: BadgeTone = BadgeTone.Neutral,
    leadingDot: Boolean = false,
) {
    val (bg, fg) = badgeColors(tone)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingDot) {
            Box(
                modifier = Modifier
                    .padding(end = 5.dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(fg),
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
        )
    }
}

@Composable
private fun badgeColors(tone: BadgeTone): Pair<Color, Color> = when (tone) {
    BadgeTone.Neutral -> MaterialTheme.colorScheme.surfaceContainerHigh to
        MaterialTheme.colorScheme.onSurfaceVariant
    BadgeTone.Accent -> MaterialTheme.colorScheme.primary.copy(alpha = 0.20f) to
        MaterialTheme.colorScheme.primaryContainer
    BadgeTone.Success -> AnimiyaPalette.Success.copy(alpha = 0.18f) to AnimiyaPalette.Success
    BadgeTone.Warning -> AnimiyaPalette.Warning.copy(alpha = 0.18f) to AnimiyaPalette.Warning
    BadgeTone.Danger -> MaterialTheme.colorScheme.error.copy(alpha = 0.18f) to
        MaterialTheme.colorScheme.error
}
