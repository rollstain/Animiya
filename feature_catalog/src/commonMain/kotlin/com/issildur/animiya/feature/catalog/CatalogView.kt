package com.issildur.animiya.feature.catalog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.issildur.animiya.core.ui.AppErrorContent
import com.issildur.animiya.core.utils.AppError
import com.issildur.animiya.uikit.component.Badge
import com.issildur.animiya.uikit.component.BadgeTone
import com.issildur.animiya.uikit.component.EmptyState
import com.issildur.animiya.uikit.component.LoadingState
import com.issildur.animiya.uikit.component.Poster
import com.issildur.animiya.uikit.component.SecondaryButton
import com.issildur.animiya.uikit.theme.AnimiyaSpacing
import kotlinx.coroutines.flow.distinctUntilChanged

private const val PREFETCH_DISTANCE = 6

/**
 * Чистая функция от состояния — не знает про [CatalogComponent].
 * Собрана на компонентах :uikit, состояния — из :uikit / :core_ui.
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
            CatalogContent.Loading -> LoadingState()

            CatalogContent.Empty -> EmptyState(text = "Ничего не найдено")

            is CatalogContent.Error -> AppErrorContent(error = content.error, onRetry = onRetry)

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
            // Adaptive — корректный вид на планшете и позже на Android TV без отдельной вёрстки.
            columns = GridCells.Adaptive(minSize = 120.dp),
            contentPadding = PaddingValues(AnimiyaSpacing.sm),
            horizontalArrangement = Arrangement.spacedBy(AnimiyaSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(AnimiyaSpacing.md),
            modifier = Modifier.weight(1f).fillMaxWidth(),
        ) {
            items(items = releases, key = { it.id.raw }) { release ->
                ReleaseCard(release = release, onClick = { onReleaseClick(release) })
            }
        }

        if (isAppending) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(AnimiyaSpacing.md),
                contentAlignment = Alignment.Center,
            ) {
                LoadingState(modifier = Modifier.height(48.dp))
            }
        }
        if (appendError != null) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(AnimiyaSpacing.md),
                contentAlignment = Alignment.Center,
            ) {
                SecondaryButton(text = "Не удалось загрузить ещё · Повторить", onClick = onRetryAppend)
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
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Poster(url = release.posterUrl, contentDescription = release.title)
            if (release.blockedReason != null) {
                Badge(
                    text = "🔒",
                    tone = BadgeTone.Danger,
                    modifier = Modifier.align(Alignment.TopEnd).padding(AnimiyaSpacing.xs),
                )
            } else if (release.isOngoing) {
                Badge(
                    text = "Онгоинг",
                    tone = BadgeTone.Success,
                    leadingDot = true,
                    modifier = Modifier.align(Alignment.TopStart).padding(AnimiyaSpacing.xs),
                )
            }
        }
        Spacer(modifier = Modifier.height(AnimiyaSpacing.xs))
        Text(
            text = release.title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
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
