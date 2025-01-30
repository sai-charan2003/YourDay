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
import com.charan.yourday.utils.ProcessState
import com.charan.yourday.utils.asCommonFlow
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.RequestCanceledException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val weatherRepo: WeatherRepo,
    private val permissionsController: PermissionsController,
    private val locationServiceRepo: LocationServiceRepo,
    private val permissionManager: PermissionManager,
    private val calenderEventsRepo: CalenderEventsRepo
) : ViewModel() {
    private val _weatherData = MutableStateFlow<ProcessState<WeatherDTO>>(ProcessState.Loading)
    val weatherData = _weatherData.asCommonFlow()
    private val _calenderEvents = MutableStateFlow<List<CalenderItems>>(emptyList())
    val calenderEvents = _calenderEvents.asCommonFlow()
    val notificationPermissionstate = MutableStateFlow(PermissionState.NotDetermined)
    var permissionState by mutableStateOf(PermissionState.NotDetermined)
        private set
    init {
        viewModelScope.launch {
            permissionState = permissionsController.getPermissionState(Permission.LOCATION)
            permissionsController.getPermissionState(Permission.LOCATION)
            notificationPermissionstate.collectLatest {
                when(it){
                    PermissionState.NotDetermined -> {}
                    PermissionState.NotGranted -> {}
                    PermissionState.Granted -> {
                        getCalenderEvents()
                    }
                    PermissionState.Denied -> {}
                    PermissionState.DeniedAlways -> {}
                }
            }
        }
        getOrProvidePermission()



    }


    private fun getWeatherForecast(lat: Double, long: Double) = viewModelScope.launch {
        val weatherData = weatherRepo.getCurrentForecast(lat, long)
        weatherData.collectLatest {
           print(it)
            _weatherData.tryEmit(it)
        }
    }

    private fun getOrProvidePermission() {
        viewModelScope.launch {
            try {
                permissionsController.providePermission(Permission.LOCATION)
                permissionState = PermissionState.Granted
                getLocation()
                getCalenderPermission()
            } catch(e: DeniedAlwaysException) {
                permissionState = PermissionState.DeniedAlways
            } catch(e: DeniedException) {
                permissionState = PermissionState.Denied
            } catch(e: RequestCanceledException) {
                e.printStackTrace()
            }
        }
    }

    private fun getLocation() = viewModelScope.launch{
        val location = locationServiceRepo.getCurrentLocation()
        if(location!=null) {
            getWeatherForecast(lat = location.latitude!!, long = location.longitude!!)
        }
    }

    private fun getCalenderPermission(){
        permissionManager.requestCalenderPermission()
        getCalenderEvents()

    }

    fun getCalenderEvents() {
        _calenderEvents.tryEmit(calenderEventsRepo.getCalenderEvents())
    }



}