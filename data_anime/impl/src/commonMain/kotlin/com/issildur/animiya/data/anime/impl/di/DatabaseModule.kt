package com.issildur.animiya.data.anime.impl.di

import org.koin.core.module.Module

/**
 * Платформенный драйвер БД: SqlDriver предоставляется по-разному на Android
 * (нужен Context) и iOS. Подключается в приложении вместе с [dataAnimeModule].
 */
expect fun platformDatabaseModule(): Module
