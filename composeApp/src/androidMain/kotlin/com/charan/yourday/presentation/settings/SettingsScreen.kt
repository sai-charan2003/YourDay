package com.charan.yourday.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.UiMode
import androidx.compose.ui.unit.dp
import com.charan.yourday.settings.SettingsEvents
import com.charan.yourday.settings.SettingsScreenComponent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(
    component: SettingsScreenComponent
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Settings") },
                navigationIcon ={
                    IconButton(
                        onClick = {
                            component.onEvent(SettingsEvents.onBack)

                        }
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack,null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                ListItem(
                    headlineContent = {
                        Text("Weather Settings")
                    },
                    shadowElevation = 10.dp,
                    modifier = Modifier.clickable {
                        component.onWeatherScreenOpen()

                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 10.dp))
            }
            item { 
                ListItem(
                    headlineContent = {
                        Text("  Integration")
                    },
                    modifier = Modifier.clickable {
                        component.onEvent(SettingsEvents.OnTodoItem)
                    }
                )
            }


        }

    }

}
