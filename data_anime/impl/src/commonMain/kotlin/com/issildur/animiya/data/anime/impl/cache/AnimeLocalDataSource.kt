package com.issildur.animiya.data.anime.impl.cache

import com.issildur.animiya.data.anime.api.model.Release
import com.issildur.animiya.data.anime.impl.db.AnimiyaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Локальный кеш каталога на SQLDelight. Хранит страницу как JSON-снимок
 * [CachedRelease]. Используется репозиторием как fallback при сбое сети —
 * API отдаёт `Cache-Control: no-cache`, а 500/504 у AniLibria не редкость.
 *
 * TTL пока не enforced (savedAt пишется, но не проверяется) — кеш срабатывает
 * только при ошибке сети, так что риск устаревания ограничен. Проверку TTL
 * добавим вместе с часами.
 */
class AnimeLocalDataSource(
    private val database: AnimiyaDatabase,
    private val json: Json,
) {
    private val queries get() = database.catalogQueries

    suspend fun saveCatalogPage(page: Int, releases: List<Release>) = withContext(Dispatchers.Default) {
        val payload = json.encodeToString(releases.map { it.toCached() })
        queries.upsertPage(page = page.toLong(), payload = payload, savedAt = 0L)
    }

    suspend fun getCatalogPage(page: Int): List<Release>? = withContext(Dispatchers.Default) {
        val row = queries.selectPage(page.toLong()).executeAsOneOrNull() ?: return@withContext null
        runCatching {
            json.decodeFromString<List<CachedRelease>>(row.payload).map { it.toDomain() }
        }.getOrNull()
    }
}
