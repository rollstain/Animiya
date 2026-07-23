package com.issildur.animiya.feature.release

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
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
import com.issildur.animiya.uikit.component.LoadingState
import com.issildur.animiya.uikit.component.Poster
import com.issildur.animiya.uikit.theme.AnimiyaSpacing
import org.koin.compose.koinInject

@Composable
fun ReleaseDetailsScreen(
    idOrAlias: String,
    modifier: Modifier = Modifier,
) {
    val getRelease: GetReleaseUseCase = koinInject()
    val scope = rememberCoroutineScope()
    val component = remember(scope, idOrAlias) {
        DefaultReleaseDetailsComponent(
            scope = scope,
            idOrAlias = idOrAlias,
            getRelease = getRelease,
        )
    }
    ReleaseDetailsScreen(component = component, modifier = modifier)
}

@Composable
fun ReleaseDetailsScreen(
    component: ReleaseDetailsComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.collectAsState()
    ReleaseDetailsView(state = state, onRetry = component::onRetry, modifier = modifier)
}

@Composable
fun ReleaseDetailsView(
    state: ReleaseDetailsContent,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        when (state) {
            ReleaseDetailsContent.Loading -> LoadingState()

            is ReleaseDetailsContent.Error -> AppErrorContent(error = state.error, onRetry = onRetry)

            is ReleaseDetailsContent.Content -> DetailsContent(state)
        }
    }
}

@Composable
private fun DetailsContent(content: ReleaseDetailsContent.Content) {
    LazyColumn(
        contentPadding = PaddingValues(AnimiyaSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AnimiyaSpacing.sm),
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Row {
                Poster(
                    url = content.posterUrl,
                    contentDescription = content.title,
                    modifier = Modifier.width(120.dp),
                )
                Spacer(modifier = Modifier.width(AnimiyaSpacing.sm))
                Column {
                    Text(
                        text = content.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(AnimiyaSpacing.xxs))
                    Text(
                        text = content.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (content.genres.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(AnimiyaSpacing.xxs))
                        Text(
                            text = content.genres,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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

        if (content.description != null) {
            item {
                Text(
                    text = content.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        item {
            Text(
                text = "Эпизоды (${content.episodes.size})",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        items(items = content.episodes, key = { it.id }) { episode ->
            EpisodeRow(episode = episode)
        }
    }
}

@Composable
private fun EpisodeRow(episode: EpisodeUiModel) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = AnimiyaSpacing.xxs)) {
        Text(
            text = episode.title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        val details = listOfNotNull(
            episode.duration,
            episode.qualities.takeIf { it.isNotEmpty() },
            episode.skipHint,
        ).joinToString(" · ")
        if (details.isNotEmpty()) {
            Text(
                text = details,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (!episode.hasVideo) {
            Text(
                text = "Видео недоступно",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
        HorizontalDivider(modifier = Modifier.padding(top = AnimiyaSpacing.xs))
    }
}
