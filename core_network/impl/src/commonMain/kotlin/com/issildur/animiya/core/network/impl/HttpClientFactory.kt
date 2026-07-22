package com.issildur.animiya.core.network.impl

import com.issildur.animiya.core.network.api.PlatformInfo
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json

private const val REQUEST_TIMEOUT_MS = 20_000L
private const val CONNECT_TIMEOUT_MS = 10_000L
private const val SOCKET_TIMEOUT_MS = 20_000L

private const val MAX_RETRIES = 3
private const val RETRY_BASE_DELAY_MS = 500L
private const val RETRY_MAX_DELAY_MS = 8_000L

/**
 * Клиент для обращения к JSON API.
 *
 * Осознанные решения:
 * - Дисковый HTTP-кеш не включаем: API отдаёт `Cache-Control: no-cache, private`
 *   (проверено), кешировать нечего. Кеш живёт на уровне приложения (SQLDelight).
 * - [UserAgent] ставим отдельным плагином, а не заголовком в [DefaultRequest]:
 *   OkHttp и Darwin подставляют собственный UA, его нужно перебить явно.
 * - Ретраим только 5xx/429 и сетевые сбои. 4xx не ретраим — это не починится.
 */
fun buildApiHttpClient(
    platformInfo: PlatformInfo,
    logger: Logger,
    isDebug: Boolean,
): HttpClient = HttpClient(platformHttpClientEngine()) {

    expectSuccess = true

    install(ContentNegotiation) {
        json(AnimiyaJson)
    }

    install(HttpTimeout) {
        requestTimeoutMillis = REQUEST_TIMEOUT_MS
        connectTimeoutMillis = CONNECT_TIMEOUT_MS
        socketTimeoutMillis = SOCKET_TIMEOUT_MS
    }

    install(UserAgent) {
        agent = platformInfo.userAgent
    }

    install(HttpRequestRetry) {
        maxRetries = MAX_RETRIES
        retryIf { _, response ->
            response.status.value in 500..599 || response.status.value == 429
        }
        retryOnExceptionIf { _, cause -> cause.isTransientNetworkFailure() }
        // Джиттер обязателен: без него при массовом 5xx все клиенты
        // синхронно бьют по серверу одной волной.
        exponentialDelay(
            base = 2.0,
            maxDelayMs = RETRY_MAX_DELAY_MS,
            randomizationMs = 250,
        )
    }

    install(Logging) {
        this.logger = logger
        level = if (isDebug) LogLevel.HEADERS else LogLevel.NONE
        sanitizeHeader { header ->
            header.equals(HttpHeaders.Authorization, ignoreCase = true) ||
                header.equals(HttpHeaders.Cookie, ignoreCase = true) ||
                header.equals(HttpHeaders.SetCookie, ignoreCase = true)
        }
    }

    install(DefaultRequest) {
        accept(ContentType.Application.Json)
    }
}

/**
 * Отдельный клиент для картинок.
 *
 * Намеренно НЕ переиспользуем API-клиент: иначе логи забьются загрузкой постеров,
 * а retry-политика удвоит трафик изображений. Плюс постерам не нужен
 * ContentNegotiation.
 */
fun buildImageHttpClient(
    platformInfo: PlatformInfo,
): HttpClient = HttpClient(platformHttpClientEngine()) {
    install(HttpTimeout) {
        requestTimeoutMillis = REQUEST_TIMEOUT_MS
        connectTimeoutMillis = CONNECT_TIMEOUT_MS
    }
    install(UserAgent) {
        agent = platformInfo.userAgent
    }
}
