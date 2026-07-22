# AniLibria API v1 — фактическая схема (снята живыми запросами 2026-07-22)

Base URL: `https://anilibria.top/api/v1`
Все ответы ниже получены реальными HTTP-запросами, не из документации.

## Инфраструктура

- Сам API за **Cloudflare** (`Server: cloudflare`, `CF-RAY`, `cf-cache-status: DYNAMIC`), не только видео-CDN.
- `Cache-Control: no-cache, private` — серверного кеша нет, кешируем сами.
- `vary: Origin` — CORS обрабатывается.
- Ответ на запрос из US прошёл без гео-ограничений.

## `GET app/status` — механизм обнаружения эндпоинтов ⭐

```json
{
  "request": {"ip":"...","country":"United States","iso_code":"US","timezone":"America/Chicago"},
  "is_alive": true,
  "available_api_endpoints": ["https://aniliberty.top"]
}
```

**Важно:** API сам публикует список живых доменов. Это готовый механизм failover при миграции/блокировке домена — не нужно изобретать свой. Стратегия: стартовый base URL из конфига → при старте/ошибке дёрнуть `app/status` → переключиться на живой из `available_api_endpoints`.

Также отдаёт гео-контекст запроса (`iso_code`) — пригодится для `is_blocked_by_geo`.

## `GET anime/catalog/releases?limit=&page=`

Обёртка: `{ "data": [...], "meta": {...} }`

```json
"meta": {"pagination": {
  "total": 1859, "count": 2, "per_page": 2,
  "current_page": 1, "total_pages": 930,
  "links": {"next": "https://anilibria.top/api/v1/anime/catalog/releases?limit=2&page=2"}
}}
```
Всего в каталоге ~1859 релизов. Пагинация классическая page/limit + готовая ссылка `next`.

## Объект Release

| Поле | Тип | Примечание |
|---|---|---|
| `id` | Int | |
| `alias` | String | человекочитаемый slug, годится для запроса вместо id |
| `type` | object `{value, description}` | enum-обёртка |
| `year` | Int | |
| `name` | object `{main, english, alternative}` | `main` — русское, `alternative` часто `null` |
| `season` | object `{value, description}` | |
| `poster` | object | **пути относительные**, см. ниже |
| `fresh_at`/`created_at`/`updated_at` | String | ISO-8601 с зоной |
| `is_ongoing` | Bool | |
| `age_rating` | object `{value, label, is_adult, description}` | напр. `R16_PLUS` / `16+` / `is_adult:false` |
| `publish_day` | object `{value, description}` | 1=Пн … 7=Вс |
| `description` | String | русский текст |
| `notification` | null | часто null |
| `episodes_total` | null | **часто null даже у релиза с эпизодами** |
| `external_player` | null | |
| `is_in_production` | Bool | |
| `is_blocked_by_geo` | Bool | |
| `is_blocked_by_copyrights` | Bool | |
| `average_duration_of_episode` | null | ненадёжно, брать `duration` из эпизода |
| `added_in_users_favorites` | Int | |
| `added_in_{planned,watched,watching,postponed,abandoned}_collection` | Int | соц-статистика |
| `genres` | array | |

Только в детальном ответе `anime/releases/{idOrAlias}`, отсутствуют в каталоге:
`members` (команда озвучки/перевода), `sponsors`, `episodes`, `torrents`.

### Poster / изображения — ⚠️ относительные пути

```json
"poster": {
  "src":       "/storage/releases/posters/10234/aYIR...jpg",
  "preview":   "/storage/releases/posters/10234/aYIR...jpg",
  "thumbnail": "/storage/releases/posters/10234/nUYh...jpg",
  "optimized": {
    "src":       "/storage/releases/posters/10234/VpgK...webp",
    "preview":   "/storage/releases/posters/10234/VpgK...webp",
    "thumbnail": "/storage/releases/posters/10234/6B8w...webp"
  }
}
```
Нужно склеивать с хостом. **Для мобилки брать `optimized.*` (WebP)** — заметно легче. Та же структура у `genres[].image` и `episodes[].preview`.

