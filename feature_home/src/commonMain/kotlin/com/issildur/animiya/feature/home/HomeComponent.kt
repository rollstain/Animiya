package com.issildur.animiya.feature.home

import androidx.compose.runtime.Immutable
import com.issildur.animiya.core.ui.ReleasePosterUi
import com.issildur.animiya.core.ui.toPosterUi
import com.issildur.animiya.core.utils.AppError
import com.issildur.animiya.core.utils.AppResult
import com.issildur.animiya.core.utils.getOrNull
import com.issildur.animiya.data.anime.api.model.Release
import com.issildur.animiya.data.anime.api.usecase.GetLatestReleasesUseCase
import com.issildur.animiya.data.anime.api.usecase.GetReleaseCatalogUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Immutable
data class HeroUi(
    val idOrAlias: String,
    val title: String,
    val subtitle: String,
    val backdropUrl: String?,
    val badge: String?,
)

@Immutable
data class HomeRail(
    val title: String,
    val items: List<ReleasePosterUi>,
)

@Immutable
sealed interface HomeContent {
    data object Loading : HomeContent
    data class Error(val error: AppError) : HomeContent
    data class Content(
        val hero: HeroUi?,
        val rails: List<HomeRail>,
    ) : HomeContent
}

interface HomeComponent {
    val state: StateFlow<HomeContent>
    fun onRetry()
}

/**
 * Discovery-главная. Собирает витрину из реальных данных AniLibria: hero из
 * первого свежего релиза, полки «Новые серии» (latest) и «Онгоинги» (каталог).
 *
 * Полки «Продолжить смотреть» пока нет — она появится с историей просмотра
 * (нужен плеер + персистентность). Это честно: показываем то, что есть.
 */
class DefaultHomeComponent(
    private val scope: CoroutineScope,
    private val getLatest: GetLatestReleasesUseCase,
    private val getCatalog: GetReleaseCatalogUseCase,
) : HomeComponent {

    private val _state = MutableStateFlow<HomeContent>(HomeContent.Loading)
    override val state: StateFlow<HomeContent> = _state.asStateFlow()

    init {
        load()
    }

    override fun onRetry() {
        _state.value = HomeContent.Loading
        load()
    }

    private fun load() {
        scope.launch {
            val latestDeferred = async { getLatest() }
            val catalogDeferred = async { getCatalog() }

            val latest = latestDeferred.await()
            val catalog = catalogDeferred.await().getOrNull()?.items.orEmpty()

            // Если и latest, и каталог не отдались — это ошибка. Иначе показываем,
            // что удалось получить.
            if (latest is AppResult.Failure && catalog.isEmpty()) {
                _state.value = HomeContent.Error(latest.error)
                return@launch
            }

            val latestItems: List<Release> = latest.getOrNull().orEmpty()
            val hero = latestItems.firstOrNull()?.let {
                HeroUi(
                    idOrAlias = it.alias ?: it.id.raw.toString(),
                    title = it.title,
                    subtitle = listOfNotNull(it.type, it.season, it.year?.toString())
                        .joinToString(" · "),
                    backdropUrl = it.poster.best(),
                    badge = if (it.isOngoing) "Новый эпизод" else null,
                )
            }

            val rails = buildList {
                val newRail = latestItems.drop(1).map(Release::toPosterUi)
                if (newRail.isNotEmpty()) add(HomeRail(title = "Новые серии", items = newRail))

                val ongoing = catalog.filter { it.isOngoing }.map(Release::toPosterUi)
                if (ongoing.isNotEmpty()) add(HomeRail(title = "Онгоинги сезона", items = ongoing))

                val popular = catalog.map(Release::toPosterUi)
                if (popular.isNotEmpty()) add(HomeRail(title = "Популярное", items = popular))
            }

            _state.value = HomeContent.Content(hero = hero, rails = rails)
        }
    }
}
