package com.issildur.animiya.uikit.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.issildur.animiya.uikit.theme.AnimiyaRadii

/**
 * Чип-фильтр. Два состояния из макетов: выбран (фиолетовая заливка + рамка,
 * опциональный «✕»), обычный (тёмная заливка). Используется в sheet фильтров
 * и в sheet выбора озвучки.
 */
@Composable
fun AnimiyaFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showRemoveWhenSelected: Boolean = false,
) {
    val scheme = MaterialTheme.colorScheme
    val bg = if (selected) scheme.primary.copy(alpha = 0.20f) else scheme.surfaceContainer
    val fg = if (selected) scheme.primaryContainer else scheme.onSurfaceVariant

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(AnimiyaRadii.sm))
            .then(
                if (selected) {
                    Modifier.border(
                        BorderStroke(1.dp, scheme.primary.copy(alpha = 0.6f)),
                        RoundedCornerShape(AnimiyaRadii.sm),
                    )
                } else {
                    Modifier
                },
            )
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = fg)
        if (selected && showRemoveWhenSelected) {
            Text(
                text = "✕",
                style = MaterialTheme.typography.labelSmall,
                color = fg,
                modifier = Modifier.padding(start = 6.dp),
            )
        }
    }
}

/**
 * Метка-чип с числом (жанр + количество): «Фэнтези · 1.2k».
 */
@Composable
fun CountChip(
    label: String,
    count: String,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(AnimiyaRadii.sm))
            .background(scheme.surfaceContainer)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = scheme.onSurface)
        Text(
            text = " · $count",
            style = MaterialTheme.typography.labelLarge,
            color = scheme.onSurfaceVariant,
        )
    }
}
