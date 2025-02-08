package com.charan.yourday.root

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.KoinComponent

class HomeScreenComponent(
    val authorizationId : String?,
    val errorCode : String?,
    componentContext: ComponentContext
) : KoinComponent, ComponentContext by componentContext {


}