package com.issildur.animiya.feature.release

import androidx.compose.runtime.Immutable
import com.issildur.animiya.core.utils.AppError
import com.issildur.animiya.core.utils.AppResult
import com.issildur.animiya.data.anime.api.model.Availability
import com.issildur.animiya.data.anime.api.model.Episode
import com.issildur.animiya.data.anime.api.model.ReleaseDetails
import com.issildur.animiya.data.anime.api.usecase.GetReleaseUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Immutable
data class EpisodeUiModel(
    val id: String,
    val title: String,
    val meta: String,
    val thumbnailUrl: String?,
    val hasVideo: Boolean,
)

@Immutable
sealed interface ReleaseDetailsContent {
    data object Loading : ReleaseDetailsContent
    data class Error(val error: AppError) : ReleaseDetailsContent
    data class Content(
        val title: String,
        val originalTitle: String?,
        /** «2026 · Зима · TV · 16+ · 12 эп». */
        val meta: String,
        val inListsLabel: String?,
        val backdropUrl: String?,
        val isOngoing: Boolean,
        val blockedReason: String?,
        /** Студия текущей озвучки (пока одна — AniLibria). */
        val dubStudio: String,
        /** «Русская озвучка · 12 серий · до 1080p». */
        val dubMeta: String,
        val description: String?,
        val genres: List<String>,
        val episodes: List<EpisodeUiModel>,
        val hasAnyVideo: Boolean,
    ) : ReleaseDetailsContent
}

interface ReleaseDetailsComponent {
    val state: StateFlow<ReleaseDetailsContent>
    fun onRetry()
}

class DefaultReleaseDetailsComponent(
    private val scope: CoroutineScope,
    private val idOrAlias: String,
    private val getRelease: GetReleaseUseCase,
) : ReleaseDetailsComponent {

    private val _state = MutableStateFlow<ReleaseDetailsContent>(ReleaseDetailsContent.Loading)
    override val state: StateFlow<ReleaseDetailsContent> = _state.asStateFlow()

    init {
        load()
    }

    override fun onRetry() {
        _state.value = ReleaseDetailsContent.Loading
        load()
    }

    private fun load() {
        scope.launch {
            _state.value = when (val result = getRelease(idOrAlias)) {
                is AppResult.Success -> result.value.toContent()
                is AppResult.Failure -> ReleaseDetailsContent.Error(result.error)
            }
        }
    }
}

private fun ReleaseDetails.toContent(): ReleaseDetailsContent.Content {
    val r = release
    val maxQuality = episodes.flatMap { it.streams }.maxOfOrNull { it.quality.height }
    val dubMeta = listOfNotNull(
        "Русская озвучка",
        episodes.size.takeIf { it > 0 }?.let { "$it серий" },
        maxQuality?.let { "до ${it}p" },
    ).joinToString(" · ")

    return ReleaseDetailsContent.Content(
        title = r.title,
        originalTitle = r.titleEnglish,
        meta = listOfNotNull(
            r.year?.toString(),
            r.season,
            r.type,
            r.ageRatingLabel,
            episodes.size.takeIf { it > 0 }?.let { "$it эп" },
        ).joinToString(" · "),
        inListsLabel = r.inListsCount?.let { "${it.grouped()} в списках" },
        backdropUrl = r.poster.best(),
        isOngoing = r.isOngoing,
        blockedReason = when (r.availability) {
            Availability.COPYRIGHT_BLOCKED -> "Заблокировано правообладателем"
            Availability.GEO_BLOCKED -> "Недоступно в вашем регионе"
            Availability.AVAILABLE -> null
        },
        dubStudio = "AniLibria",
        dubMeta = dubMeta,
        description = r.description,
        genres = r.genres.map { it.name },
        episodes = episodes.map { it.toUiModel() },
        hasAnyVideo = episodes.any { it.hasVideo },
    )
}

private fun Episode.toUiModel(): EpisodeUiModel = EpisodeUiModel(
    id = id.raw,
    title = buildString {
        val number = ordinal
        if (number != null) {
            // Целые номера — без дробной части: «7», а не «7.0».
            append(if (number % 1.0 == 0.0) number.toInt().toString() else number.toString())
            append(". ")
        }
        append(title ?: "Серия")
    },
    meta = listOfNotNull(
        durationSec?.let { "${it / 60} мин" },
        streams.firstOrNull()?.let { "до ${streams.maxOf { s -> s.quality.height }}p" },
        opening?.let { "опенинг ${it.startSec}–${it.stopSec}с" },
    ).joinToString(" · "),
    thumbnailUrl = preview.best(),
    hasVideo = hasVideo,
)

/** Простая группировка тысяч пробелом: 24189 → «24 189». */
private fun Int.grouped(): String {
    val s = toString()
    val sb = StringBuilder()
    for ((i, c) in s.withIndex()) {
        if (i > 0 && (s.length - i) % 3 == 0) sb.append(' ')
        sb.append(c)
    }
    return sb.toString()
}
