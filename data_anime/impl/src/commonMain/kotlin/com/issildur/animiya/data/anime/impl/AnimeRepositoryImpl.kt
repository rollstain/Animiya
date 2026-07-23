package com.issildur.animiya.data.anime.impl

import com.issildur.animiya.core.utils.AppResult
import com.issildur.animiya.data.anime.api.AnimeRemoteDataSource
import com.issildur.animiya.data.anime.api.AnimeRepository
import com.issildur.animiya.data.anime.api.model.Page
import com.issildur.animiya.data.anime.api.model.Release
import com.issildur.animiya.data.anime.api.model.ReleaseDetails
import com.issildur.animiya.data.anime.api.usecase.GetReleaseCatalogUseCase
import com.issildur.animiya.data.anime.impl.cache.AnimeLocalDataSource

/**
 * Network-first с откатом на кеш для каталога.
 *
 * API отдаёт `Cache-Control: no-cache` (HTTP-кеш бесполезен), а 500/504 у
 * AniLibria не редкость — поэтому при сбое сети отдаём последнюю успешную
 * страницу из SQLDelight. На успехе сети — пишем свежую страницу в кеш.
 *
 * getLatest / getRelease пока без кеша — добавим тем же паттерном при
 * необходимости офлайна для главной и деталей.
 */
class AnimeRepositoryImpl(
    private val remote: AnimeRemoteDataSource,
    private val local: AnimeLocalDataSource,
) : AnimeRepository {

    override suspend fun loadCatalogPage(
        page: Int,
        forceRefresh: Boolean,
    ): AppResult<Page<Release>> {
        return when (val remoteResult = remote.getCatalog(page, GetReleaseCatalogUseCase.DEFAULT_PAGE_SIZE)) {
            is AppResult.Success -> {
                local.saveCatalogPage(page, remoteResult.value.items)
                remoteResult
            }

            is AppResult.Failure -> {
                val cached = local.getCatalogPage(page)
                if (!cached.isNullOrEmpty()) {
                    // Из кеша пагинацию не продолжаем — hasNext=false.
                    AppResult.Success(
                        Page(items = cached, page = page, totalPages = null, hasNext = false),
                    )
                } else {
                    remoteResult
                }
            }
        }
    }

    override suspend fun getLatest(limit: Int): AppResult<List<Release>> =
        remote.getLatest(limit = limit)

    override suspend fun getRelease(idOrAlias: String): AppResult<ReleaseDetails> =
        remote.getRelease(idOrAlias)

    override suspend fun search(query: String): AppResult<List<Release>> =
        remote.search(query)
}
