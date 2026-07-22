package com.issildur.animiya.feature.catalog

import androidx.compose.runtime.Immutable
import com.issildur.animiya.core.utils.AppError
import com.issildur.animiya.data.anime.api.model.ReleaseId

/**
 * Готовая к отрисовке карточка.
 *
 * Доменная модель не попадает во View намеренно: сборка подписи и выбор
 * размера постера — это логика, и ей не место в Composable.
 */
@Immutable
data class ReleaseUiModel(
    val id: ReleaseId,
    /** Что передавать в запрос деталей: alias, если он есть, иначе числовой id. */
    val idOrAlias: String,
    val title: String,
    /** «2026 · Лето · ТВ» — собирается заранее, null-поля пропускаются. */
    val subtitle: String,
    val posterUrl: String?,
    val isOngoing: Boolean,
    val blockedReason: String?,
)

@Immutable
sealed interface CatalogContent {
    data object Loading : CatalogContent
    data object Empty : CatalogContent
    data class Error(val error: AppError) : CatalogContent
    data class Items(
        val releases: List<ReleaseUiModel>,
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
