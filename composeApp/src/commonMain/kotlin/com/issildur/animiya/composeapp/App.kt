package com.issildur.animiya.composeapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.issildur.animiya.feature.home.HomeScreen
import com.issildur.animiya.feature.release.ReleaseDetailsScreen
import com.issildur.animiya.uikit.component.EmptyState
import com.issildur.animiya.uikit.theme.AnimiyaTheme
import io.ktor.client.HttpClient
import org.koin.compose.getKoin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val koin = getKoin()

    // Картинки ходят через ОТДЕЛЬНЫЙ HttpClient: у него нет ContentNegotiation,
    // логирования и retry-политики API.
    setSingletonImageLoaderFactory { context: PlatformContext ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory(httpClient = { koin.get<HttpClient>(ImageHttpClient) }))
            }
            .crossfade(true)
            .build()
    }

    AnimiyaTheme {
        var selectedTab: AppTab by rememberSaveable { mutableStateOf(AppTab.Home) }
        // Детали релиза открываются оверлеем поверх любой вкладки.
        var openedRelease: String? by rememberSaveable { mutableStateOf(null) }

        PlatformBackHandler(enabled = openedRelease != null) { openedRelease = null }

        val current = openedRelease
        if (current != null) {
            ReleaseOverlay(idOrAlias = current, onBack = { openedRelease = null })
        } else {
            MainScaffold(
                selectedTab = selectedTab,
                onSelectTab = { selectedTab = it },
                onReleaseClick = { openedRelease = it },
            )
        }
    }
}

@Composable
private fun MainScaffold(
    selectedTab: AppTab,
    onSelectTab: (AppTab) -> Unit,
    onReleaseClick: (String) -> Unit,
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                AppTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { onSelectTab(tab) },
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                        label = { Text(text = tab.label) },
                        colors = NavigationBarItemDefaults.colors(),
                    )
                }
            }
        },
    ) { innerPadding ->
        val contentModifier = Modifier.fillMaxSize().padding(innerPadding)
        when (selectedTab) {
            AppTab.Home -> HomeScreen(onReleaseClick = onReleaseClick, modifier = contentModifier)
            AppTab.Catalog -> CatalogScreen(onReleaseClick = onReleaseClick, modifier = contentModifier)
            AppTab.My -> PlaceholderTab(text = "Списки и загрузки появятся здесь", modifier = contentModifier)
            AppTab.Profile -> PlaceholderTab(text = "Профиль и настройки появятся здесь", modifier = contentModifier)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReleaseOverlay(idOrAlias: String, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Релиз") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        ReleaseDetailsScreen(
            idOrAlias = idOrAlias,
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        )
    }
}

@Composable
private fun PlaceholderTab(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        EmptyState(text = text)
    }
}
