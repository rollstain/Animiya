package com.issildur.animiya.composeapp.di

import android.os.Build
import com.issildur.animiya.core.network.api.PlatformInfo
import org.koin.core.module.Module
import org.koin.dsl.module

private class AndroidPlatformInfo : PlatformInfo {
    override val appName: String = "Animiya"
    override val appVersion: String = AppVersion.NAME
    override val platformName: String = "Android"
    override val osVersion: String = Build.VERSION.RELEASE ?: "unknown"
    override val deviceModel: String = "${Build.MANUFACTURER} ${Build.MODEL}"
}

actual val platformModule: Module = module {
    single<PlatformInfo> { AndroidPlatformInfo() }
}
