package com.charan.yourday

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat.PermissionCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.handleDeepLink
import com.arkivanov.decompose.retainedComponent
import com.charan.yourday.di.androidModule
import com.charan.yourday.di.initKoin
import com.charan.yourday.root.RootComponent
import com.charan.yourday.utils.TodoProvidersEnums

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalDecomposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        val root =
            handleDeepLink { uri ->
                val authenticationCode = uri?.extractTodoistAuthentication()
                val error = uri?.extractErrorForTodoist()
                RootComponent(
                    componentContext = defaultComponentContext(
                        discardSavedState = authenticationCode != null,
                    ),
                    authorizationId = authenticationCode,
                    errorCode = error,
                    todoProvider = uri?.getTodoProvider()
                )
            } ?: return
        setContent {
            App(root)
        }
    }

    private fun Uri.getTodoProvider() : TodoProvidersEnums{
        val path =  this.path.toString()
        if(path.contains("todoist")){
            return TodoProvidersEnums.TODOIST
        }
        if(path.contains("tick_tick")){
            return TodoProvidersEnums.TICK_TICK
        }
        return TodoProvidersEnums.UNKNOWN
    }
    private fun Uri.extractTodoistAuthentication(): String? {
        val code = this.getQueryParameter("code")
        return code

    }

    private fun Uri.extractErrorForTodoist() : String? {
        val error = this.getQueryParameter("error")
        return error
    }


}

