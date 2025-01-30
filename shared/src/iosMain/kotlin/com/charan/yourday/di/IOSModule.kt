package com.charan.yourday.di

import com.charan.yourday.data.repository.CalenderEventsRepo
import com.charan.yourday.data.repository.LocationServiceRepo
import com.charan.yourday.data.repository.impl.CalenderEventsImp
import com.charan.yourday.data.repository.impl.LocationServiceImp
import com.charan.yourday.permission.PermissionManager
import com.charan.yourday.permission.PermissionManagerImp
import com.charan.yourday.viewmodels.HomeScreenViewModel
import dev.icerock.moko.permissions.ios.PermissionsController
import dev.icerock.moko.permissions.ios.PermissionsControllerProtocol
import io.ktor.client.engine.darwin.Darwin
import kotlinx.io.IOException
import org.koin.compose.koinInject
import org.koin.core.component.get
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.dsl.module


val iosModule = module {
    single <LocationServiceRepo>{ LocationServiceImp() }
    single <PermissionsControllerProtocol>{ PermissionsController() }
    single { Darwin.create() }
    single <PermissionManager>{ PermissionManagerImp() }
    single <CalenderEventsRepo>{ CalenderEventsImp() }
}

object ProvideComponents : KoinComponent {
    fun getHomeViewModel() : HomeScreenViewModel = get()
}
class KointInitHelper(){
    fun initKoin() {
        initKoin {
            modules(iosModule)
        }
    }
}