package com.charan.yourday.presentation.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.charan.yourday.settings.SettingsEvents
import com.charan.yourday.settings.SettingsScreenComponent
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseScreen(
    component: SettingsScreenComponent
){
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("License") },
                scrollBehavior = scroll,
                navigationIcon = {
                    IconButton(onClick = { component.onEvent(SettingsEvents.onBack)}) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "back")

                    }
                }
            )

        }
    ) {
        LibrariesContainer(
            modifier = Modifier.padding(it).nestedScroll(scroll.nestedScrollConnection)
        )


    }

}