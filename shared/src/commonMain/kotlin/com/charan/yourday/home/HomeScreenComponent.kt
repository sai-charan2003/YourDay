package com.charan.yourday.home

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.coroutineScope
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
import com.charan.yourday.utils.DataStoreConst
import com.charan.yourday.utils.ProcessState
import com.charan.yourday.utils.asCommonFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class HomeScreenComponent(
    val authorizationId : String?,
    val errorCode : String?,
    componentContext: ComponentContext
) : KoinComponent, ComponentContext by componentContext {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val weatherRepo: WeatherRepo = get()
    private val locationServiceRepo: LocationServiceRepo = get()
    private val permissionManager: PermissionManager = get()
    private val calenderEventsRepo: CalenderEventsRepo = get()
    private val todoistRepo: TodoistRepo = get()
    private val datastore : DataStore<Preferences> = get()

    private val _weatherData = MutableStateFlow<ProcessState<WeatherDTO>>(ProcessState.Loading)
    val weatherData = _weatherData.asCommonFlow()
    private val _calenderEvents = MutableStateFlow<List<CalenderItems>>(emptyList())
    val calenderEvents = _calenderEvents.asCommonFlow()
    private val _calenderPermission = MutableStateFlow<Boolean>(false)
    val calenderPermission = _calenderPermission.asCommonFlow()
    private val todoistToken = stringPreferencesKey(DataStoreConst.TODOIST_ACCESS_TOKEN)
    private val _todoistAuthorizationFlow = MutableStateFlow<ProcessState<TodoistTokenDTO>>(
        ProcessState.NotDetermined)
    val todoistAuthorizationFlow = _todoistAuthorizationFlow.asCommonFlow()
    private val _todoistTasks = MutableStateFlow<ProcessState<List<TodoistTodayTasksDTO>>>(
        ProcessState.Loading)
    val todoistTasks = _todoistTasks.asCommonFlow()
    private val _todoistAuthToken = MutableStateFlow<String?>(null)
    val todoistAuthToken = _todoistAuthToken.asCommonFlow()
    private val _isErrorSent = MutableStateFlow(false)
    val isErrorSent = _isErrorSent.asCommonFlow()



    init {
        isCalenderPermissionGranted()
        if(authorizationId != null){
            authenticateTodoist(authorizationId)
        }
        if(errorCode !=null){
            _isErrorSent.tryEmit(true)
        }
        checkTokenAndFetchTasks()


    }


    private fun getWeatherForecast(lat: Double, long: Double) = coroutineScope.launch {
        val weatherData = weatherRepo.getCurrentForecast(lat, long)
        weatherData.collectLatest {
            _weatherData.tryEmit(it)
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
        datastore.edit {
            it[todoistToken] = token
        }
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
        datastore.data.collectLatest {
            val token = it[todoistToken]
            _todoistAuthToken.tryEmit(token)
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
        datastore.edit {preferences ->
            preferences.remove(todoistToken)

        }

    }

    fun openSettings(){
        permissionManager.openAppSettings()
    }



}