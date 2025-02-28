package com.charan.yourday.home

sealed interface HomeViewEffect {
    data class ShowToast(val message : String) : HomeViewEffect
    data object RequestLocationPermission : HomeViewEffect
    data object RequestCalenderPermission : HomeViewEffect

}