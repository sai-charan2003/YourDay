package com.charan.yourday.presentation.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.charan.yourday.settings.SettingsEvents
import com.charan.yourday.settings.SettingsScreenComponent
import com.charan.yourday.utils.WeatherUnits
import com.charan.yourday.utils.WeatherUnitsEnums

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherSettingScreen(
    component: SettingsScreenComponent
) {
    var dropDown by remember { mutableStateOf(false) }
    val weatherUnits = component.weatherUnits.collectAsState()
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text("Weather")
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
                        Text("Temperature Units")
                    },
                    trailingContent = {
                        TextButton(onClick = {dropDown = true}) {
                            weatherUnits.value?.let { Text(it) }
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                            MaterialTheme(shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))){
                                DropdownMenu(
                                    expanded = dropDown,
                                    onDismissRequest = {dropDown = false}) {
                                    WeatherUnitsEnums.entries.forEach {
                                        val item = when(it){
                                            WeatherUnitsEnums.C -> WeatherUnits.C
                                            WeatherUnitsEnums.F -> WeatherUnits.F
                                        }
                                        DropdownMenuItem(
                                            text = {
                                                Text(item)
                                            },
                                            onClick = {
                                                dropDown = false
                                                component.onEvent(SettingsEvents.OnChangeWeatherUnits(item))
                                            }
                                        )
                                    }

                                }
                            }

                        }
                    }
                )
            }

        }


    }

}