### Genre

```json
{"id":14, "name":"Экшен", "image":{...}, "total_releases":671}
```

## Объект Episode (в `anime/releases/{idOrAlias}` → `episodes[]`)

| Поле | Тип | Примечание |
|---|---|---|
| `id` | **String (UUID)** | ⚠️ у релиза `id` — Int, у эпизода — UUID-строка. Не унифицировать типы бездумно |
| `name` | String? | русское название серии |
| `name_english` | String? | часто `null` |
| `ordinal` | Int | номер серии |
| `sort_order` | Int | |
| `release_id` | Int | обратная ссылка |
| `duration` | Int | секунды (напр. 1420) |
| `opening` | object `{start, stop}` | секунды, поля могут быть `null` |
| `ending` | object `{start, stop}` | то же |
| `preview` | object | превью-кадр, относительные пути |
| `hls_480` / `hls_720` / `hls_1080` | String? | **абсолютные** URL `.m3u8`, любое может быть `null` |
| `rutube_id` / `youtube_id` | String? | альтернативные плееры, часто `null` |
| `updated_at` | String | ISO-8601 |

Пример HLS: `https://cache.libria.fun/videos/media/ts/10234/1/720/f8501a....m3u8`
Схема пути: `/videos/media/ts/{release_id}/{ordinal}/{quality}/{hash}.m3u8`

## ⚠️ Неконсистентность обёрток ответа (критично для сетевого слоя)

Разные эндпоинты возвращают структурно **разные** формы. Единый generic-враппер `ApiResponse<T>` на всё не подойдёт.

| Эндпоинт | Форма ответа | Размер |
|---|---|---|
| `anime/catalog/releases` | **объект** `{data:[], meta:{pagination}}` | ~6.6 КБ / 2 шт |
| `app/search/releases?query=` | **голый массив** | 15 КБ / 3 шт |
| `anime/releases/latest?limit=` | **голый массив** | 16 КБ / 2 шт |
| `anime/genres` | **голый массив** (35 жанров) | 16 КБ |
| `anime/schedule/week` | **голый массив** (34 записи) | **257 КБ** |

### Разный набор полей у одной и той же сущности Release

| Источник | Есть `genres` | Есть `latest_episode` |
|---|---|---|
| `catalog/releases` | ✅ | ❌ |
| `app/search/releases` | ❌ | ❌ |
| `releases/latest` | ✅ | ✅ |
| `releases/{alias}` | ✅ | ❌ (зато `episodes`, `members`, `torrents`, `sponsors`) |

То есть «Release» — это не одна схема, а надмножество. Вывод: **один DTO с широкими nullable-полями**, а не отдельные DTO на эндпоинт (иначе дублирование ~27 полей ×4), плюс доменные модели разного «богатства» на выходе мапперов.

### Schedule

`anime/schedule/week` → массив записей:
```
{ release: {...Release}, full_season_is_released: Bool,
  published_release_episode: {...Episode}, next_release_episode_number: ... }
```
**257 КБ на один запрос** — обязателен кеш и/или отложенная загрузка, на мобильном трафике это заметно.

## Выводы для реализации

1. **`ignoreUnknownKeys = true` обязателен** — схема шире, чем задокументировано, и меняется.
2. **Почти всё nullable.** `episodes_total`, `average_duration_of_episode`, `alternative`, `name_english`, любое `hls_*` — реально приходят `null`. DTO делать максимально nullable, дефолты подставлять в мапперах.
3. **Не смешивать типы id:** релиз — `Int`, эпизод — `String(UUID)`.
4. **Изображения относительные, HLS абсолютные** — разная обработка.
5. **enum-обёртки `{value, description}`** — не парсить как строку; заводить свои enum по `value` с безопасным fallback на неизвестное значение.
6. **`app/status` → `available_api_endpoints`** — встроенный failover, использовать вместо самодельного списка зеркал.
