package com.charan.yourday.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.arkivanov.decompose.ComponentContext
import com.charan.yourday.data.network.responseDTO.WeatherDTO
import com.charan.yourday.data.repository.TodoistRepo
import com.charan.yourday.utils.ProcessState
import com.charan.yourday.utils.UserPreferencesStore
import com.charan.yourday.utils.WeatherUnits
import com.charan.yourday.utils.asCommonFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class SettingsScreenComponent (
    componentContext: ComponentContext,
    val onWeatherScreenOpen : () -> Unit ,
    val onTodoSettingsOpen : () -> Unit,
    val onBackClick : () -> Unit

) : KoinComponent, ComponentContext by componentContext{
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val userPreferences : UserPreferencesStore = get()
    private val todoistRepo: TodoistRepo = get()
    private val _weatherUnits = MutableStateFlow<String?>(null)
    private val _todoToken = MutableStateFlow<String?>(null)
    val todoToken = _todoToken.asStateFlow()
    val weatherUnits = _weatherUnits.asStateFlow()
    init {
        getSetTemperatureUnits()
        isTodoConnected()
    }

    private fun getSetTemperatureUnits() = coroutineScope.launch{
        userPreferences.weatherUnits.collectLatest {
            _weatherUnits.value = it
        }
    }
    private fun setTemperature(weatherUnits : String) = coroutineScope.launch {
       userPreferences.setWeatherUnits(weatherUnits)
    }
    private fun deleteTodoistToken() = coroutineScope.launch {
        userPreferences.deleteTodoistToken()
    }
    private fun isTodoConnected() = coroutineScope.launch {
        userPreferences.todoistAccessToken.collectLatest {
            _todoToken.value = it
        }

    }

    fun onEvent(event : SettingsEvents) = coroutineScope.launch{
        when(event){
            is SettingsEvents.OnChangeWeatherUnits -> {
                setTemperature(event.weatherUnit)
            }

            SettingsEvents.OnWeatherItem -> {
                onWeatherScreenOpen()
            }

            SettingsEvents.TodoConnect -> {
                if(_todoToken.value.isNullOrEmpty()){
                    todoistRepo.requestAuthorization()
                } else{
                    deleteTodoistToken()
                }
            }

            SettingsEvents.OnTodoItem -> {
                onTodoSettingsOpen()
            }

            SettingsEvents.onBack -> {
                onBackClick()
            }
        }
    }
}