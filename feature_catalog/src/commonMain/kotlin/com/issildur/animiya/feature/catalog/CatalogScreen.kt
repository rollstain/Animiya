package com.issildur.animiya.feature.catalog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.issildur.animiya.data.anime.api.usecase.GetReleaseCatalogUseCase
import org.koin.compose.koinInject

/**
 * Единственный мост Component -> View.
 *
 * Ровно эта пара Screen/View позволит написать TV-вариант, переиспользовав
 * и состояние, и компонент: у него будет свой CatalogTvView и тот же
 * [CatalogComponent].
 */
@Composable
fun CatalogScreen(modifier: Modifier = Modifier) {
    val getCatalog: GetReleaseCatalogUseCase = koinInject()
    val scope = rememberCoroutineScope()
    val component = remember(scope) {
        DefaultCatalogComponent(scope = scope, getCatalog = getCatalog)
    }

    CatalogScreen(component = component, modifier = modifier)
}

@Composable
fun CatalogScreen(
    component: CatalogComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.collectAsState()

    CatalogView(
        state = state,
        onRetry = component::onRetry,
        onLoadMore = component::onLoadMore,
        modifier = modifier,
    )
}
