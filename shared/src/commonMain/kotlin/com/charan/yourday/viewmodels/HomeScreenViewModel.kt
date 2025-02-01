package com.charan.yourday.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.yourday.Platform
import com.charan.yourday.data.model.CalenderItems
import com.charan.yourday.data.network.responseDTO.WeatherDTO
import com.charan.yourday.data.repository.CalenderEventsRepo
import com.charan.yourday.data.repository.LocationServiceRepo
import com.charan.yourday.data.repository.WeatherRepo
import com.charan.yourday.permission.PermissionManager
import com.charan.yourday.permission.Permissions
import com.charan.yourday.utils.CommonFlow
import com.charan.yourday.utils.PlatformSettings
import com.charan.yourday.utils.ProcessState
import com.charan.yourday.utils.asCommonFlow
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.coroutines.flow.Flow
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
    private val _calenderPermission = MutableStateFlow<Boolean>(false)
    val calenderPermission = _calenderPermission.asCommonFlow()


    init {
        isCalenderPermissionGranted()
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

    fun grantLocationPermission(shouldShowRationale : Boolean) {
        if(shouldShowRationale) {
            permissionManager.requestPermission(Permissions.LOCATION)

        } else {
            permissionManager.openAppSettings()

        }
    }

    fun grantCalenderPermission(shouldShowRationale: Boolean) {
        if(shouldShowRationale) {
            permissionManager.requestPermission(Permissions.CALENDER)

        } else {
            permissionManager.openAppSettings()

        }

    }

    fun isPermissionEnabled(permissions: Permissions) : Boolean {
        return permissionManager.isPermissionGranted(permissions)

    }

    fun isCalenderPermissionGranted() = viewModelScope.launch{
       permissionManager.observeCalenderPermission().collectLatest {
           if(it == true){
               getCalenderEvents()
           }
           _calenderPermission.tryEmit(it)

       }
    }

    fun requestLocationCalenderPermission() {
        permissionManager.requestMultiplePermissions(listOf(Permissions.LOCATION,Permissions.CALENDER))


    }






}