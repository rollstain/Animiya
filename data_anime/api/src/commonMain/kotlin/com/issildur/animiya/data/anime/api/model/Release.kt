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
 *
 * ВНИМАНИЕ по размерам (замерено на реальном постере):
 *   optimized.thumbnail — 258 байт
 *   thumbnail (jpg)     — 842 байта
 *   preview / src       — 55 КБ (webp) и 76 КБ (jpg), это один и тот же файл
 *
 * То есть [thumbnail] — НЕ уменьшенный постер, а микро-заглушка в несколько
 * пикселей, пригодная только под blur-плейсхолдер. Показывать её в сетке
 * нельзя: картинка выглядит «мыльной».
 */
data class ImageSet(
    val thumbnail: String? = null,
    val preview: String? = null,
    val full: String? = null,
) {
    /** Полноразмерный вариант. */
    fun best(): String? = full ?: preview ?: thumbnail

    /**
     * Вариант для сетки каталога — тоже полноразмерный.
     * WebP из optimized уже даёт экономию ~28% против JPEG, а [thumbnail]
     * для отображения непригоден (см. описание класса).
     */
    fun forGrid(): String? = preview ?: full ?: thumbnail

    /** Микро-заглушка на время загрузки настоящего постера. */
    fun blurPlaceholder(): String? = thumbnail

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
    /** Сколько пользователей добавили релиз в избранное — соц-доказательство на карточке. */
    val inListsCount: Int?,
) {
    val isBlocked: Boolean get() = availability != Availability.AVAILABLE
}

/**
 * Релиз с озвучками — из детального запроса.
 *
 * Список переводов: у AniLibria пока один (своя озвучка), но структура готова
 * к нескольким источникам (AnimeVost, Sovetromantica) — их добавит агрегация
 * на репозитории/бэкенде без изменения модели.
 */
data class ReleaseDetails(
    val release: Release,
    val translations: List<Translation>,
) {
    val primaryTranslation: Translation? get() = translations.firstOrNull()
}
