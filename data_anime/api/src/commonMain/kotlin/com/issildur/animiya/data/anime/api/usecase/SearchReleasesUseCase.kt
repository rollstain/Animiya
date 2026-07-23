package com.issildur.animiya.data.anime.api.usecase

import com.issildur.animiya.core.utils.AppResult
import com.issildur.animiya.data.anime.api.AnimeRepository
import com.issildur.animiya.data.anime.api.model.Release

/** Поиск релизов по названию. AniLibria отдаёт голый массив без пагинации. */
class SearchReleasesUseCase(
    private val repository: AnimeRepository,
) {
    suspend operator fun invoke(query: String): AppResult<List<Release>> =
        repository.search(query.trim())
}
