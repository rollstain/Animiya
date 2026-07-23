package com.issildur.animiya.uikit.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.issildur.animiya.uikit.theme.AnimiyaRadii
import com.issildur.animiya.uikit.theme.AnimiyaSpacing

/**
 * Строка эпизода (макет 02-title): превью 16:9 + номер.название + опциональный
 * бейдж + мета (длительность/остаток) + опциональный трейлинг (загрузка и т.п.).
 */
@Composable
fun EpisodeRow(
    title: String,
    meta: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    thumbnailUrl: String? = null,
    badgeText: String? = null,
    badgeTone: BadgeTone = BadgeTone.Accent,
    unavailable: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AnimiyaRadii.md))
            .clickable(enabled = !unavailable, onClick = onClick)
            .padding(vertical = AnimiyaSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(96.dp)
                .height(54.dp)
                .clip(RoundedCornerShape(AnimiyaRadii.sm))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            if (!thumbnailUrl.isNullOrBlank()) {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                )
            }
        }
        Spacer(modifier = Modifier.width(AnimiyaSpacing.sm))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (badgeText != null) {
                    Spacer(modifier = Modifier.width(AnimiyaSpacing.xs))
                    Badge(text = badgeText, tone = badgeTone)
                }
            }
            if (meta.isNotEmpty()) {
                Text(
                    text = meta,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (unavailable) {
                Text(
                    text = "В этой озвучке недоступно",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (trailing != null) {
            Row(
                modifier = Modifier.padding(start = AnimiyaSpacing.xs),
                horizontalArrangement = Arrangement.End,
            ) { trailing() }
        }
    }
}
