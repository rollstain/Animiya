package com.issildur.animiya.data.anime.api.usecase

import com.issildur.animiya.core.utils.AppResult
import com.issildur.animiya.data.anime.api.AnimeRepository
import com.issildur.animiya.data.anime.api.model.Release

/** Свежие релизы — для hero и полки «Новые серии» на главной. */
class GetLatestReleasesUseCase(
    private val repository: AnimeRepository,
) {
    suspend operator fun invoke(limit: Int = DEFAULT_LIMIT): AppResult<List<Release>> =
        repository.getLatest(limit = limit)

    companion object {
        const val DEFAULT_LIMIT = 15
    }
}
