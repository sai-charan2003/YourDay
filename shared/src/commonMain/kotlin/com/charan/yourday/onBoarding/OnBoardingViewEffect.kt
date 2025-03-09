package com.charan.yourday.onBoarding

sealed interface OnBoardingViewEffect {
    data object OnLocationPermissionRequest : OnBoardingViewEffect
    data object OnCalenderPermissionRequest : OnBoardingViewEffect
}