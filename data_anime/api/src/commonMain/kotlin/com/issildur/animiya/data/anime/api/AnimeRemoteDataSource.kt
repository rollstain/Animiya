package com.issildur.animiya.data.anime.api

import com.issildur.animiya.core.utils.AppResult
import com.issildur.animiya.data.anime.api.model.Page
import com.issildur.animiya.data.anime.api.model.Release
import com.issildur.animiya.data.anime.api.model.ReleaseDetails

/**
 * Источник данных о релизах.
 *
 * ЭТО КЛЮЧЕВОЙ ШОВ АРХИТЕКТУРЫ. Интерфейс возвращает доменные модели, а не DTO,
 * поэтому переход на собственный backend-прокси сведётся к новой реализации
 * этого интерфейса (со своими DTO и мапперами) и подмене в Koin-модуле —
 * Repository, UseCase и UI не изменятся ни на строку.
 *
 * Именно ради этого шва слой пишется за интерфейсом с самого начала, хотя
 * реализация пока одна.
 */
interface AnimeRemoteDataSource {

    suspend fun getCatalog(page: Int, limit: Int): AppResult<Page<Release>>

    suspend fun getRelease(idOrAlias: String): AppResult<ReleaseDetails>

    suspend fun search(query: String): AppResult<List<Release>>

    suspend fun getLatest(limit: Int): AppResult<List<Release>>
}
