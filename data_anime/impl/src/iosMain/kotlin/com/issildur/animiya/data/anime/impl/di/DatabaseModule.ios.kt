package com.issildur.animiya.data.anime.impl.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.issildur.animiya.data.anime.impl.db.AnimiyaDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformDatabaseModule(): Module = module {
    single<SqlDriver> {
        NativeSqliteDriver(schema = AnimiyaDatabase.Schema, name = "animiya.db")
    }
}
