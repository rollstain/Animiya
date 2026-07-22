package com.issildur.animiya.data.anime.api.usecase

import com.issildur.animiya.core.utils.AppResult
import com.issildur.animiya.data.anime.api.AnimeRepository
import com.issildur.animiya.data.anime.api.model.Page
import com.issildur.animiya.data.anime.api.model.Release

/**
 * Страница каталога релизов.
 *
 * UseCase намеренно тонкий. Его ценность не в логике, а в том, что Component
 * зависит от одного узкого контракта вместо всего репозитория.
 */
class GetReleaseCatalogUseCase(
    private val repository: AnimeRepository,
) {
    suspend operator fun invoke(
        page: Int = FIRST_PAGE,
        forceRefresh: Boolean = false,
    ): AppResult<Page<Release>> = repository.loadCatalogPage(page = page, forceRefresh = forceRefresh)

    companion object {
        const val FIRST_PAGE = 1
        const val DEFAULT_PAGE_SIZE = 30
    }
}
