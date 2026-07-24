package com.issildur.animiya.data.anime.impl.mapper

import com.issildur.animiya.data.anime.api.model.Availability
import com.issildur.animiya.data.anime.api.model.Episode
import com.issildur.animiya.data.anime.api.model.EpisodeId
import com.issildur.animiya.data.anime.api.model.Genre
import com.issildur.animiya.data.anime.api.model.Playback
import com.issildur.animiya.data.anime.api.model.Release
import com.issildur.animiya.data.anime.api.model.ReleaseDetails
import com.issildur.animiya.data.anime.api.model.ReleaseId
import com.issildur.animiya.data.anime.api.model.Stream
import com.issildur.animiya.data.anime.api.model.StreamContainer
import com.issildur.animiya.data.anime.api.model.Timecode
import com.issildur.animiya.data.anime.api.model.Translation
import com.issildur.animiya.data.anime.api.model.TranslationKind
import com.issildur.animiya.data.anime.api.model.VideoQuality
import com.issildur.animiya.data.anime.api.model.VideoSource
import com.issildur.animiya.data.anime.impl.dto.EpisodeDto
import com.issildur.animiya.data.anime.impl.dto.GenreDto
import com.issildur.animiya.data.anime.impl.dto.ReleaseDto
import com.issildur.animiya.data.anime.impl.dto.TimecodeDto

private const val FALLBACK_TITLE = "Без названия"

/**
 * DTO -> домен.
 *
 * Главное правило: мапперы НИКОГДА не бросают исключений. Один битый элемент
 * не должен ронять всю страницу — он просто выпадает из выдачи
 * (см. [toDomainList]). API отдаёт данные неравномерного качества, и падение
 * каталога из-за одного кривого релиза недопустимо.
 */
class ReleaseMapper(
    private val urls: ImageUrlResolver,
) {

    fun toDomain(dto: ReleaseDto): Release = Release(
        id = ReleaseId(dto.id),
        alias = dto.alias?.takeIf { it.isNotBlank() },
        title = dto.name?.main?.takeIf { it.isNotBlank() }
            ?: dto.name?.english?.takeIf { it.isNotBlank() }
            ?: FALLBACK_TITLE,
        titleEnglish = dto.name?.english?.takeIf { it.isNotBlank() },
        year = dto.year,
        season = dto.season?.description?.takeIf { it.isNotBlank() },
        type = dto.type?.description?.takeIf { it.isNotBlank() },
        poster = urls.toImageSet(dto.poster),
        isOngoing = dto.isOngoing,
        episodesTotal = dto.episodesTotal,
        ageRatingLabel = dto.ageRating?.label?.takeIf { it.isNotBlank() },
        isAdult = dto.ageRating?.isAdult ?: false,
        availability = dto.toAvailability(),
        genres = dto.genres.mapNotNull { it.toDomainOrNull() },
        description = dto.description?.takeIf { it.isNotBlank() },
        publishWeekday = dto.publishDay?.value?.takeIf { it in 1..7 },
        inListsCount = dto.addedInUsersFavorites?.takeIf { it > 0 },
    )

    /** Битые элементы отбрасываются, а не роняют всю страницу. */
    fun toDomainList(dtos: List<ReleaseDto>): List<Release> =
        dtos.mapNotNull { dto -> runCatching { toDomain(dto) }.getOrNull() }

    fun toDetails(dto: ReleaseDto): ReleaseDetails {
        val episodes = dto.episodes
            .mapNotNull { episode -> runCatching { toEpisode(episode) }.getOrNull() }
            .sortedBy { it.ordinal ?: Double.MAX_VALUE }

        // AniLibria = одна озвучка. Заворачиваем в один Translation; структура
        // готова к нескольким источникам (AnimeVost/Sovetromantica добавит агрегация).
        val translations = if (episodes.isEmpty()) {
            emptyList()
        } else {
            listOf(
                Translation(
                    id = "anilibria:${dto.id}",
                    source = VideoSource.ANILIBRIA,
                    studio = "AniLibria",
                    kind = TranslationKind.VOICE,
                    episodes = episodes,
                ),
            )
        }
        return ReleaseDetails(release = toDomain(dto), translations = translations)
    }

    fun toEpisode(dto: EpisodeDto): Episode {
        val streams = dto.toStreams()
        return Episode(
            id = EpisodeId(dto.id),
            ordinal = dto.ordinal,
            title = dto.name?.takeIf { it.isNotBlank() },
            durationSec = dto.duration?.takeIf { it > 0 },
            preview = urls.toImageSet(dto.preview),
            opening = dto.opening.toTimecodeOrNull(),
            ending = dto.ending.toTimecodeOrNull(),
            // Пустой список потоков = серия ещё без видео → playback отсутствует.
            playback = if (streams.isEmpty()) null else Playback.Direct(streams),
        )
    }
}

private fun ReleaseDto.toAvailability(): Availability = when {
    // Копирайт приоритетнее гео: он абсолютный, гео можно обойти сменой региона.
    isBlockedByCopyrights -> Availability.COPYRIGHT_BLOCKED
    isBlockedByGeo -> Availability.GEO_BLOCKED
    else -> Availability.AVAILABLE
}

private fun GenreDto.toDomainOrNull(): Genre? {
    val genreName = name?.takeIf { it.isNotBlank() } ?: return null
    return Genre(id = id, name = genreName)
}

/**
 * Таймкод считается валидным, только если обе границы заданы и конец позже начала.
 * Иначе кнопка «пропустить опенинг» показывалась бы на мусорных данных.
 */
private fun TimecodeDto?.toTimecodeOrNull(): Timecode? {
    val from = this?.start ?: return null
    val to = this.stop ?: return null
    if (from < 0 || to <= from) return null
    return Timecode(startSec = from, stopSec = to)
}

/** Только реально доступные качества, по убыванию. Пустой список — валидное состояние. */
private fun EpisodeDto.toStreams(): List<Stream> = listOfNotNull(
    hls1080?.takeIf { it.isNotBlank() }?.let { Stream(VideoQuality.P1080, it, StreamContainer.HLS) },
    hls720?.takeIf { it.isNotBlank() }?.let { Stream(VideoQuality.P720, it, StreamContainer.HLS) },
    hls480?.takeIf { it.isNotBlank() }?.let { Stream(VideoQuality.P480, it, StreamContainer.HLS) },
)
