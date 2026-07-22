package com.issildur.animiya.core.utils

/**
 * Результат операции, которая может завершиться доменной ошибкой.
 *
 * Осознанно не используем [kotlin.Result]: он не сохраняет тип ошибки в сигнатуре,
 * не сериализуется и плохо ложится на Obj-C interop — а модуль экспортируется в iOS.
 */
sealed interface AppResult<out T> {

    data class Success<out T>(val value: T) : AppResult<T>

    data class Failure(val error: AppError) : AppResult<Nothing>
}

inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(value))
    is AppResult.Failure -> this
}

inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T> = apply {
    if (this is AppResult.Success) action(value)
}

inline fun <T> AppResult<T>.onFailure(action: (AppError) -> Unit): AppResult<T> = apply {
    if (this is AppResult.Failure) action(error)
}

fun <T> AppResult<T>.getOrNull(): T? = (this as? AppResult.Success)?.value

fun <T> AppResult<T>.errorOrNull(): AppError? = (this as? AppResult.Failure)?.error
