package com.issildur.animiya.feature.catalog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.issildur.animiya.data.anime.api.usecase.GetReleaseCatalogUseCase
import com.issildur.animiya.data.anime.api.usecase.SearchReleasesUseCase
import com.issildur.animiya.uikit.theme.AnimiyaSpacing
import org.koin.compose.koinInject

@Composable
fun CatalogScreen(
    onReleaseClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val getCatalog: GetReleaseCatalogUseCase = koinInject()
    val search: SearchReleasesUseCase = koinInject()
    val scope = rememberCoroutineScope()
    val component = remember(scope) {
        DefaultCatalogComponent(scope = scope, getCatalog = getCatalog, search = search)
    }

    CatalogScreen(component = component, onReleaseClick = onReleaseClick, modifier = modifier)
}

@Composable
fun CatalogScreen(
    component: CatalogComponent,
    onReleaseClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by component.state.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        SearchField(
            query = state.query,
            onQueryChange = component::onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AnimiyaSpacing.md, vertical = AnimiyaSpacing.xs),
        )
        CatalogView(
            state = state,
            onRetry = component::onRetry,
            onLoadMore = component::onLoadMore,
            onReleaseClick = { onReleaseClick(it.idOrAlias) },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        singleLine = true,
        shape = CircleShape,
        placeholder = {
            Text(
                text = "Поиск аниме, студий…",
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
        ),
    )
}
