package com.issildur.animiya

import android.app.Application
import com.issildur.animiya.composeapp.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class AnimiyaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(isDebug = BuildConfig.DEBUG) {
            androidContext(this@AnimiyaApplication)
            if (BuildConfig.DEBUG) androidLogger()
        }
    }
}
