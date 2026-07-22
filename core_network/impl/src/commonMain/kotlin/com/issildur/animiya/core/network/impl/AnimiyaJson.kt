package com.issildur.animiya.core.network.impl

import kotlinx.serialization.json.Json

/**
 * Настройки разбора JSON.
 *
 * Каждая опция здесь — следствие фактического поведения API, проверенного
 * живыми запросами (см. swarm-report/anilibria-api-schema.md):
 *
 * - [Json.ignoreUnknownKeys] — машиночитаемой спеки нет, схема шире документации
 *   и меняется без предупреждения;
 * - [Json.explicitNulls] = false — половина полей приходит null (`episodes_total`,
 *   `average_duration_of_episode`, `name.alternative`, любое из `hls_*`);
 * - [Json.coerceInputValues] — в паре с дефолтами не даёт упасть, если поле-список
 *   или Boolean внезапно придёт null;
 * - [Json.isLenient] — терпимость к нестрогому JSON.
 */
val AnimiyaJson: Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = false
    coerceInputValues = true
    encodeDefaults = false
    allowSpecialFloatingPointValues = true
}
