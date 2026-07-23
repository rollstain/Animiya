package com.issildur.animiya.data.anime.impl.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.issildur.animiya.data.anime.impl.db.AnimiyaDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformDatabaseModule(): Module = module {
    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = AnimiyaDatabase.Schema,
            context = androidContext(),
            name = "animiya.db",
        )
    }
}
