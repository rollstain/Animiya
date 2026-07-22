package com.issildur.animiya.core.network.api

/**
 * Адрес источника данных.
 *
 * [apiBaseUrl] и [mediaBaseUrl] живут вместе намеренно: у AniLibria постеры
 * приходят ОТНОСИТЕЛЬНЫМИ путями (`/storage/releases/posters/...`), и при
 * переезде домена ломаются одновременно и API, и картинки. Держать их
 * раздельно — значит однажды получить рабочий каталог с пустыми постерами.
 */
data class ApiEndpoint(
    val id: String,
    val apiBaseUrl: String,
    val mediaBaseUrl: String,
) {
    init {
        require(apiBaseUrl.endsWith('/')) {
            "apiBaseUrl должен заканчиваться на '/', иначе Ktor затрёт path-сегмент: $apiBaseUrl"
        }
        require(!mediaBaseUrl.endsWith('/')) {
            "mediaBaseUrl не должен заканчиваться на '/', пути к медиа начинаются со слеша: $mediaBaseUrl"
        }
    }
}

/**
 * Поставщик активного эндпоинта.
 *
 * Хардкод базового URL запрещён: за последние два года источник переезжал
 * anilibria.tv -> aniliberty.top -> anilibria.top. Все запросы в коде задают
 * только относительный путь, хост подставляется здесь.
 */
interface ApiEndpointProvider {

    /** Текущий рабочий эндпоинт. */
    fun current(): ApiEndpoint

    /**
     * Пометить [failed] как недоступный и выдать следующий кандидат.
     * Возвращает null, если исправных кандидатов не осталось.
     */
    fun rotate(failed: ApiEndpoint): ApiEndpoint?

    /** Ручное переопределение (debug-меню, QA). null — вернуться к списку по умолчанию. */
    suspend fun setOverride(endpoint: ApiEndpoint?)
}
