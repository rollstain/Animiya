package com.issildur.animiya.uikit.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.issildur.animiya.uikit.theme.AnimiyaRadii
import com.issildur.animiya.uikit.theme.AnimiyaSpacing

/**
 * Карточка выбранной озвучки на экране тайтла (макет 02-title).
 * Аватар студии + название + мета («Дубляж · 12 серий · до 1080p») + «Сменить».
 * Тап по карточке или по «Сменить» открывает sheet выбора озвучки.
 */
@Composable
fun DubSelectorCard(
    studio: String,
    meta: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    changeLabel: String = "Сменить",
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AnimiyaRadii.md))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick)
            .padding(AnimiyaSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StudioAvatar(text = studio.take(1).uppercase(), accent = true)
        Spacer(modifier = Modifier.width(AnimiyaSpacing.sm))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = studio,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = meta,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = changeLabel,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = AnimiyaSpacing.sm),
        )
    }
}
