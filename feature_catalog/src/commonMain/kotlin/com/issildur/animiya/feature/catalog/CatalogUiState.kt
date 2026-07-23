package com.issildur.animiya.feature.catalog

import androidx.compose.runtime.Immutable
import com.issildur.animiya.core.ui.ReleasePosterUi
import com.issildur.animiya.core.utils.AppError

@Immutable
sealed interface CatalogContent {
    data object Loading : CatalogContent
    data object Empty : CatalogContent
    data class Error(val error: AppError) : CatalogContent
    data class Items(
        val releases: List<ReleasePosterUi>,
        val hasNext: Boolean,
    ) : CatalogContent
}

@Immutable
data class CatalogUiState(
    val content: CatalogContent = CatalogContent.Loading,
    /** Догрузка следующей страницы — отдельно от первичной загрузки. */
    val isAppending: Boolean = false,
    val appendError: AppError? = null,
)
