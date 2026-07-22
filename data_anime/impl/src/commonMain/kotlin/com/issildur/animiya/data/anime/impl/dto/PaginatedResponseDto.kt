package com.issildur.animiya.data.anime.impl.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Конверт с пагинацией.
 *
 * ВАЖНО: API непоследователен. В такой конверт завёрнут ТОЛЬКО каталог
 * (`anime/catalog/releases`). Поиск, `releases/latest`, `anime/genres` и
 * `anime/schedule/week` отдают голый массив без метаданных.
 * Поэтому единого generic-враппера на все запросы здесь быть не может.
 */
@Serializable
data class PaginatedResponseDto<T>(
    @SerialName("data") val data: List<T> = emptyList(),
    @SerialName("meta") val meta: MetaDto? = null,
)

@Serializable
data class MetaDto(
    @SerialName("pagination") val pagination: PaginationDto? = null,
)

@Serializable
data class PaginationDto(
    @SerialName("total") val total: Int? = null,
    @SerialName("count") val count: Int? = null,
    @SerialName("per_page") val perPage: Int? = null,
    @SerialName("current_page") val currentPage: Int? = null,
    @SerialName("total_pages") val totalPages: Int? = null,
)
