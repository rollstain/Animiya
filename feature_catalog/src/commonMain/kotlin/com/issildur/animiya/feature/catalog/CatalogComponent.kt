package com.issildur.animiya.feature.catalog

import com.issildur.animiya.core.ui.toPosterUi
import com.issildur.animiya.core.utils.AppResult
import com.issildur.animiya.data.anime.api.usecase.GetReleaseCatalogUseCase
import com.issildur.animiya.data.anime.api.usecase.SearchReleasesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val SEARCH_DEBOUNCE_MS = 350L

interface CatalogComponent {
    val state: StateFlow<CatalogUiState>
    fun onRetry()
    fun onLoadMore()
    fun onQueryChange(query: String)
}

/**
 * Держатель состояния каталога с поиском.
 *
 * Каталог (с пагинацией) и результаты поиска живут раздельно: при пустом запросе
 * показываем сетку каталога, при непустом — результаты поиска (без пагинации,
 * AniLibria отдаёт плоский массив). Очистка запроса мгновенно возвращает сетку
 * без повторного запроса.
 */
class DefaultCatalogComponent(
    private val scope: CoroutineScope,
    private val getCatalog: GetReleaseCatalogUseCase,
    private val search: SearchReleasesUseCase,
) : CatalogComponent {

    private val _state = MutableStateFlow(CatalogUiState())
    override val state: StateFlow<CatalogUiState> = _state.asStateFlow()

    private var currentPage: Int = GetReleaseCatalogUseCase.FIRST_PAGE
    private var hasNext: Boolean = false

    /** Состояние сетки каталога — отдельно от того, что показано (может быть поиск). */
    private var catalogContent: CatalogContent = CatalogContent.Loading
    private var searchJob: Job? = null

    init {
        loadFirstPage()
    }

    private val isSearching: Boolean get() = _state.value.query.isNotBlank()

    override fun onRetry() {
        if (isSearching) {
            runSearch(_state.value.query)
        } else {
            catalogContent = CatalogContent.Loading
            _state.update { it.copy(content = CatalogContent.Loading, appendError = null) }
            loadFirstPage()
        }
    }

    override fun onQueryChange(query: String) {
        _state.update { it.copy(query = query) }
        searchJob?.cancel()
        if (query.isBlank()) {
            // Мгновенно возвращаем закешированную сетку каталога.
            _state.update { it.copy(content = catalogContent, appendError = null) }
        } else {
            searchJob = scope.launch {
                delay(SEARCH_DEBOUNCE_MS)
                runSearch(query)
            }
        }
    }

    override fun onLoadMore() {
        // Догрузка — только для каталога, не для поиска.
        if (isSearching) return
        val snapshot = _state.value
        if (snapshot.isAppending || !hasNext) return
        if (snapshot.content !is CatalogContent.Items) return

        _state.update { it.copy(isAppending = true, appendError = null) }
        scope.launch {
            when (val result = getCatalog(page = currentPage + 1)) {
                is AppResult.Success -> {
                    currentPage = result.value.page
                    hasNext = result.value.hasNext
                    val appended = result.value.items.map { it.toPosterUi() }
                    val existing = (catalogContent as? CatalogContent.Items)?.releases.orEmpty()
                    catalogContent = CatalogContent.Items(releases = existing + appended, hasNext = hasNext)
                    _state.update { it.copy(content = catalogContent, isAppending = false) }
                }

                is AppResult.Failure -> _state.update {
                    it.copy(isAppending = false, appendError = result.error)
                }
            }
        }
    }

    private fun runSearch(query: String) {
        searchJob = scope.launch {
            _state.update { it.copy(content = CatalogContent.Loading) }
            when (val result = search(query)) {
                is AppResult.Success -> {
                    val items = result.value.map { it.toPosterUi() }
                    _state.update {
                        it.copy(
                            content = if (items.isEmpty()) {
                                CatalogContent.Empty
                            } else {
                                CatalogContent.Items(releases = items, hasNext = false)
                            },
                        )
                    }
                }

                is AppResult.Failure -> _state.update { it.copy(content = CatalogContent.Error(result.error)) }
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
                    catalogContent = if (items.isEmpty()) {
                        CatalogContent.Empty
                    } else {
                        CatalogContent.Items(releases = items, hasNext = hasNext)
                    }
                    // Обновляем показанное, только если пользователь сейчас не в поиске.
                    if (!isSearching) _state.update { it.copy(content = catalogContent) }
                }

                is AppResult.Failure -> {
                    catalogContent = CatalogContent.Error(result.error)
                    if (!isSearching) _state.update { it.copy(content = catalogContent) }
                }
            }
        }
    }
}
