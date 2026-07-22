package com.issildur.animiya.data.anime.impl.mapper

import com.issildur.animiya.core.network.api.ApiEndpointProvider
import com.issildur.animiya.data.anime.api.model.ImageSet
import com.issildur.animiya.data.anime.impl.dto.ImageSetDto

/**
 * Превращает относительные пути изображений в абсолютные URL.
 *
 * Отдельный класс, а не функция, потому что зависит от текущего эндпоинта:
 * при переезде домена меняется и хост картинок.
 *
 * Предпочитает WebP-версии из `optimized` — они заметно легче, что важно
 * для сетки каталога на мобильном трафике.
 */
class ImageUrlResolver(
    private val endpointProvider: ApiEndpointProvider,
) {

    fun resolve(path: String?): String? {
        val trimmed = path?.trim().orEmpty()
        if (trimmed.isEmpty()) return null
        // Часть полей (например, ссылки на HLS) уже абсолютны — не трогаем.
        if (trimmed.startsWith("http://", ignoreCase = true) ||
            trimmed.startsWith("https://", ignoreCase = true)
        ) {
            return trimmed
        }
        val base = endpointProvider.current().mediaBaseUrl
        return if (trimmed.startsWith('/')) base + trimmed else "$base/$trimmed"
    }

    fun toImageSet(dto: ImageSetDto?): ImageSet {
        if (dto == null) return ImageSet.Empty
        val optimized = dto.optimized
        return ImageSet(
            thumbnail = resolve(optimized?.thumbnail ?: dto.thumbnail),
            preview = resolve(optimized?.preview ?: dto.preview),
            full = resolve(optimized?.src ?: dto.src),
        )
    }
}
