package com.issildur.animiya.core.network.impl

import com.issildur.animiya.core.utils.AppError
import com.issildur.animiya.core.utils.AppResult
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpHeaders
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

/** Сбой, который имеет смысл повторить. */
internal fun Throwable.isTransientNetworkFailure(): Boolean =
    this is IOException || this is HttpRequestTimeoutException

/**
 * Единственная точка превращения транспортных исключений в [AppError].
 *
 * Выше по стеку — в Repository, UseCase и UI — сетевых исключений быть не должно:
 * там работают только с [AppResult]. Это позволяет не обмазывать try/catch
 * каждый слой.
 *
 * [CancellationException] пробрасывается намеренно: проглотить её — значит
 * сломать отмену корутин.
 */
internal inline fun <T> safeApiCall(block: () -> T): AppResult<T> = try {
    AppResult.Success(block())
} catch (cancellation: CancellationException) {
    throw cancellation
} catch (throwable: Throwable) {
    AppResult.Failure(throwable.toAppError())
}

fun Throwable.toAppError(): AppError = when (this) {
    is HttpRequestTimeoutException -> AppError.Timeout

    is ClientRequestException -> when (val code = response.status.value) {
        404 -> AppError.NotFound
        429 -> AppError.RateLimited(
            retryAfterSeconds = response.headers[HttpHeaders.RetryAfter]?.toLongOrNull(),
        )
        else -> AppError.ClientError(code)
    }

    is ServerResponseException -> AppError.ServerError(response.status.value)

    is JsonConvertException -> AppError.ParseError(message)
    is SerializationException -> AppError.ParseError(message)

    is IOException -> AppError.NoConnection(message)

    else -> AppError.Unknown(message)
}
