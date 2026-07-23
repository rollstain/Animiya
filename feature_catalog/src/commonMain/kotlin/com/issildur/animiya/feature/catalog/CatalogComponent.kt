package com.issildur.animiya.feature.catalog

import com.issildur.animiya.core.ui.toPosterUi
import com.issildur.animiya.core.utils.AppResult
import com.issildur.animiya.data.anime.api.usecase.GetReleaseCatalogUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface CatalogComponent {
    val state: StateFlow<CatalogUiState>
    fun onRetry()
    fun onLoadMore()
}

/**
 * Держатель состояния экрана каталога.
 *
 * View не знает про домен: маппинг Release -> ReleasePosterUi (общий, из :core_ui)
 * происходит здесь.
 */
class DefaultCatalogComponent(
    private val scope: CoroutineScope,
    private val getCatalog: GetReleaseCatalogUseCase,
) : CatalogComponent {

    private val _state = MutableStateFlow(CatalogUiState())
    override val state: StateFlow<CatalogUiState> = _state.asStateFlow()

    private var currentPage: Int = GetReleaseCatalogUseCase.FIRST_PAGE
    private var hasNext: Boolean = false

    init {
        loadFirstPage()
    }

    override fun onRetry() {
        _state.update { it.copy(content = CatalogContent.Loading, appendError = null) }
        loadFirstPage()
    }

    override fun onLoadMore() {
        val snapshot = _state.value
        // Защита от повторного запуска на быстрой прокрутке.
        if (snapshot.isAppending || !hasNext) return
        if (snapshot.content !is CatalogContent.Items) return

        _state.update { it.copy(isAppending = true, appendError = null) }
        scope.launch {
            when (val result = getCatalog(page = currentPage + 1)) {
                is AppResult.Success -> {
                    currentPage = result.value.page
                    hasNext = result.value.hasNext
                    val appended = result.value.items.map { it.toPosterUi() }
                    _state.update { previous ->
                        val existing = (previous.content as? CatalogContent.Items)?.releases.orEmpty()
                        previous.copy(
                            content = CatalogContent.Items(
                                releases = existing + appended,
                                hasNext = hasNext,
                            ),
                            isAppending = false,
                        )
                    }
                }

                is AppResult.Failure -> _state.update {
                    it.copy(isAppending = false, appendError = result.error)
                }
            }
        }
    }

    private fun loadFirstPage() {
        scope.launch {
            when (val result = getCatalog(page = GetReleaseCatalogUseCase.FIRST_PAGE)) {
                is AppResult.Success -> {
                    currentPage = result.value.page
                    hasNext = result.value.hasNext
                    val items = result.value.items.map { it.toPosterUi() }
                    _state.update {
                        it.copy(
                            content = if (items.isEmpty()) {
                                CatalogContent.Empty
                            } else {
                                CatalogContent.Items(releases = items, hasNext = hasNext)
                            },
                        )
                    }
                }

                is AppResult.Failure -> _state.update {
                    it.copy(content = CatalogContent.Error(result.error))
                }
            }
        }
    }
}
