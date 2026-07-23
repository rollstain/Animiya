package com.issildur.animiya.feature.release

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.issildur.animiya.core.ui.AppErrorContent
import com.issildur.animiya.data.anime.api.usecase.GetReleaseUseCase
import com.issildur.animiya.uikit.component.Backdrop
import com.issildur.animiya.uikit.component.Badge
import com.issildur.animiya.uikit.component.BadgeTone
import com.issildur.animiya.uikit.component.DubSelectorCard
import com.issildur.animiya.uikit.component.EpisodeRow
import com.issildur.animiya.uikit.component.LoadingState
import com.issildur.animiya.uikit.component.PrimaryButton
import com.issildur.animiya.uikit.component.SectionHeader
import com.issildur.animiya.uikit.theme.AnimiyaSpacing
import org.koin.compose.koinInject

@Composable
fun ReleaseDetailsScreen(
    idOrAlias: String,
    onPlay: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val getRelease: GetReleaseUseCase = koinInject()
    val scope = rememberCoroutineScope()
    val component = remember(scope, idOrAlias) {
        DefaultReleaseDetailsComponent(scope = scope, idOrAlias = idOrAlias, getRelease = getRelease)
    }
    ReleaseDetailsScreen(component = component, onPlay = { onPlay(idOrAlias) }, modifier = modifier)
}

@Composable
fun ReleaseDetailsScreen(
    component: ReleaseDetailsComponent,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by component.state.collectAsState()
    ReleaseDetailsView(state = state, onRetry = component::onRetry, onPlay = onPlay, modifier = modifier)
}

@Composable
fun ReleaseDetailsView(
    state: ReleaseDetailsContent,
    onRetry: () -> Unit,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        when (state) {
            ReleaseDetailsContent.Loading -> LoadingState()
            is ReleaseDetailsContent.Error -> AppErrorContent(error = state.error, onRetry = onRetry)
            is ReleaseDetailsContent.Content -> DetailsContent(content = state, onPlay = onPlay)
        }
    }
}

@Composable
private fun DetailsContent(content: ReleaseDetailsContent.Content, onPlay: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(bottom = AnimiyaSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AnimiyaSpacing.sm),
        ) {
            item { Header(content) }
            item { PaddedDubCard(content) }
            content.description?.let { desc ->
                item {
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = AnimiyaSpacing.md),
                    )
                }
            }
            item {
                SectionHeader(
                    title = "Эпизоды",
                    count = "${content.episodes.size}",
                    modifier = Modifier.padding(horizontal = AnimiyaSpacing.md),
                )
            }
            items(items = content.episodes, key = { it.id }) { episode ->
                EpisodeRow(
                    title = episode.title,
                    meta = episode.meta,
                    onClick = onPlay,
                    thumbnailUrl = episode.thumbnailUrl,
                    unavailable = !episode.hasVideo,
                    modifier = Modifier.padding(horizontal = AnimiyaSpacing.md),
                )
            }
        }

        // Sticky-CTA снизу.
        if (content.hasAnyVideo) {
            Surface(tonalElevation = 0.dp, color = MaterialTheme.colorScheme.background) {
                PrimaryButton(
                    text = "▶  Смотреть",
                    onClick = onPlay,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AnimiyaSpacing.md),
                )
            }
        }
    }
}

@Composable
private fun Header(content: ReleaseDetailsContent.Content) {
    Column {
        Box(modifier = Modifier.fillMaxWidth()) {
            Backdrop(url = content.backdropUrl, contentDescription = content.title, scrim = true)
        }
        Column(modifier = Modifier.padding(horizontal = AnimiyaSpacing.md, vertical = AnimiyaSpacing.xs)) {
            if (content.isOngoing) {
                Badge(text = "Онгоинг", tone = BadgeTone.Success, leadingDot = true)
                Spacer(modifier = Modifier.height(AnimiyaSpacing.xs))
            }
            Text(
                text = content.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            content.originalTitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(AnimiyaSpacing.xxs))
            Text(
                text = content.meta,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            content.inListsLabel?.let {
                Text(
                    text = "▲ $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            if (content.blockedReason != null) {
                Spacer(modifier = Modifier.height(AnimiyaSpacing.xs))
                Text(
                    text = "🔒 ${content.blockedReason}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun PaddedDubCard(content: ReleaseDetailsContent.Content) {
    Column(modifier = Modifier.padding(horizontal = AnimiyaSpacing.md)) {
        Text(
            text = "ОЗВУЧКА",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(AnimiyaSpacing.xs))
        DubSelectorCard(
            studio = content.dubStudio,
            meta = content.dubMeta,
            // Sheet выбора озвучки появится с моделью мультиозвучки; пока одна студия.
            onClick = {},
        )
    }
}
