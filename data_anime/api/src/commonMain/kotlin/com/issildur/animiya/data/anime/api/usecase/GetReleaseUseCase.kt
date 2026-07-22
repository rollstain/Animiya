package com.issildur.animiya.data.anime.api.usecase

import com.issildur.animiya.core.utils.AppResult
import com.issildur.animiya.data.anime.api.AnimeRepository
import com.issildur.animiya.data.anime.api.model.ReleaseDetails

class GetReleaseUseCase(
    private val repository: AnimeRepository,
) {
    /**
     * [idOrAlias] — API принимает и числовой id, и человекочитаемый alias.
     * Передаём alias, когда он есть: ссылки читабельнее и переживают
     * перенумерацию.
     */
    suspend operator fun invoke(idOrAlias: String): AppResult<ReleaseDetails> =
        repository.getRelease(idOrAlias)
}
