package com.issildur.animiya.data.anime.impl.cache

import com.issildur.animiya.data.anime.api.model.Availability
import com.issildur.animiya.data.anime.api.model.Genre
import com.issildur.animiya.data.anime.api.model.ImageSet
import com.issildur.animiya.data.anime.api.model.Release
import com.issildur.animiya.data.anime.api.model.ReleaseId
import kotlinx.serialization.Serializable

/**
 * Сериализуемый снимок [Release] для кеша. Отдельная модель, чтобы держать
 * доменные модели чистыми от аннотаций персистентности.
 */
@Serializable
data class CachedRelease(
    val id: Int,
    val alias: String?,
    val title: String,
    val titleEnglish: String?,
    val year: Int?,
    val season: String?,
    val type: String?,
    val posterThumb: String?,
    val posterPreview: String?,
    val posterFull: String?,
    val isOngoing: Boolean,
    val episodesTotal: Int?,
    val ageRatingLabel: String?,
    val isAdult: Boolean,
    val availability: String,
    val genres: List<CachedGenre>,
    val description: String?,
    val publishWeekday: Int?,
    val inListsCount: Int?,
)

@Serializable
data class CachedGenre(val id: Int, val name: String)

fun Release.toCached(): CachedRelease = CachedRelease(
    id = id.raw,
    alias = alias,
    title = title,
    titleEnglish = titleEnglish,
    year = year,
    season = season,
    type = type,
    posterThumb = poster.thumbnail,
    posterPreview = poster.preview,
    posterFull = poster.full,
    isOngoing = isOngoing,
    episodesTotal = episodesTotal,
    ageRatingLabel = ageRatingLabel,
    isAdult = isAdult,
    availability = availability.name,
    genres = genres.map { CachedGenre(it.id, it.name) },
    description = description,
    publishWeekday = publishWeekday,
    inListsCount = inListsCount,
)

fun CachedRelease.toDomain(): Release = Release(
    id = ReleaseId(id),
    alias = alias,
    title = title,
    titleEnglish = titleEnglish,
    year = year,
    season = season,
    type = type,
    poster = ImageSet(thumbnail = posterThumb, preview = posterPreview, full = posterFull),
    isOngoing = isOngoing,
    episodesTotal = episodesTotal,
    ageRatingLabel = ageRatingLabel,
    isAdult = isAdult,
    availability = runCatching { Availability.valueOf(availability) }.getOrDefault(Availability.AVAILABLE),
    genres = genres.map { Genre(it.id, it.name) },
    description = description,
    publishWeekday = publishWeekday,
    inListsCount = inListsCount,
)
