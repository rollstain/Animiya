package com.issildur.animiya.feature.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.issildur.animiya.core.utils.AppError
import kotlinx.coroutines.flow.distinctUntilChanged

private const val PREFETCH_DISTANCE = 6

/**
 * Чистая функция от состояния — не знает про [CatalogComponent].
 *
 * Именно поэтому её можно переиспользовать в превью, в тестах и написать
 * рядом TV-вариант, потребляющий тот же [CatalogUiState].
 */
@Composable
fun CatalogView(
    state: CatalogUiState,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onReleaseClick: (ReleaseUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        when (val content = state.content) {
            CatalogContent.Loading -> CenteredProgress()

            CatalogContent.Empty -> CenteredMessage(text = "Ничего не найдено")

            is CatalogContent.Error -> ErrorState(
                message = content.error.toReadableMessage(),
                onRetry = onRetry,
            )

            is CatalogContent.Items -> ReleaseGrid(
                releases = content.releases,
                isAppending = state.isAppending,
                appendError = state.appendError,
                onLoadMore = onLoadMore,
                onRetryAppend = onLoadMore,
                onReleaseClick = onReleaseClick,
            )
        }
    }
}

@Composable
private fun ReleaseGrid(
    releases: List<ReleaseUiModel>,
    isAppending: Boolean,
    appendError: AppError?,
    onLoadMore: () -> Unit,
    onRetryAppend: () -> Unit,
    onReleaseClick: (ReleaseUiModel) -> Unit,
) {
    val gridState = rememberLazyGridState()
    PaginationEffect(gridState = gridState, itemCount = releases.size, onLoadMore = onLoadMore)

    Column(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            state = gridState,
            // Adaptive, а не фиксированное число колонок: бесплатно даёт корректный
            // вид на планшете и позже на Android TV.
            columns = GridCells.Adaptive(minSize = 120.dp),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f).fillMaxWidth(),
        ) {
            items(items = releases, key = { it.id.raw }) { release ->
                ReleaseCard(release = release, onClick = { onReleaseClick(release) })
            }
        }

        if (isAppending) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
        if (appendError != null) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Button(onClick = onRetryAppend) {
                    Text(text = "Не удалось загрузить ещё. Повторить")
                }
            }
        }
    }
}

@Composable
private fun PaginationEffect(
    gridState: LazyGridState,
    itemCount: Int,
    onLoadMore: () -> Unit,
) {
    LaunchedEffect(gridState, itemCount) {
        snapshotFlow {
            val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            itemCount > 0 && lastVisible >= itemCount - PREFETCH_DISTANCE
        }
            .distinctUntilChanged()
            .collect { reached -> if (reached) onLoadMore() }
    }
}

@Composable
private fun ReleaseCard(release: ReleaseUiModel, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            // Скругление на clickable, чтобы ripple не выходил за карточку.
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(POSTER_ASPECT_RATIO)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            AsyncImage(
                model = release.posterUrl,
                contentDescription = release.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            if (release.blockedReason != null) {
                Badge(text = "🔒", modifier = Modifier.align(Alignment.TopEnd))
            } else if (release.isOngoing) {
                Badge(text = "Онгоинг", modifier = Modifier.align(Alignment.TopStart))
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = release.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (release.subtitle.isNotEmpty()) {
            Text(
                text = release.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun Badge(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.padding(6.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(6.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}

@Composable
private fun CenteredProgress() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun CenteredMessage(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp),
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) { Text(text = "Повторить") }
        }
    }
}

private const val POSTER_ASPECT_RATIO = 2f / 3f

/** Ошибка -> текст для пользователя. Технические детали наружу не выносим. */
internal fun AppError.toReadableMessage(): String = when (this) {
    is AppError.NoConnection -> "Нет соединения с интернетом"
    AppError.Timeout -> "Сервер не ответил вовремя"
    is AppError.ServerError -> "Сервер недоступен, попробуйте позже"
    AppError.AllEndpointsUnavailable -> "Все известные адреса недоступны"
    is AppError.RateLimited -> "Слишком много запросов, подождите немного"
    AppError.NotFound -> "Не найдено"
    is AppError.ClientError -> "Запрос отклонён сервером"
    is AppError.ParseError -> "Не удалось обработать ответ сервера"
    is AppError.Unknown -> "Что-то пошло не так"
}
