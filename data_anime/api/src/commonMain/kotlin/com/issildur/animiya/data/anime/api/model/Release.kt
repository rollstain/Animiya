package com.issildur.animiya.data.anime.api.model

import kotlin.jvm.JvmInline

/**
 * Идентификатор релиза.
 *
 * Обёрнут намеренно: у релиза id — целое число, а у эпизода — UUID-строка
 * (проверено живым запросом). Разные типы не дают их перепутать.
 */
@JvmInline
value class ReleaseId(val raw: Int)

/**
 * Набор вариантов изображения.
 * URL здесь уже АБСОЛЮТНЫЕ — склейка с mediaBaseUrl происходит в мапперах.
 */
data class ImageSet(
    val thumbnail: String? = null,
    val preview: String? = null,
    val full: String? = null,
) {
    /** Наиболее подходящий доступный вариант, от мелкого к крупному. */
    fun best(): String? = full ?: preview ?: thumbnail

    /** Вариант для сетки каталога. */
    fun forGrid(): String? = thumbnail ?: preview ?: full

    companion object {
        val Empty = ImageSet()
    }
}

/**
 * Доступность релиза.
 *
 * Это доменное состояние из успешного ответа, а НЕ ошибка транспорта —
 * поэтому здесь, а не в AppError.
 */
enum class Availability {
    AVAILABLE,
    GEO_BLOCKED,
    COPYRIGHT_BLOCKED,
}

data class Genre(
    val id: Int,
    val name: String,
)

data class Release(
    val id: ReleaseId,
    val alias: String?,
    val title: String,
    val titleEnglish: String?,
    val year: Int?,
    val season: String?,
    val type: String?,
    val poster: ImageSet,
    val isOngoing: Boolean,
    val episodesTotal: Int?,
    val ageRatingLabel: String?,
    val isAdult: Boolean,
    val availability: Availability,
    val genres: List<Genre>,
    val description: String?,
    /** 1 — понедельник, 7 — воскресенье. В v1 нумерация с единицы, в старом v3 была с нуля. */
    val publishWeekday: Int?,
) {
    val isBlocked: Boolean get() = availability != Availability.AVAILABLE
}

/** Релиз вместе с эпизодами — приходит только из детального запроса. */
data class ReleaseDetails(
    val release: Release,
    val episodes: List<Episode>,
)
