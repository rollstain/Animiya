package com.issildur.animiya.data.anime.impl.di

import com.issildur.animiya.data.anime.api.AnimeRemoteDataSource
import com.issildur.animiya.data.anime.api.AnimeRepository
import com.issildur.animiya.data.anime.api.usecase.GetReleaseCatalogUseCase
import com.issildur.animiya.data.anime.api.usecase.GetReleaseUseCase
import com.issildur.animiya.data.anime.impl.AnilibriaRemoteDataSource
import com.issildur.animiya.data.anime.impl.AnimeRepositoryImpl
import com.issildur.animiya.data.anime.impl.mapper.ImageUrlResolver
import com.issildur.animiya.data.anime.impl.mapper.ReleaseMapper
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

/** Квалификатор клиента для JSON API (в отличие от клиента для картинок). */
val ApiHttpClient = named("api-http-client")

/** Квалификатор клиента для загрузки изображений. */
val ImageHttpClient = named("image-http-client")

val dataAnimeModule = module {
    single { ImageUrlResolver(endpointProvider = get()) }
    single { ReleaseMapper(urls = get()) }

    single<AnimeRemoteDataSource> {
        AnilibriaRemoteDataSource(
            client = get<HttpClient>(ApiHttpClient),
            endpointProvider = get(),
            mapper = get(),
        )
    }

    single<AnimeRepository> { AnimeRepositoryImpl(remote = get()) }

    factory { GetReleaseCatalogUseCase(repository = get()) }
    factory { GetReleaseUseCase(repository = get()) }
}
