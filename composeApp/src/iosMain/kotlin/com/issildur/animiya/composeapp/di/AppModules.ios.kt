package com.issildur.animiya.composeapp.di

import com.issildur.animiya.core.network.api.PlatformInfo
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.UIKit.UIDevice

private class IosPlatformInfo : PlatformInfo {
    override val appName: String = "Animiya"
    override val appVersion: String = AppVersion.NAME
    override val platformName: String = "iOS"
    override val osVersion: String = UIDevice.currentDevice.systemVersion
    override val deviceModel: String = UIDevice.currentDevice.model
}

actual val platformModule: Module = module {
    single<PlatformInfo> { IosPlatformInfo() }
}
