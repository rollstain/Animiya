package com.issildur.animiya.core.ui

import androidx.compose.runtime.Immutable
import com.issildur.animiya.data.anime.api.model.Availability
import com.issildur.animiya.data.anime.api.model.Release
import com.issildur.animiya.data.anime.api.model.ReleaseId

/**
 * Готовая к отрисовке модель постер-карточки. Едина для каталога и полок главной —
 * маппинг [Release] → это одно место, а не копия в каждой фиче.
 */
@Immutable
data class ReleasePosterUi(
    val id: ReleaseId,
    /** Что передавать в запрос деталей: alias, если есть, иначе числовой id. */
    val idOrAlias: String,
    val title: String,
    /** «2026 · Зима · TV» — собрано заранее, null-поля пропущены. */
    val subtitle: String,
    val posterUrl: String?,
    val isOngoing: Boolean,
    val blockedReason: String?,
)

fun Release.toPosterUi(): ReleasePosterUi = ReleasePosterUi(
    id = id,
    idOrAlias = alias ?: id.raw.toString(),
    title = title,
    subtitle = listOfNotNull(year?.toString(), season, type).joinToString(separator = " · "),
    posterUrl = poster.forGrid(),
    isOngoing = isOngoing,
    blockedReason = when (availability) {
        Availability.AVAILABLE -> null
        Availability.GEO_BLOCKED -> "Недоступно в вашем регионе"
        Availability.COPYRIGHT_BLOCKED -> "Заблокировано правообладателем"
    },
)
