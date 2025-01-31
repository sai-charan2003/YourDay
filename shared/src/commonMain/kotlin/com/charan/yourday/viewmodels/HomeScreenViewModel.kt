package com.charan.yourday.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.yourday.data.model.CalenderItems
import com.charan.yourday.data.network.responseDTO.WeatherDTO
import com.charan.yourday.data.repository.CalenderEventsRepo
import com.charan.yourday.data.repository.LocationServiceRepo
import com.charan.yourday.data.repository.WeatherRepo
import com.charan.yourday.permission.PermissionManager
import com.charan.yourday.permission.Permissions
import com.charan.yourday.utils.PlatformSettings
import com.charan.yourday.utils.ProcessState
import com.charan.yourday.utils.asCommonFlow
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val weatherRepo: WeatherRepo,
    private val locationServiceRepo: LocationServiceRepo,
    private val permissionManager: PermissionManager,
    private val calenderEventsRepo: CalenderEventsRepo,
) : ViewModel() {

    private val _weatherData = MutableStateFlow<ProcessState<WeatherDTO>>(ProcessState.Loading)
    val weatherData = _weatherData.asCommonFlow()
    private val _calenderEvents = MutableStateFlow<List<CalenderItems>>(emptyList())
    val calenderEvents = _calenderEvents.asCommonFlow()

    init {
        permissionManager.requestMultiplePermissions(listOf(Permissions.CALENDER,Permissions.LOCATION))
    }


    private fun getWeatherForecast(lat: Double, long: Double) = viewModelScope.launch {
        val weatherData = weatherRepo.getCurrentForecast(lat, long)
        weatherData.collectLatest {
            _weatherData.tryEmit(it)
        }
    }


    fun getLocation() = viewModelScope.launch {
        val location = locationServiceRepo.getCurrentLocation()
        if (location != null) {
            getWeatherForecast(lat = location.latitude!!, long = location.longitude!!)
        }
    }

    fun getCalenderEvents() {
        _calenderEvents.tryEmit(calenderEventsRepo.getCalenderEvents())
    }

    fun grantLocationPermission(openSettings : Boolean) {
        if(openSettings) {
            permissionManager.requestPermission(Permissions.LOCATION)

        } else {
            permissionManager.openAppSettings()

        }
    }

    fun grantCalenderPermission(openSettings: Boolean) {
        if(openSettings) {
            permissionManager.requestPermission(Permissions.CALENDER)

        } else {
            permissionManager.openAppSettings()

        }

    }






}