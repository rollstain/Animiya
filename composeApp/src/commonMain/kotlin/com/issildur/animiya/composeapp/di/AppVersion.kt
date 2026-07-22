package com.issildur.animiya.composeapp.di

/**
 * Версия приложения для User-Agent.
 *
 * Пока константа: BuildConfig в KMP-модуле с новым AGP-плагином не генерируется,
 * а тащить ради одной строки отдельный плагин генерации не хочется.
 */
internal object AppVersion {
    const val NAME: String = "0.1.0"
}
