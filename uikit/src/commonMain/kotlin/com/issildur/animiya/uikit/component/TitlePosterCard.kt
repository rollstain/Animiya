package com.issildur.animiya.uikit.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.issildur.animiya.uikit.theme.AnimiyaRadii
import com.issildur.animiya.uikit.theme.AnimiyaSpacing

/**
 * Постер-карточка с названием и подписью. Общая для сетки каталога и
 * горизонтальных полок главной — чтобы карточка тайтла выглядела одинаково везде.
 *
 * [overlay] — слот поверх постера под бейджи (онгоинг / блокировка / прогресс).
 */
@Composable
fun TitlePosterCard(
    posterUrl: String?,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    overlay: (@Composable BoxScope.() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(AnimiyaRadii.md))
            .clickable(onClick = onClick),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Poster(url = posterUrl, contentDescription = title)
            overlay?.invoke(this)
        }
        Spacer(modifier = Modifier.height(AnimiyaSpacing.xs))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (subtitle.isNotEmpty()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
