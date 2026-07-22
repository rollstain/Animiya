package com.issildur.animiya.data.anime.api.model

/**
 * Страница выдачи.
 *
 * Заметка по источнику: у AniLibria только каталог отдаёт конверт с
 * `meta.pagination`. Поиск, latest, жанры и расписание возвращают голый массив
 * без метаданных — для них [totalPages] будет null, а [hasNext] считается
 * по факту заполненности страницы.
 */
data class Page<out T>(
    val items: List<T>,
    val page: Int,
    val totalPages: Int?,
    val hasNext: Boolean,
) {
    val isEmpty: Boolean get() = items.isEmpty()

    companion object {
        fun <T> single(items: List<T>): Page<T> = Page(
            items = items,
            page = 1,
            totalPages = 1,
            hasNext = false,
        )
    }
}
