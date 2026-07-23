package com.issildur.animiya.uikit.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import com.issildur.animiya.uikit.modifier.posterScrim
import com.issildur.animiya.uikit.theme.AnimiyaRadii
import com.issildur.animiya.uikit.theme.PosterAspectRatio

/**
 * Постер тайтла. Инкапсулирует пропорцию 2:3, `Crop`, скругление и плейсхолдер —
 * чтобы нигде не повторять и не словить баг «мыльный thumbnail» из-за неверного
 * варианта картинки.
 */
@Composable
fun Poster(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    aspectRatio: Float = PosterAspectRatio,
    cornerRadius: Dp = AnimiyaRadii.md,
) {
    Box(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        if (!url.isNullOrBlank()) {
            AsyncImage(
                model = url,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/**
 * Широкий бэкдроп 16:9 (шапка карточки тайтла). Опциональный градиент-скрим —
 * для читаемости текста поверх изображения.
 */
@Composable
fun Backdrop(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    scrim: Boolean = true,
) {
    Box(
        modifier = modifier
            .aspectRatio(16f / 9f)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        if (!url.isNullOrBlank()) {
            AsyncImage(
                model = url,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        if (scrim) {
            Box(modifier = Modifier.fillMaxSize().posterScrim())
        }
    }
}
