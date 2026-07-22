package com.issildur.animiya.feature.release

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.issildur.animiya.core.utils.AppError
import com.issildur.animiya.data.anime.api.usecase.GetReleaseUseCase
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
            ReleaseDetailsContent.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            is ReleaseDetailsContent.Error -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp),
                ) {
                    Text(text = state.error.toReadableMessage(), textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRetry) { Text(text = "Повторить") }
                }
            }

            is ReleaseDetailsContent.Content -> DetailsContent(state)
        }
    }
}

@Composable
private fun DetailsContent(content: ReleaseDetailsContent.Content) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Row {
                AsyncImage(
                    model = content.posterUrl,
                    contentDescription = content.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(120.dp)
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = content.title, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = content.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (content.genres.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = content.genres, style = MaterialTheme.typography.bodySmall)
                    }
                    if (content.blockedReason != null) {
                        Spacer(modifier = Modifier.height(8.dp))
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
                Text(text = content.description, style = MaterialTheme.typography.bodyMedium)
            }
        }

        item {
            Text(
                text = "Эпизоды (${content.episodes.size})",
                style = MaterialTheme.typography.titleSmall,
            )
        }

        items(items = content.episodes, key = { it.id }) { episode ->
            EpisodeRow(episode = episode)
        }
    }
}

@Composable
private fun EpisodeRow(episode: EpisodeUiModel) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(text = episode.title, style = MaterialTheme.typography.bodyMedium)
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
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}

internal fun AppError.toReadableMessage(): String = when (this) {
    is AppError.NoConnection -> "Нет соединения с интернетом"
    AppError.Timeout -> "Сервер не ответил вовремя"
    is AppError.ServerError -> "Сервер недоступен, попробуйте позже"
    AppError.AllEndpointsUnavailable -> "Все известные адреса недоступны"
    is AppError.RateLimited -> "Слишком много запросов, подождите немного"
    AppError.NotFound -> "Релиз не найден"
    is AppError.ClientError -> "Запрос отклонён сервером"
    is AppError.ParseError -> "Не удалось обработать ответ сервера"
    is AppError.Unknown -> "Что-то пошло не так"
}
