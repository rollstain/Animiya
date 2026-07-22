package com.issildur.animiya.data.anime.impl

import com.issildur.animiya.core.network.api.ApiEndpoint
import com.issildur.animiya.core.network.api.ApiEndpointProvider
import com.issildur.animiya.core.network.impl.toAppError
import com.issildur.animiya.core.utils.AppError
import com.issildur.animiya.core.utils.AppResult
import com.issildur.animiya.core.utils.map
import com.issildur.animiya.data.anime.api.AnimeRemoteDataSource
import com.issildur.animiya.data.anime.api.model.Page
import com.issildur.animiya.data.anime.api.model.Release
import com.issildur.animiya.data.anime.api.model.ReleaseDetails
import com.issildur.animiya.data.anime.impl.dto.PaginatedResponseDto
import com.issildur.animiya.data.anime.impl.dto.ReleaseDto
import com.issildur.animiya.data.anime.impl.mapper.ReleaseMapper
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.CancellationException

/**
 * Реализация поверх публичного API AniLibria/AniLiberty v1.
 *
 * Пути задаются ТОЛЬКО относительные — хост подставляется из
 * [ApiEndpointProvider]. Это и есть шов, позволяющий позже подменить источник
 * на собственный backend-прокси, не трогая слои выше.
 *
 * Переключение на зеркало реализовано здесь, а не Ktor-плагином, намеренно:
 * плагин пришлось бы согласовывать по порядку с HttpRequestRetry, что даёт
 * неочевидное поведение. Явный цикл детерминирован и тестируем.
 */
class AnilibriaRemoteDataSource(
    private val client: HttpClient,
    private val endpointProvider: ApiEndpointProvider,
    private val mapper: ReleaseMapper,
) : AnimeRemoteDataSource {

    override suspend fun getCatalog(page: Int, limit: Int): AppResult<Page<Release>> =
        request<PaginatedResponseDto<ReleaseDto>>(
            path = "anime/catalog/releases",
            params = mapOf("page" to page, "limit" to limit),
        ).map { response ->
            val pagination = response.meta?.pagination
            val currentPage = pagination?.currentPage ?: page
            val totalPages = pagination?.totalPages
            Page(
                items = mapper.toDomainList(response.data),
                page = currentPage,
                totalPages = totalPages,
                hasNext = totalPages?.let { currentPage < it }
                    ?: response.data.size.let { it > 0 && it >= limit },
            )
        }

    override suspend fun getRelease(idOrAlias: String): AppResult<ReleaseDetails> =
        request<ReleaseDto>(path = "anime/releases/$idOrAlias")
            .map { mapper.toDetails(it) }

    /** Поиск отдаёт ГОЛЫЙ массив без конверта — в отличие от каталога. */
    override suspend fun search(query: String): AppResult<List<Release>> =
        request<List<ReleaseDto>>(
            path = "app/search/releases",
            params = mapOf("query" to query),
        ).map { mapper.toDomainList(it) }

    /** Тоже голый массив. */
    override suspend fun getLatest(limit: Int): AppResult<List<Release>> =
        request<List<ReleaseDto>>(
            path = "anime/releases/latest",
            params = mapOf("limit" to limit),
        ).map { mapper.toDomainList(it) }

    /**
     * GET с перебором зеркал.
     *
     * Ретраи на одном хосте уже сделал HttpRequestRetry внутри клиента; сюда
     * управление доходит, только когда хост исчерпал попытки. Тогда пробуем
     * следующий эндпоинт.
     */
    private suspend inline fun <reified T> request(
        path: String,
        params: Map<String, Any?> = emptyMap(),
    ): AppResult<T> {
        var endpoint: ApiEndpoint = endpointProvider.current()
        var lastError: AppError

        while (true) {
            try {
                val value = client.get(endpoint.apiBaseUrl + path) {
                    params.forEach { (key, raw) -> if (raw != null) parameter(key, raw) }
                }.body<T>()
                return AppResult.Success(value)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                lastError = throwable.toAppError()

                // Ошибки клиента (404, 4xx) на зеркале воспроизведутся один в один —
                // перебирать хосты бессмысленно.
                if (!lastError.isWorthTryingAnotherEndpoint()) {
                    return AppResult.Failure(lastError)
                }
                endpoint = endpointProvider.rotate(endpoint)
                    ?: return AppResult.Failure(AppError.AllEndpointsUnavailable)
            }
        }
    }
}

/** Есть ли смысл повторить запрос на другом зеркале. */
private fun AppError.isWorthTryingAnotherEndpoint(): Boolean = when (this) {
    is AppError.NoConnection,
    is AppError.Timeout,
    is AppError.ServerError,
    -> true

    is AppError.ClientError,
    is AppError.NotFound,
    is AppError.RateLimited,
    is AppError.ParseError,
    is AppError.AllEndpointsUnavailable,
    is AppError.Unknown,
    -> false
}
