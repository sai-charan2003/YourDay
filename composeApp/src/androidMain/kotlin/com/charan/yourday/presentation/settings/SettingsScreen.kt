package com.charan.yourday.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.UiMode
import androidx.compose.ui.unit.dp
import com.charan.yourday.presentation.settings.components.SectionDivider
import com.charan.yourday.presentation.settings.components.SectionHeader
import com.charan.yourday.presentation.settings.components.SettingItem
import com.charan.yourday.settings.SettingsEvents
import com.charan.yourday.settings.SettingsScreenComponent
import com.charan.yourday.utils.WeatherUnits
import com.charan.yourday.utils.WeatherUnitsEnums

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    component: SettingsScreenComponent
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var dropDown by remember { mutableStateOf(false) }
    val state by component.settingsState.collectAsState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = { component.onEvent(SettingsEvents.onBack) }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {

            item {
                SectionHeader(title = "Weather")
                SettingItem(
                    label = "Temperature Units",
                    trailingContent = {

                        TextButton(onClick = { dropDown = true }) {
                            state.weatherUnits?.let { Text(it) }
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Select unit")
                        }

                        DropdownMenu(
                            expanded = dropDown,
                            onDismissRequest = { dropDown = false }
                        ) {
                            WeatherUnitsEnums.entries.forEach { unitEnum ->
                                val unit = when (unitEnum) {
                                    WeatherUnitsEnums.C -> WeatherUnits.C
                                    WeatherUnitsEnums.F -> WeatherUnits.F
                                }
                                DropdownMenuItem(
                                    text = { Text(unit) },
                                    onClick = {
                                        dropDown = false
                                        component.onEvent(SettingsEvents.OnChangeWeatherUnits(unit))
                                    }
                                )
                            }
                        }
                    }
                )
                SectionDivider()
            }

            item {
                SectionHeader(title = "Tasks")
                SettingItem(
                    label = "Todoist",
                    trailingContent = {
                        TextButton(
                            onClick = { component.onEvent(SettingsEvents.TodoConnect) }
                        ) {
                            val buttonText = if (state.isTodoistConnected == false) "Connect" else "Disconnect"
                            val buttonColor = if (state.isTodoistConnected == false) Color.Green else Color.Red
                            Text(buttonText, color = buttonColor)
                        }
                    }
                )
                SectionDivider()
            }

            item {
                SectionHeader(title = "About App")
                SettingItem(
                    label = "Open source licenses",
                    isClickable = true,
                    onClick = {
                        component.onEvent(SettingsEvents.OnLicenseNavigate)
                    }
                )
                SettingItem(
                    label = "App version",
                    supportingContent = {
                        Text(state.appVersion ?: "", style = MaterialTheme.typography.bodyMedium)
                    },

                )
            }
        }
    }
}






