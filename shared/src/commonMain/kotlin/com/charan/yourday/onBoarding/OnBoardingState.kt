package com.charan.yourday.onBoarding

data class OnBoardingState (
    val isLocationPermissionGranted : Boolean = false,
    val isCalenderPermissionGranted : Boolean = false,
    val isTodoistConnected : Boolean = false,
)