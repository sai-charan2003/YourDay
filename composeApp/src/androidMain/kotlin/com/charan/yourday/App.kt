package com.charan.yourday

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.charan.yourday.presentation.home.HomeScreen
import com.charan.yourday.utils.DateUtils
import com.example.compose.AppTheme
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.getKoin
import org.koin.compose.koinInject


@Composable
@Preview
fun App() {
    KoinContext  {
        val permissionsController: PermissionsController = koinInject()
        BindEffect(permissionsController = permissionsController)
        AppTheme {
            Surface {
                HomeScreen()

            }
        }

    }

}