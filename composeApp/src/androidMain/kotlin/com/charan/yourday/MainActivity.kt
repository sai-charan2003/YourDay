package com.charan.yourday

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat.PermissionCompatDelegate
import androidx.core.view.WindowCompat
import com.charan.yourday.di.androidModule
import com.charan.yourday.di.initKoin
import com.charan.yourday.viewmodels.HomeScreenViewModel
import dev.icerock.moko.permissions.compose.BindEffect
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.dsl.module

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        initKoin{
            androidContext(this@MainActivity)
            modules(androidModule)
            androidLogger()
        }
        setContent {
            App()
        }
    }

    override fun onResume() {
        super.onResume()
    }


}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}