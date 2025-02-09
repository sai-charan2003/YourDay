package com.charan.yourday.di

import com.charan.yourday.createDataStore
import com.charan.yourday.data.repository.CalenderEventsRepo
import com.charan.yourday.data.repository.LocationServiceRepo
import com.charan.yourday.data.repository.impl.CalenderEventsImp
import com.charan.yourday.data.repository.impl.LocationServiceImp
import com.charan.yourday.permission.PermissionManager
import com.charan.yourday.permission.PermissionManagerImp
import dev.icerock.moko.permissions.ios.PermissionsController
import dev.icerock.moko.permissions.ios.PermissionsControllerProtocol
import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module


val iosModule = module {
    single <LocationServiceRepo>{ LocationServiceImp() }
    single <PermissionsControllerProtocol>{ PermissionsController() }
    single { Darwin.create() }
    single <PermissionManager>{ PermissionManagerImp() }
    single <CalenderEventsRepo>{ CalenderEventsImp() }
    single { createDataStore() }
}

class KointInitHelper(){
    fun initKoin() {
        initKoin {
            modules(iosModule)
        }
    }
}