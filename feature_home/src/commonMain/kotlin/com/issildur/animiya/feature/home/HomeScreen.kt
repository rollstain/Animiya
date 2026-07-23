package com.issildur.animiya.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.issildur.animiya.data.anime.api.usecase.GetLatestReleasesUseCase
import com.issildur.animiya.data.anime.api.usecase.GetReleaseCatalogUseCase
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    onReleaseClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val getLatest: GetLatestReleasesUseCase = koinInject()
    val getCatalog: GetReleaseCatalogUseCase = koinInject()
    val scope = rememberCoroutineScope()
    val component = remember(scope) {
        DefaultHomeComponent(scope = scope, getLatest = getLatest, getCatalog = getCatalog)
    }
    HomeScreen(component = component, onReleaseClick = onReleaseClick, modifier = modifier)
}

@Composable
fun HomeScreen(
    component: HomeComponent,
    onReleaseClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by component.state.collectAsState()
    HomeView(
        state = state,
        onReleaseClick = onReleaseClick,
        onRetry = component::onRetry,
        modifier = modifier,
    )
}
