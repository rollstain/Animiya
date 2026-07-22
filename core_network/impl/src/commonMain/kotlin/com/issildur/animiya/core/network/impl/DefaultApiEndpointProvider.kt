package com.issildur.animiya.core.network.impl

import com.issildur.animiya.core.network.api.ApiEndpoint
import com.issildur.animiya.core.network.api.ApiEndpointProvider
import kotlin.concurrent.Volatile

/** Известные адреса источника. Оба проверены живыми запросами 22.07.2026. */
object AnilibriaEndpoints {
    val Default: List<ApiEndpoint> = listOf(
        ApiEndpoint(
            id = "anilibria-top",
            apiBaseUrl = "https://anilibria.top/api/v1/",
            mediaBaseUrl = "https://anilibria.top",
        ),
        ApiEndpoint(
            id = "api-anilibria-app",
            apiBaseUrl = "https://api.anilibria.app/api/v1/",
            mediaBaseUrl = "https://api.anilibria.app",
        ),
    )
}

/**
 * Провайдер эндпоинтов с последовательным перебором.
 *
 * Выбор «липкий»: переключившись на рабочее зеркало, остаёмся на нём, а не
 * проверяем основной хост на каждом запросе.
 *
 * Состояние — один [Volatile] Int, разделяемых коллекций нет. Гонка возможна
 * только между двумя одновременными отказами и приводит максимум к лишней
 * ротации, что безопасно.
 */
class DefaultApiEndpointProvider(
    private val endpoints: List<ApiEndpoint> = AnilibriaEndpoints.Default,
) : ApiEndpointProvider {

    init {
        require(endpoints.isNotEmpty()) { "Список эндпоинтов не может быть пустым" }
    }

    @Volatile
    private var activeIndex: Int = 0

    @Volatile
    private var override: ApiEndpoint? = null

    override fun current(): ApiEndpoint = override ?: endpoints[activeIndex]

    override fun rotate(failed: ApiEndpoint): ApiEndpoint? {
        // Ручное переопределение не ротируем: адрес задан осознанно.
        if (override != null) return null

        val failedIndex = endpoints.indexOfFirst { it.id == failed.id }
        // Упал не тот эндпоинт, что сейчас активен — значит кто-то уже переключил.
        if (failedIndex < 0) return current()

        val nextIndex = failedIndex + 1
        if (nextIndex >= endpoints.size) {
            // Круг замкнулся: живых кандидатов нет. Сбрасываем на начало,
            // чтобы следующая попытка пользователя начала перебор заново.
            activeIndex = 0
            return null
        }
        activeIndex = nextIndex
        return endpoints[nextIndex]
    }

    override suspend fun setOverride(endpoint: ApiEndpoint?) {
        override = endpoint
        if (endpoint == null) activeIndex = 0
    }
}
