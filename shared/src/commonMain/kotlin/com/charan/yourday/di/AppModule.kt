package com.charan.yourday.di

import com.charan.yourday.data.network.ApiService
import com.charan.yourday.data.network.KtorClient
import com.charan.yourday.data.repository.LocationServiceRepo
import com.charan.yourday.data.repository.WeatherRepo
import com.charan.yourday.data.repository.impl.WeatherRepoImp
import com.charan.yourday.viewmodels.HomeScreenViewModel
import dev.icerock.moko.permissions.PermissionsController
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

    val appModule = module {
        single { KtorClient.client }
        factory  { ApiService(client = get()) }
        factory  <WeatherRepo> { WeatherRepoImp(apiService = get()) }
        viewModel { HomeScreenViewModel(get(),get(),get()) }
    }

    fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
        appDeclaration()
        modules(appModule)
    }
