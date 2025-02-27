package com.charan.yourday.home

sealed interface HomeEvent {
    data class RequestLocationPermission(val showRationale: Boolean) : HomeEvent
    data class RequestCalendarPermission(val showRationale: Boolean) : HomeEvent
    object ResetLocationPermission : HomeEvent
    object ResetCalenderPermission : HomeEvent
    object ConnectTodoist : HomeEvent
    object FetchWeather : HomeEvent
    object FetchCalendarEvents : HomeEvent
    object DisconnectTodoist : HomeEvent
    object OpenSettingsPage : HomeEvent
    data class ShowToast(val content : String) : HomeEvent
    object ClearToast : HomeEvent
}