package com.charan.yourday

import android.app.Activity
import android.app.Application
import com.charan.yourday.di.androidModule
import com.charan.yourday.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@MainApplication)
            modules(androidModule)
            androidLogger()

        }
    }
}