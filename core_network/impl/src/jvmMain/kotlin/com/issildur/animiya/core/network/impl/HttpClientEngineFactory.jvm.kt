package com.issildur.animiya.core.network.impl

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

/**
 * JVM-таргет существует ради запуска тестов на хосте: iOS-тесты на Windows
 * не гоняются, а Android-тесты требуют устройства.
 */
actual fun platformHttpClientEngine(): HttpClientEngineFactory<*> = OkHttp
