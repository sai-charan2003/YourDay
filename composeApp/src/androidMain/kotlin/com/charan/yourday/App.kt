package com.charan.yourday

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.charan.yourday.presentation.home.HomeScreen
import com.charan.yourday.presentation.navigation.NavigationAppHost
import com.charan.yourday.root.RootComponent
import com.charan.yourday.utils.DateUtils
import com.charan.yourday.viewmodels.HomeScreenViewModel
import com.example.compose.AppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import dev.icerock.moko.permissions.PermissionState
import com.arkivanov.decompose.extensions.compose.stack.Children
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.getKoin
import org.koin.compose.koinInject



@OptIn(ExperimentalPermissionsApi::class)
@Composable
@Preview
fun App(root: RootComponent) {
    KoinContext  {
        val permissionsController: PermissionsController = koinInject()
        BindEffect(permissionsController = permissionsController)
        val multiplePermissionRequest = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissions.keys.forEach {

            }
        }
        LaunchedEffect(Unit) {
            multiplePermissionRequest.launch(
                arrayOf(
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }

        AppTheme {
            Surface {
                Children(
                    stack = root.childStack
                ) { child ->
                    when (val instance = child.instance) {
                        is RootComponent.Child.HomeScreen -> HomeScreen(instance.component, authorizationId = instance.component.authorizationId, error = instance.component.errorCode)

                    }

                }
            }
        }

    }

}