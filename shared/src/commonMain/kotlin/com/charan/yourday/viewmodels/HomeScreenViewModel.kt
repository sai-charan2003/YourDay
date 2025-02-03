package com.charan.yourday.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.yourday.Platform
import com.charan.yourday.data.model.CalenderItems
import com.charan.yourday.data.network.responseDTO.TodoistTodayTasksDTO
import com.charan.yourday.data.network.responseDTO.TodoistTokenDTO
import com.charan.yourday.data.network.responseDTO.WeatherDTO
import com.charan.yourday.data.repository.CalenderEventsRepo
import com.charan.yourday.data.repository.LocationServiceRepo
import com.charan.yourday.data.repository.TodoistRepo
import com.charan.yourday.data.repository.WeatherRepo
import com.charan.yourday.permission.PermissionManager
import com.charan.yourday.permission.Permissions
import com.charan.yourday.utils.AppConstants
import com.charan.yourday.utils.CommonFlow
import com.charan.yourday.utils.PlatformSettings
import com.charan.yourday.utils.ProcessState
import com.charan.yourday.utils.asCommonFlow
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val weatherRepo: WeatherRepo,
    private val locationServiceRepo: LocationServiceRepo,
    private val permissionManager: PermissionManager,
    private val calenderEventsRepo: CalenderEventsRepo,
    private val todoistRepo: TodoistRepo,
    private val datastore : DataStore<Preferences>

) : ViewModel() {


    private val _weatherData = MutableStateFlow<ProcessState<WeatherDTO>>(ProcessState.Loading)
    val weatherData = _weatherData.asCommonFlow()
    private val _calenderEvents = MutableStateFlow<List<CalenderItems>>(emptyList())
    val calenderEvents = _calenderEvents.asCommonFlow()
    private val _calenderPermission = MutableStateFlow<Boolean>(false)
    val calenderPermission = _calenderPermission.asCommonFlow()
    private val todoistToken = stringPreferencesKey(AppConstants.TODOIST_ACCESS_TOKEN)
    private val _todoistAuthorizationFlow = MutableStateFlow<ProcessState<TodoistTokenDTO>>(ProcessState.NotDetermined)
    val todoistAuthorizationFlow = _todoistAuthorizationFlow.asCommonFlow()
    private val _todoistTasks = MutableStateFlow<ProcessState<List<TodoistTodayTasksDTO>>>(ProcessState.Loading)
    val todoistTasks = _todoistTasks.asCommonFlow()
    private val _todoistAuthToken = MutableStateFlow<String?>(null)
    val todoistAuthToken = _todoistAuthToken.asCommonFlow()


    init {
        isCalenderPermissionGranted()
        getToken()

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

    fun requestTodoistAuthentication() =viewModelScope.launch{
        _todoistAuthorizationFlow.tryEmit(ProcessState.Loading)
        todoistRepo.requestAuthorization()
    }

    fun getTodoistAccessToken(code : String)  = viewModelScope.launch {
        todoistRepo.getAccessToken(code).collectLatest {
            println("Access Token")
            println(it)
            _todoistAuthorizationFlow.tryEmit(it)
        }
    }

    fun getTodoistTodayTasks(code: String) = viewModelScope.launch {
        todoistRepo.getTodayTasks(code).collectLatest {
            _todoistTasks.tryEmit(it)
        }



    }

    fun saveTodoistToken(token: String) = viewModelScope.launch {
        datastore.edit {
            it[todoistToken] = token
        }
    }

    fun getToken() = viewModelScope.launch {

        datastore.data.collectLatest {
            _todoistAuthToken.tryEmit(it[todoistToken])

        }
    }






}