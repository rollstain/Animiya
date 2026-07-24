package com.issildur.animiya.data.anime.api.model

/** Источник видео. Уровень 1+2: прямой поток (наш плеер) + свои embed. */
enum class VideoSource {
    ANILIBRIA,
    ANIMEVOST,
    SOVETROMANTICA,
}

/** Тип перевода. UNKNOWN — когда источник не сообщает точно (напр. AniLibria). */
enum class TranslationKind {
    DUB,
    MULTI_VOICE,
    VOICE,
    SUBTITLES,
    UNKNOWN,
}

/**
 * Озвучка/перевод одного тайтла от одного источника — главная выбираемая сущность
 * мультиозвучки. У разных переводов РАЗНОЕ число серий и своя нумерация, поэтому
 * эпизоды живут ВНУТРИ перевода, а не глобально.
 */
data class Translation(
    /** Стабильный ключ (source + студия) для памяти выбора по тайтлу. */
    val id: String,
    val source: VideoSource,
    val studio: String,
    val kind: TranslationKind,
    val episodes: List<Episode>,
) {
    val episodeCount: Int get() = episodes.size

    /** true — воспроизведение через WebView (embed), не наш нативный плеер. */
    val isExternal: Boolean
        get() = episodes.firstOrNull { it.playback != null }?.playback is Playback.Embed

    val maxQualityHeight: Int?
        get() = episodes.asSequence()
            .flatMap { it.directStreams().asSequence() }
            .maxOfOrNull { it.quality.height }
}
