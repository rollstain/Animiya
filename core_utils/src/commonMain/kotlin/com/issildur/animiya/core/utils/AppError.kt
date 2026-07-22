package com.issildur.animiya.core.utils

/**
 * Доменная ошибка. Транспортные исключения конвертируются в неё ровно в одном месте —
 * в реализации DataSource. Выше по стеку (Repository / UseCase / UI) сетевых
 * исключений быть не должно.
 *
 * Важно: гео- и копирайт-блокировки релиза сюда НЕ относятся — они приходят
 * в успешном ответе и являются доменным состоянием, а не ошибкой транспорта.
 */
sealed interface AppError {

    /** Нет сети, DNS не резолвится, соединение оборвалось. */
    data class NoConnection(val detail: String? = null) : AppError

    /** Истёк таймаут запроса. */
    data object Timeout : AppError

    /** 5xx на стороне источника. У AniLibria это происходит регулярно. */
    data class ServerError(val code: Int) : AppError

    /**
     * 429. AniLibria не документирует лимиты и не отдаёт RateLimit-заголовки,
     * поэтому [retryAfterSeconds] может отсутствовать.
     */
    data class RateLimited(val retryAfterSeconds: Long? = null) : AppError

    /** 404. */
    data object NotFound : AppError

    /** Прочие 4xx. Отдельно стоит следить за 403 — это может быть Cloudflare. */
    data class ClientError(val code: Int) : AppError

    /** Ответ не разобрался. Схема API задокументирована не полностью. */
    data class ParseError(val detail: String? = null) : AppError

    /** Перебрали все известные эндпоинты, живого не нашлось. */
    data object AllEndpointsUnavailable : AppError

    data class Unknown(val detail: String? = null) : AppError
}
