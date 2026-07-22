package com.issildur.animiya.core.network.impl

import io.ktor.client.engine.HttpClientEngineFactory

/** Платформенный движок Ktor: OkHttp на Android, Darwin на iOS. */
expect fun platformHttpClientEngine(): HttpClientEngineFactory<*>
