package com.issildur.animiya.data.anime.impl.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Релиз.
 *
 * ОДИН широкий DTO на все эндпоинты — осознанное решение. Один и тот же объект
 * приходит с разным набором полей (проверено живыми запросами):
 *   - catalog/releases  — есть genres, нет latest_episode
 *   - app/search        — нет ни genres, ни latest_episode
 *   - releases/latest   — есть и genres, и latest_episode
 *   - releases/{alias}  — есть episodes, members, torrents
 * Отдельные DTO на эндпоинт означали бы дублирование ~27 полей четырежды.
 *
 * Практически всё nullable намеренно: API реально отдаёт null там, где по
 * здравому смыслу ожидается значение (`episodes_total` пустой даже у релиза
 * с эпизодами).
 */
@Serializable
data class ReleaseDto(
    @SerialName("id") val id: Int,
    @SerialName("alias") val alias: String? = null,
    @SerialName("name") val name: NameDto? = null,
    @SerialName("year") val year: Int? = null,
    @SerialName("type") val type: ValueDescriptionDto? = null,
    @SerialName("season") val season: ValueDescriptionDto? = null,
    @SerialName("poster") val poster: ImageSetDto? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("is_ongoing") val isOngoing: Boolean = false,
    @SerialName("is_in_production") val isInProduction: Boolean = false,
    @SerialName("is_blocked_by_geo") val isBlockedByGeo: Boolean = false,
    @SerialName("is_blocked_by_copyrights") val isBlockedByCopyrights: Boolean = false,
    @SerialName("episodes_total") val episodesTotal: Int? = null,
    @SerialName("average_duration_of_episode") val averageDurationOfEpisode: Int? = null,
    @SerialName("age_rating") val ageRating: AgeRatingDto? = null,
    @SerialName("publish_day") val publishDay: IntValueDescriptionDto? = null,
    @SerialName("added_in_users_favorites") val addedInUsersFavorites: Int? = null,
    @SerialName("genres") val genres: List<GenreDto> = emptyList(),
    @SerialName("episodes") val episodes: List<EpisodeDto> = emptyList(),
    @SerialName("fresh_at") val freshAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
data class NameDto(
    @SerialName("main") val main: String? = null,
    @SerialName("english") val english: String? = null,
    @SerialName("alternative") val alternative: String? = null,
)

/**
 * Enum-обёртка вида `{"value": "TV", "description": "ТВ"}`.
 *
 * Разбираем именно как объект, а не как строку: если на бэке появится новое
 * значение, парсинг не упадёт — мы читаем сырую строку и сопоставляем её
 * с известными значениями уже в маппере, с безопасным fallback.
 */
@Serializable
data class ValueDescriptionDto(
    @SerialName("value") val value: String? = null,
    @SerialName("description") val description: String? = null,
)

/** То же, но с числовым значением (`publish_day`: 1 — Пн … 7 — Вс). */
@Serializable
data class IntValueDescriptionDto(
    @SerialName("value") val value: Int? = null,
    @SerialName("description") val description: String? = null,
)

@Serializable
data class AgeRatingDto(
    @SerialName("value") val value: String? = null,
    @SerialName("label") val label: String? = null,
    @SerialName("is_adult") val isAdult: Boolean = false,
    @SerialName("description") val description: String? = null,
)

@Serializable
data class GenreDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String? = null,
    @SerialName("total_releases") val totalReleases: Int? = null,
)

/**
 * Набор изображений.
 *
 * ВНИМАНИЕ: пути ОТНОСИТЕЛЬНЫЕ (`/storage/releases/posters/...`) — в отличие от
 * ссылок на HLS, которые приходят абсолютными. Склейка с mediaBaseUrl
 * обязательна, иначе картинки молча не загрузятся.
 */
@Serializable
data class ImageSetDto(
    @SerialName("src") val src: String? = null,
    @SerialName("preview") val preview: String? = null,
    @SerialName("thumbnail") val thumbnail: String? = null,
    @SerialName("optimized") val optimized: OptimizedImageSetDto? = null,
)

/** WebP-версии тех же изображений — заметно легче, для мобильного предпочтительны. */
@Serializable
data class OptimizedImageSetDto(
    @SerialName("src") val src: String? = null,
    @SerialName("preview") val preview: String? = null,
    @SerialName("thumbnail") val thumbnail: String? = null,
)
