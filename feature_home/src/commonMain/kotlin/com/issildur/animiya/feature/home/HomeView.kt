package com.issildur.animiya.feature.home

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.issildur.animiya.core.ui.AppErrorContent
import com.issildur.animiya.core.ui.ReleasePosterUi
import com.issildur.animiya.uikit.component.Badge
import com.issildur.animiya.uikit.component.BadgeTone
import com.issildur.animiya.uikit.component.Backdrop
import com.issildur.animiya.uikit.component.LoadingState
import com.issildur.animiya.uikit.component.PrimaryButton
import com.issildur.animiya.uikit.component.SectionHeader
import com.issildur.animiya.uikit.component.TitlePosterCard
import com.issildur.animiya.uikit.theme.AnimiyaSpacing

private val RailCardWidth = 132.dp

/**
 * Discovery-витрина: hero + горизонтальные полки. Чистая функция от состояния —
 * рядом можно написать TV-вариант, потребляющий тот же [HomeContent].
 */
@Composable
fun HomeView(
    state: HomeContent,
    onReleaseClick: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        when (state) {
            HomeContent.Loading -> LoadingState()
            is HomeContent.Error -> AppErrorContent(error = state.error, onRetry = onRetry)
            is HomeContent.Content -> HomeContentList(state, onReleaseClick)
        }
    }
}

@Composable
private fun HomeContentList(
    content: HomeContent.Content,
    onReleaseClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = AnimiyaSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(AnimiyaSpacing.lg),
    ) {
        content.hero?.let { hero ->
            item { Hero(hero = hero, onClick = { onReleaseClick(hero.idOrAlias) }) }
        }
        items(items = content.rails, key = { it.title }) { rail ->
            Rail(rail = rail, onReleaseClick = onReleaseClick)
        }
    }
}

@Composable
private fun Hero(hero: HeroUi, onClick: () -> Unit) {
    Column {
        Box(modifier = Modifier.fillMaxWidth()) {
            Backdrop(url = hero.backdropUrl, contentDescription = hero.title, scrim = true)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(AnimiyaSpacing.md),
            ) {
                if (hero.badge != null) {
                    Badge(text = hero.badge, tone = BadgeTone.Accent)
                    Spacer(modifier = Modifier.height(AnimiyaSpacing.xs))
                }
                Text(
                    text = hero.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (hero.subtitle.isNotEmpty()) {
                    Text(
                        text = hero.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(AnimiyaSpacing.sm))
        PrimaryButton(
            text = "▶  Смотреть",
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AnimiyaSpacing.md),
        )
    }
}

@Composable
private fun Rail(rail: HomeRail, onReleaseClick: (String) -> Unit) {
    Column {
        SectionHeader(
            title = rail.title,
            modifier = Modifier.padding(horizontal = AnimiyaSpacing.md),
        )
        Spacer(modifier = Modifier.height(AnimiyaSpacing.sm))
        LazyRow(
            contentPadding = PaddingValues(horizontal = AnimiyaSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(AnimiyaSpacing.sm),
        ) {
            items(items = rail.items, key = { it.id.raw }) { item ->
                RailCard(item = item, onClick = { onReleaseClick(item.idOrAlias) })
            }
        }
    }
}

@Composable
private fun RailCard(item: ReleasePosterUi, onClick: () -> Unit) {
    TitlePosterCard(
        posterUrl = item.posterUrl,
        title = item.title,
        subtitle = item.subtitle,
        onClick = onClick,
        modifier = Modifier.width(RailCardWidth),
        overlay = {
            if (item.blockedReason != null) {
                Badge(
                    text = "🔒",
                    tone = BadgeTone.Danger,
                    modifier = Modifier.align(Alignment.TopEnd).padding(AnimiyaSpacing.xs),
                )
            } else if (item.isOngoing) {
                Badge(
                    text = "Онгоинг",
                    tone = BadgeTone.Success,
                    leadingDot = true,
                    modifier = Modifier.align(Alignment.TopStart).padding(AnimiyaSpacing.xs),
                )
            }
        },
    )
}
