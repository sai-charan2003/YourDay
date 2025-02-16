package com.charan.yourday.settings

sealed class SettingsEvents {
        data class OnChangeWeatherUnits(val weatherUnit : String) : SettingsEvents()
        data object OnWeatherItem : SettingsEvents()
        data object TodoConnect : SettingsEvents()
        data object OnTodoItem : SettingsEvents()
        data object onBack : SettingsEvents()
}