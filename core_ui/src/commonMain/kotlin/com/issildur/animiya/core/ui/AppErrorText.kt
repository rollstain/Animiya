package com.issildur.animiya.core.ui

import com.issildur.animiya.core.utils.AppError

/**
 * Доменная ошибка → текст для пользователя.
 *
 * Живёт в :core_ui, а не в :uikit (design-система не знает про домен) и не в
 * каждой фиче (было дублировано в feature_catalog и feature_release).
 */
fun AppError.toReadableMessage(): String = when (this) {
    is AppError.NoConnection -> "Нет соединения с интернетом"
    AppError.Timeout -> "Сервер не ответил вовремя"
    is AppError.ServerError -> "Сервер недоступен, попробуйте позже"
    AppError.AllEndpointsUnavailable -> "Все известные адреса недоступны"
    is AppError.RateLimited -> "Слишком много запросов, подождите немного"
    AppError.NotFound -> "Не найдено"
    is AppError.ClientError -> "Запрос отклонён сервером"
    is AppError.ParseError -> "Не удалось обработать ответ сервера"
    is AppError.Unknown -> "Что-то пошло не так"
}
