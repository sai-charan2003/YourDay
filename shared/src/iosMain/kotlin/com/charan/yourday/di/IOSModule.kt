package com.charan.yourday.di

import com.charan.yourday.data.repository.LocationServiceRepo
import com.charan.yourday.data.repository.impl.LocationServiceImp
import com.charan.yourday.viewmodels.HomeScreenViewModel
import org.koin.compose.koinInject
import org.koin.core.component.get
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.dsl.module

val iosModule = module {
    single <LocationServiceRepo>{ LocationServiceImp() }
}

object ProvideComponents : KoinComponent {
        fun getHomeViewModel() : HomeScreenViewModel = get()
}
fun initKoin(){
    initKoin {
        modules(iosModule)
    }
}