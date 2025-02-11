package com.charan.yourday.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.charan.yourday.home.HomeScreenComponent
import kotlinx.serialization.Serializable

class RootComponent(
    var authorizationId: String? = null,
    var errorCode: String? = null,
    componentContext: ComponentContext
) : ComponentContext by componentContext{

    private val navigation = StackNavigation<Configuration>()
    val childStack : Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = Configuration.HomeScreen(authorizationId,errorCode),
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun createChild(
        config:  Configuration,
        context : ComponentContext
    ) : Child{
        return when(config) {
            is Configuration.HomeScreen -> Child.HomeScreen(HomeScreenComponent(componentContext=context,authorizationId = config.authorizationId, errorCode = config.errorCode))
        }

    }

    sealed class Child {
        data class HomeScreen(val component: HomeScreenComponent) : Child()
    }
    @Serializable
    sealed class  Configuration {
        @Serializable
        data class HomeScreen(val authorizationId : String?,val errorCode : String?) : Configuration()
    }


}