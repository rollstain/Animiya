package com.issildur.animiya.composeapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.issildur.animiya.data.anime.impl.di.ImageHttpClient
import com.issildur.animiya.feature.catalog.CatalogScreen
import com.issildur.animiya.feature.release.ReleaseDetailsScreen
import io.ktor.client.HttpClient
import org.koin.compose.getKoin

@OptIn(ExperimentalMaterial3Api::class)
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
        // Навигация на два экрана держится одним saveable-полем: null — каталог,
        // непустое — детали. Полноценная навигационная библиотека появится,
        // когда экранов станет больше и понадобится глубокий стек и диплинки;
        // сейчас она была бы механикой ради механики.
        var openedRelease: String? by rememberSaveable { mutableStateOf(null) }

        PlatformBackHandler(enabled = openedRelease != null) { openedRelease = null }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = if (openedRelease == null) "Каталог" else "Релиз") },
                    navigationIcon = {
                        if (openedRelease != null) {
                            IconButton(onClick = { openedRelease = null }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Назад",
                                )
                            }
                        }
                    },
                )
            },
        ) { innerPadding ->
            val contentModifier = Modifier.padding(innerPadding)
            val current = openedRelease
            if (current == null) {
                CatalogScreen(
                    onReleaseClick = { idOrAlias -> openedRelease = idOrAlias },
                    modifier = contentModifier,
                )
            } else {
                ReleaseDetailsScreen(idOrAlias = current, modifier = contentModifier)
            }
        }
    }
}

@Composable
fun AnimiyaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
        content = content,
    )
}
