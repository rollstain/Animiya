# Источники видео для Animiya: классификация по формату (2026-07-24)

Вопрос: какие источники отдают ПРЯМОЙ поток (HLS/mp4) через API — играются в нашем
нативном плеере БЕЗ iframe и БЕЗ реверса. Проверено WebFetch по исходникам обёрток/доке.

## Категории
- **A** — прямой HLS/mp4 + API → наш плеер напрямую
- **B** — iframe/embed (балансёр или свой плеер) → только WebView или deep-link
- **C** — только HTML-скрапинг/реверс чужой защиты → хрупко + юр. риск
- **D** — не установлено

## Категория A (годится для нативного плеера) — их МАЛО
1. **AniLibria/AniLiberty** — `anilibria.top/api/v1` (+ старое v3 `api.anilibria.tv/v3/title` → `player.list[].hls.fhd/hd/sd`), прямой .m3u8 на `cache.libria.fun`, авторизация не нужна. Эталон, надёжен. Домен мигрирует — нужен fallback.
2. **AnimeVost** — `api.animevost.org/v1/` (search/last/playlist), поля `hd`/`std` = прямые .mp4. Работает годами, НО официальной доки нет (API известно через сообщество: anicli-api, avproxy, AnimeVostPlayer).
3. **Anime365/SmotretAnime** — с оговоркой: часть переводов HLS, часть iframe; ПЛАТНАЯ подписка; офиц. публичной доки нет. 3-й с натяжкой.

Международных A-источников — НОЛЬ.

## Категория B (iframe)
- **Sovetromantica** — свой embed (openapi.yaml, поле `embed` → страница-плеер, не поток). Но это ИХ own плеер, не сторонний балансёр; часть контента вроде частично лицензирована → риск ниже Kodik.
- **Kodik/Alloha/Sibnet** — сторонние балансёры, F6-инфраструктура.
- **AnimeGO** — HTML + Kodik/AniBoom. Блок РКН.
- **Anixart, AniNet** — трекеры-агрегаторы поверх балансёров (Kodik и др.). НЕ источники видео сами по себе.

## Категория C (реверс/скрапинг — НЕ трогаем)
Jut.su (mp4 через HTML, блок РКН), AllAnime (XOR-шифр), Gogoanime/Anitaku, HiAnime/Zoro
(враппер aniwatch под **HTTP 451**), Animepahe (kwik-обфускация), **Consumet** (под
**DMCA 2026-04**: «circumvention functionality... programmatic access to pirated content»).
Юридически токсично даже на уровне инструментов.

## Вывод для §8.1 (уровни риска гибрида)
1. **Наш плеер, чисто:** AniLibria (.m3u8) + AnimeVost (.mp4). Фандаб без лицензии (серо),
   но НЕ F6-инфраструктура, без реверса, наши фичи работают.
2. **+ embed своих:** Sovetromantica (own embed, риск ниже балансёров).
3. **+ балансёры (полный Anixart):** Kodik/Alloha → iframe-реверс, F6-риск, сайдлоад,
   вылет из Google Play.
Категорию C — исключить полностью.

## Архитектурные заметки
- Писать 2 тонких адаптера (AniLibria + AnimeVost) поверх их JSON API, НЕ тащить
  универсальные парсер-тулкиты (80% кода под B/C + юр. риск).
- Доменная модель `Release → Translation → Episode → Stream(quality, url, type)`,
  где `type ∈ {DIRECT_HLS, DIRECT_MP4, EMBED}` → роутер плеера по type.
- Fallback на смену домена обязателен (AniLibria мигрирует, у AnimeVost нет доки).
