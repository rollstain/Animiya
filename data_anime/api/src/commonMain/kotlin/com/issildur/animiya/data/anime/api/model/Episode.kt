package com.issildur.animiya.data.anime.api.model

import kotlin.jvm.JvmInline

/** Идентификатор эпизода — UUID-строка (в отличие от целочисленного [ReleaseId]). */
@JvmInline
value class EpisodeId(val raw: String)

enum class VideoQuality(val height: Int) {
    P480(480),
    P720(720),
    P1080(1080),
}

/** Готовая ссылка на HLS-манифест. В отличие от постеров, приходит абсолютной. */
data class HlsStream(
    val quality: VideoQuality,
    val url: String,
)

/** Отрезок для пропуска (опенинг/эндинг), в секундах от начала эпизода. */
data class Timecode(
    val startSec: Int,
    val stopSec: Int,
) {
    val durationSec: Int get() = stopSec - startSec
}

data class Episode(
    val id: EpisodeId,
    /**
     * Номер серии. Дробный намеренно: у спешлов и рекапов встречаются
     * номера вида 7.5.
     */
    val ordinal: Double?,
    val title: String?,
    val durationSec: Int?,
    val preview: ImageSet,
    val opening: Timecode?,
    val ending: Timecode?,
    /** Только доступные качества, отсортированы по убыванию. Может быть пустым. */
    val streams: List<HlsStream>,
) {
    val hasVideo: Boolean get() = streams.isNotEmpty()

    fun bestStream(): HlsStream? = streams.firstOrNull()

    fun streamFor(quality: VideoQuality): HlsStream? = streams.firstOrNull { it.quality == quality }
}
