package com.issildur.animiya.data.anime.impl

import com.issildur.animiya.core.utils.AppResult
import com.issildur.animiya.data.anime.api.AnimeRemoteDataSource
import com.issildur.animiya.data.anime.api.AnimeRepository
import com.issildur.animiya.data.anime.api.model.Page
import com.issildur.animiya.data.anime.api.model.Release
import com.issildur.animiya.data.anime.api.model.ReleaseDetails
import com.issildur.animiya.data.anime.api.usecase.GetReleaseCatalogUseCase

/**
 * Пока — тонкий проброс в сеть.
 *
 * Локального кеша сознательно ещё нет: SQLDelight подключается отдельным шагом,
 * так как это самый вероятный источник поломки сборки. Когда он появится,
 * стратегия станет network-first с записью в кеш и откатом на кеш при сбое —
 * это важно, потому что API отдаёт `Cache-Control: no-cache` и HTTP-кеш
 * бесполезен, а 500/504 у источника случаются регулярно.
 */
class AnimeRepositoryImpl(
    private val remote: AnimeRemoteDataSource,
) : AnimeRepository {

    override suspend fun loadCatalogPage(
        page: Int,
        forceRefresh: Boolean,
    ): AppResult<Page<Release>> = remote.getCatalog(
        page = page,
        limit = GetReleaseCatalogUseCase.DEFAULT_PAGE_SIZE,
    )

    override suspend fun getLatest(limit: Int): AppResult<List<Release>> =
        remote.getLatest(limit = limit)

    override suspend fun getRelease(idOrAlias: String): AppResult<ReleaseDetails> =
        remote.getRelease(idOrAlias)

    override suspend fun search(query: String): AppResult<List<Release>> =
        remote.search(query)
}
