package com.charan.yourday.home

import com.arkivanov.decompose.ComponentContext
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
import org.koin.compose.koinInject
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class HomeScreenComponent(
    val authorizationId : String?,
    val errorCode : String?,
    val onSettingsOpen : () -> Unit,
    componentContext: ComponentContext
) : KoinComponent, ComponentContext by componentContext {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val weatherRepo: WeatherRepo = get()
    private val locationServiceRepo: LocationServiceRepo = get()
    private val permissionManager: PermissionManager = get()
    private val calenderEventsRepo: CalenderEventsRepo = get()
    private val todoistRepo: TodoistRepo = get()
    private val userPreferences : UserPreferencesStore = get()


    private val _weatherData = MutableStateFlow<ProcessState<WeatherDTO>>(ProcessState.Loading)
    val weatherData = _weatherData.asCommonFlow()
    private val _calenderEvents = MutableStateFlow<List<CalenderItems>>(emptyList())
    val calenderEvents = _calenderEvents.asCommonFlow()
    private val _calenderPermission = MutableStateFlow<Boolean>(false)
    val calenderPermission = _calenderPermission.asCommonFlow()
    private val _todoistAuthorizationFlow = MutableStateFlow<ProcessState<TodoistTokenDTO>>(
        ProcessState.NotDetermined)
    val todoistAuthorizationFlow = _todoistAuthorizationFlow.asStateFlow()
    private val _todoistTasks = MutableStateFlow<ProcessState<List<TodoistTodayTasksDTO>>>(
        ProcessState.Loading)
    val todoistTasks = _todoistTasks.asStateFlow()
    private val _todoistAuthToken = MutableStateFlow<String?>(null)
    val todoistAuthToken = _todoistAuthToken.asStateFlow()
    private val _isErrorSent = MutableStateFlow(false)
    val isErrorSent = _isErrorSent.asCommonFlow()

    private val _currentTemperature = MutableStateFlow<String?>(null)
    val currentTemperature= _currentTemperature.asStateFlow()

    private val _maxTemperature = MutableStateFlow<String?>(null)
    val maxTemperature = _maxTemperature.asStateFlow()

    private val _minTemperature = MutableStateFlow<String?>(null)
    val minTemperature = _minTemperature.asStateFlow()



    init {
        print(authorizationId)
        isCalenderPermissionGranted()
        if(authorizationId != null){
            authenticateTodoist(authorizationId)
        }
        if(errorCode !=null){
            _isErrorSent.tryEmit(true)
        }
        checkTokenAndFetchTasks()
        checkUserPreference()
    }

    private fun checkUserPreference() = coroutineScope.launch{
        userPreferences.weatherUnits.collect { unit ->
            println("from user preference $unit")
            getCurrentTemperature(unit ?: WeatherUnits.C)

        }

    }


    private fun getWeatherForecast(lat: Double, long: Double) = coroutineScope.launch {
        val weatherData = weatherRepo.getCurrentForecast(lat, long)
        weatherData.collectLatest {
            _weatherData.tryEmit(it)
        }
    }

    private fun getCurrentTemperature(unit : String) = coroutineScope.launch{
        _weatherData.collectLatest { weatherData ->
            when (unit) {
                WeatherUnits.C -> {
                    _currentTemperature.tryEmit(
                        weatherData.extractData()?.getCurrentTemperatureInC()
                    )
                    _maxTemperature.tryEmit(
                        weatherData.extractData()?.getMaxTemperatureInC()
                    )
                    _minTemperature.tryEmit(
                        weatherData.extractData()?.getMinTemperatureInC()
                    )

                }

                WeatherUnits.F -> {
                    _currentTemperature.tryEmit(
                        weatherData.extractData()?.getCurrentTemperatureInF()
                    )
                    _maxTemperature.tryEmit(
                        weatherData.extractData()?.getMaxTemperatureInF()
                    )
                    _minTemperature.tryEmit(
                        weatherData.extractData()?.getMinTemperatureInF()
                    )

                }
            }
        }
    }


    fun getLocation() = coroutineScope.launch {
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

    fun isCalenderPermissionGranted() = coroutineScope.launch{
        permissionManager.observeCalenderPermission().collectLatest {
            if(it == true){
                getCalenderEvents()
            }
            _calenderPermission.tryEmit(it)

        }
    }

    fun requestLocationCalenderPermission() {
        permissionManager.requestMultiplePermissions(listOf(Permissions.LOCATION, Permissions.CALENDER))
    }

    fun requestTodoistAuthentication() =coroutineScope.launch{
        _todoistAuthorizationFlow.tryEmit(ProcessState.Loading)
        todoistRepo.requestAuthorization()
    }

    fun saveTodoistToken(token: String) = coroutineScope.launch {
        userPreferences.setTodoistAccessToken(token)
    }

    fun authenticateTodoist(authorizationId: String) {
        coroutineScope.launch {
            todoistRepo.getAccessToken(authorizationId).collectLatest { state ->
                _todoistAuthorizationFlow.value = state
                if (state is ProcessState.Success) {
                    state.data.access_token?.let { token ->
                        saveTodoistToken(token)
                        fetchTodoistTasks(token)
                    }
                }
            }
        }
    }


    private fun fetchTodoistTasks(token: String) {
        coroutineScope.launch {
            todoistRepo.getTodayTasks(token).collectLatest { state ->
                _todoistTasks.tryEmit(state)
            }
        }
    }

    private fun checkTokenAndFetchTasks()= coroutineScope.launch {
        userPreferences.todoistAccessToken.collectLatest { token ->

            _todoistAuthToken.tryEmit(token)
            print("the token for the todoist is ${_todoistAuthToken.value}")
            println(token)
            if(token !=null){
                fetchTodoistTasks(token)
            }

        }


    }

    fun resetTodoistFlow() {
        _todoistAuthorizationFlow.tryEmit(ProcessState.NotDetermined)
    }

    fun clearTodoistToken() = coroutineScope.launch{
        userPreferences.deleteTodoistToken()
    }

    fun openSettings(){
        permissionManager.openAppSettings()
    }



}