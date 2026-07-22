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
    val duration: String?,
    val hasVideo: Boolean,
    val qualities: String,
    val skipHint: String?,
)

@Immutable
sealed interface ReleaseDetailsContent {
    data object Loading : ReleaseDetailsContent
    data class Error(val error: AppError) : ReleaseDetailsContent
    data class Content(
        val title: String,
        val subtitle: String,
        val description: String?,
        val posterUrl: String?,
        val genres: String,
        val blockedReason: String?,
        val episodes: List<EpisodeUiModel>,
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
    return ReleaseDetailsContent.Content(
        title = r.title,
        subtitle = listOfNotNull(
            r.year?.toString(),
            r.season,
            r.type,
            r.ageRatingLabel,
            episodes.size.takeIf { it > 0 }?.let { "$it эп." },
        ).joinToString(" · "),
        description = r.description,
        posterUrl = r.poster.best(),
        genres = r.genres.joinToString(", ") { it.name },
        blockedReason = when (r.availability) {
            Availability.COPYRIGHT_BLOCKED -> "Заблокировано правообладателем"
            Availability.GEO_BLOCKED -> "Недоступно в вашем регионе"
            Availability.AVAILABLE -> null
        },
        episodes = episodes.map { it.toUiModel() },
    )
}

private fun Episode.toUiModel(): EpisodeUiModel = EpisodeUiModel(
    id = id.raw,
    title = buildString {
        val number = ordinal
        if (number != null) {
            // Целые номера показываем без дробной части: «7», а не «7.0».
            append(if (number % 1.0 == 0.0) number.toInt().toString() else number.toString())
            append(". ")
        }
        append(title ?: "Без названия")
    },
    duration = durationSec?.let { total ->
        val minutes = total / 60
        val seconds = total % 60
        "$minutes:${seconds.toString().padStart(2, '0')}"
    },
    hasVideo = hasVideo,
    qualities = streams.joinToString(" / ") { "${it.quality.height}p" },
    skipHint = opening?.let { "Опенинг ${it.startSec}–${it.stopSec} с" },
)
