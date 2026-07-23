package com.issildur.animiya.composeapp.di

import com.issildur.animiya.core.network.api.ApiEndpointProvider
import com.issildur.animiya.core.network.api.PlatformInfo
import com.issildur.animiya.core.network.impl.DefaultApiEndpointProvider
import com.issildur.animiya.core.network.impl.buildApiHttpClient
import com.issildur.animiya.core.network.impl.buildImageHttpClient
import com.issildur.animiya.data.anime.impl.di.ApiHttpClient
import com.issildur.animiya.data.anime.impl.di.ImageHttpClient
import com.issildur.animiya.data.anime.impl.di.dataAnimeModule
import com.issildur.animiya.data.anime.impl.di.platformDatabaseModule
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.Logger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Флаг отладки приходит извне (из androidApp / iOS-хоста), а не из BuildConfig:
 * BuildConfig в KMP-модуле с новым AGP-плагином не генерируется.
 */
class AppConfig(val isDebug: Boolean)

private val networkModule = module {
    single<ApiEndpointProvider> { DefaultApiEndpointProvider() }

    single<Logger> {
        object : Logger {
            override fun log(message: String) {
                // HLS-ссылки содержат гео- и рекламные query-параметры,
                // поэтому уровень логирования выше HEADERS не поднимаем.
                println("[Ktor] $message")
            }
        }
    }

    single<HttpClient>(ApiHttpClient) {
        buildApiHttpClient(
            platformInfo = get(),
            logger = get(),
            isDebug = get<AppConfig>().isDebug,
        )
    }

    single<HttpClient>(ImageHttpClient) {
        buildImageHttpClient(platformInfo = get())
    }
}

/** Платформенная часть: [PlatformInfo] и всё, что требует системного контекста. */
expect val platformModule: Module

fun appModules(isDebug: Boolean): List<Module> = listOf(
    module { single { AppConfig(isDebug = isDebug) } },
    platformModule,
    platformDatabaseModule(),
    networkModule,
    dataAnimeModule,
)

fun initKoin(
    isDebug: Boolean,
    appDeclaration: KoinAppDeclaration = {},
): KoinApplication = startKoin {
    appDeclaration()
    modules(appModules(isDebug = isDebug))
}
