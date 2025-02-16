package com.charan.yourday.presentation.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charan.yourday.settings.SettingsEvents
import com.charan.yourday.settings.SettingsScreenComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoSettingsScreen(
    component: SettingsScreenComponent
) {
    val todoToken = component.todoToken.collectAsState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text("Integrations")
                },
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
                        Text("Todoist Integration")
                    },
                    trailingContent = {
                        ElevatedButton(onClick = {
                            component.onEvent(SettingsEvents.TodoConnect)
                        },
                            shape = ButtonDefaults.shape
                        ) {
                            if(todoToken.value.isNullOrEmpty()){
                                Icon(Icons.Default.Link,null)

                                Text("Connect", modifier = Modifier.padding(start = 5.dp))
                            } else{
                                Icon(Icons.Default.LinkOff,null)

                                Text("Disconnect",modifier = Modifier.padding(start = 5.dp))
                            }
                        }
                    }
                )
            }

        }

    }

}