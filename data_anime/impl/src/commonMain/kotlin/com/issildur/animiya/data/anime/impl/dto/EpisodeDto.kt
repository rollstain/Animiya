package com.issildur.animiya.data.anime.impl.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Эпизод.
 *
 * [id] — строка (UUID), а не число: у релиза id целочисленный, у эпизода —
 * `"a2357e00-217e-4d7f-9c55-a4fee04e741e"`. Наивный Int здесь уронил бы
 * разбор всего детального ответа.
 */
@Serializable
data class EpisodeDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String? = null,
    @SerialName("name_english") val nameEnglish: String? = null,
    /** Дробный: у спешлов встречаются номера вида 7.5. */
    @SerialName("ordinal") val ordinal: Double? = null,
    @SerialName("sort_order") val sortOrder: Int? = null,
    @SerialName("duration") val duration: Int? = null,
    @SerialName("preview") val preview: ImageSetDto? = null,
    @SerialName("opening") val opening: TimecodeDto? = null,
    @SerialName("ending") val ending: TimecodeDto? = null,
    /** Абсолютные URL на cache.libria.fun. Любое качество может отсутствовать. */
    @SerialName("hls_480") val hls480: String? = null,
    @SerialName("hls_720") val hls720: String? = null,
    @SerialName("hls_1080") val hls1080: String? = null,
    @SerialName("rutube_id") val rutubeId: String? = null,
    @SerialName("youtube_id") val youtubeId: String? = null,
)

/** Границы отрезка в секундах. Часто приходит с null внутри. */
@Serializable
data class TimecodeDto(
    @SerialName("start") val start: Int? = null,
    @SerialName("stop") val stop: Int? = null,
)
