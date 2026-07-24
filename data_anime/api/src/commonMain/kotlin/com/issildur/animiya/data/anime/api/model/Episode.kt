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

/** Контейнер прямого потока. Нативный плеер (Media3/AVPlayer) играет оба по URL. */
enum class StreamContainer { HLS, MP4 }

/** Прямая ссылка на видеопоток конкретного качества. */
data class Stream(
    val quality: VideoQuality,
    val url: String,
    val container: StreamContainer,
)

/**
 * Способ воспроизведения эпизода.
 *
 * [Direct] — прямые потоки (AniLibria HLS, AnimeVost mp4) → наш нативный плеер.
 * [Embed]  — страница-плеер источника (Sovetromantica) → WebView, наши фичи там
 *            не работают. Разделение по типам — спина мультиисточниковой модели.
 */
sealed interface Playback {
    data class Direct(val streams: List<Stream>) : Playback
    data class Embed(val url: String) : Playback
}

/** Отрезок для пропуска (опенинг/эндинг), в секундах от начала эпизода. */
data class Timecode(
    val startSec: Int,
    val stopSec: Int,
) {
    val durationSec: Int get() = stopSec - startSec
}

data class Episode(
    val id: EpisodeId,
    /** Номер серии. Дробный намеренно: у спешлов встречаются номера вида 7.5. */
    val ordinal: Double?,
    val title: String?,
    val durationSec: Int?,
    val preview: ImageSet,
    val opening: Timecode?,
    val ending: Timecode?,
    val playback: Playback?,
) {
    val isPlayable: Boolean get() = playback != null

    fun directStreams(): List<Stream> = (playback as? Playback.Direct)?.streams.orEmpty()

    fun bestStream(): Stream? = directStreams().firstOrNull()

    fun streamFor(quality: VideoQuality): Stream? = directStreams().firstOrNull { it.quality == quality }

    val maxQualityHeight: Int? get() = directStreams().maxOfOrNull { it.quality.height }
}
