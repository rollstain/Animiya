package com.issildur.animiya.data.anime.api

import com.issildur.animiya.core.utils.AppResult
import com.issildur.animiya.data.anime.api.model.Page
import com.issildur.animiya.data.anime.api.model.Release
import com.issildur.animiya.data.anime.api.model.ReleaseDetails

interface AnimeRepository {

    suspend fun loadCatalogPage(page: Int, forceRefresh: Boolean = false): AppResult<Page<Release>>

    suspend fun getRelease(idOrAlias: String): AppResult<ReleaseDetails>

    suspend fun search(query: String): AppResult<List<Release>>
}
