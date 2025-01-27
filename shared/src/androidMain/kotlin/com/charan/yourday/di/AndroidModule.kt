package com.charan.yourday.di

import com.charan.yourday.data.repository.LocationServiceRepo
import com.charan.yourday.data.repository.impl.LocationServiceImp
import dev.icerock.moko.permissions.PermissionsController
import io.ktor.client.engine.okhttp.OkHttp

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single { PermissionsController(applicationContext = androidContext()) }
    factory   <LocationServiceRepo>{ LocationServiceImp(context = androidContext())  }
    single { OkHttp.create() }
}