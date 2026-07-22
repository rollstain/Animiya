package com.issildur.animiya.composeapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.issildur.animiya.data.anime.impl.di.ImageHttpClient
import com.issildur.animiya.feature.catalog.CatalogScreen
import io.ktor.client.HttpClient
import org.koin.compose.getKoin

@Composable
fun App() {
    val koin = getKoin()

    // Картинки ходят через ОТДЕЛЬНЫЙ HttpClient: у него нет ContentNegotiation,
    // логирования и retry-политики API — иначе логи забились бы постерами,
    // а ретраи удвоили бы трафик изображений.
    setSingletonImageLoaderFactory { context: PlatformContext ->
        ImageLoader.Builder(context)
            .components {
                add(
                    KtorNetworkFetcherFactory(
                        httpClient = { koin.get<HttpClient>(ImageHttpClient) },
                    ),
                )
            }
            .crossfade(true)
            .build()
    }

    AnimiyaTheme {
        // Навигация появится вместе со вторым экраном: тащить навигационную
        // библиотеку ради единственного destination преждевременно.
        CatalogScreen()
    }
}

@Composable
fun AnimiyaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
        content = content,
    )
}
