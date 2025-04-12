package com.charan.yourday.home

import com.arkivanov.decompose.ComponentContext
import com.charan.yourday.data.mapper.mapToWeatherData
import com.charan.yourday.data.model.TodoData
import com.charan.yourday.data.model.WeatherData
import com.charan.yourday.data.network.responseDTO.TodoistTokenDTO
import com.charan.yourday.data.network.responseDTO.WeatherDTO
import com.charan.yourday.data.repository.CalenderEventsRepo
import com.charan.yourday.data.repository.LocationServiceRepo
import com.charan.yourday.data.repository.TodoistRepo
import com.charan.yourday.data.repository.WeatherRepo
import com.charan.yourday.permission.PermissionManager
import com.charan.yourday.permission.Permissions
import com.charan.yourday.utils.DateUtils
import com.charan.yourday.utils.ErrorCodes
import com.charan.yourday.utils.OpenURL
import com.charan.yourday.utils.ProcessState
import com.charan.yourday.utils.TodoProvidersEnums
import kotlinx.coroutines.flow.MutableSharedFlow
import com.charan.yourday.utils.UserPreferencesStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


class HomeScreenComponent(
    val authorizationId: String?,
    private val errorCode: String?,
    private val todoProvider : TodoProvidersEnums?,
    private val onSettingsOpen: () -> Unit = {},
    private val onBoardFinish: () -> Unit = {},
    componentContext: ComponentContext
) : KoinComponent, ComponentContext by componentContext {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)



    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<HomeViewEffect>()
    val effects = _effects.asSharedFlow()


    private val weatherRepo: WeatherRepo = get()
    private val locationServiceRepo: LocationServiceRepo = get()
    private val permissionManager: PermissionManager = get()
    private val calendarEventsRepo: CalenderEventsRepo = get()
    private val todoistRepo: TodoistRepo = get()
    private val userPreferences: UserPreferencesStore = get()

    init {
        coroutineScope.launch {
            if(todoProvider !=null){
                authenticateTodoProvider()
            }
            checkTokenAndFetchTasks()
        }
    }

    private fun authenticateTodoProvider(){
        when(todoProvider){
            TodoProvidersEnums.TODOIST -> {
                authorizationId?.let { getTodoistAuthToken(it) }
                errorCode?.let { showToastEvent("Unable to authenticate") }

            }
            TodoProvidersEnums.TICK_TICK -> {

            }
            TodoProvidersEnums.UNKNOWN -> {

            }
            else -> {

            }
        }
    }

    fun onEvent(intent: HomeEvent) {
        when (intent) {
            is HomeEvent.RequestLocationPermission -> handleLocationPermission(intent.showRationale)
            is HomeEvent.RequestCalendarPermission -> handleCalendarPermission(intent.showRationale)
            is HomeEvent.ConnectTodoist -> requestAuthentication(intent.todoProvider)
            HomeEvent.FetchWeather -> getLocation()
            HomeEvent.FetchCalendarEvents -> fetchCalendarEvents()
            HomeEvent.DisconnectTodoist -> clearTodoistToken()
            HomeEvent.OpenSettingsPage -> onSettingsOpen()
            is HomeEvent.OnOpenLink -> openURL(intent.url)
            HomeEvent.FetchTodo -> checkTokenAndFetchTasks()
            HomeEvent.RefreshData -> refreshData()
            HomeEvent.OnBoardingFinish -> {
                onBoardFinish()
            }
        }
    }

    private fun refreshData() {
        getLocation()
        fetchCalendarEvents()
        checkTokenAndFetchTasks()


    }

    private fun requestAuthentication(provider : String){
        when(provider){
            TodoProvidersEnums.TODOIST.name -> {
                requestTodoistAuthentication()
            }

        }
    }



    private fun getLocation() = coroutineScope.launch {
        updateWeatherState(isLoading = true)
        if(permissionManager.isPermissionGranted(Permissions.LOCATION)) {
            val location = locationServiceRepo.getCurrentLocation()
            if (location != null) {
                fetchWeatherData(location.latitude!!, location.longitude!!)
            } else {
                showToastEvent("Unable to fetch the location")
                updateWeatherState(
                    isLoading = false,
                    error = "Unable to fetch the location"
                )
            }
        } else {
            updateWeatherState(isLoading = false)
        }
    }

    private fun openURL(url : String) {
        OpenURL.openURL(url)
    }

    private fun fetchWeatherData(lat: Double, long: Double) = coroutineScope.launch {
        weatherRepo.getCurrentForecast(lat, long).collectLatest { processState ->
            when (processState) {
                is ProcessState.Error -> {
                    updateWeatherState(error = processState.message)
                    showToastEvent(processState.message)
                }
                ProcessState.Loading -> updateWeatherState(isLoading = true)
                ProcessState.NotDetermined -> updateWeatherState()
                is ProcessState.Success -> handleWeatherSuccess(processState.data)
            }
        }
    }

    private suspend fun handleWeatherSuccess(data: WeatherDTO) {
        userPreferences.weatherUnits.collectLatest { units ->
            val weatherData = units?.let { data.mapToWeatherData(it) }
            updateWeatherState(weatherData = weatherData, lastSycned = DateUtils.getCurrentTimeInMillis().toString())

        }
    }


    private fun handleLocationPermission(shouldShowRationale: Boolean) = coroutineScope.launch{
        println(shouldShowRationale)
        if (!shouldShowRationale) {
            _effects.emit(HomeViewEffect.RequestLocationPermission)

        } else {
            permissionManager.openAppSettings()
        }
    }

    private fun handleCalendarPermission(shouldShowRationale: Boolean) = coroutineScope.launch{
        if (!shouldShowRationale) {
            _effects.emit(HomeViewEffect.RequestCalenderPermission)
        } else {
            permissionManager.openAppSettings()
        }
    }

    private fun isPermissionEnabled(permissions: Permissions): Boolean {
        return permissionManager.isPermissionGranted(permissions)
    }


    private fun fetchCalendarEvents() {
        println("Fetching calender events....")
        println(isPermissionEnabled(Permissions.CALENDER))
        if(isPermissionEnabled(Permissions.CALENDER)) {
            _state.update {
                it.copy(
                    calenderData = it.calenderData.copy(
                        isCalenderPermissionGranted = isPermissionEnabled(Permissions.CALENDER),
                        calenderData = calendarEventsRepo.getCalenderEvents()
                    )
                )
            }
        }
    }


    private fun requestTodoistAuthentication() = coroutineScope.launch {
        updateTodoState(isAuthenticating = true,todoProvider = TodoProvidersEnums.TODOIST)
        todoistRepo.requestAuthorization()
    }

    private fun getTodoistAuthToken(authorizationId: String) = coroutineScope.launch {
        todoistRepo.getAccessToken(authorizationId).collectLatest { processState ->
            when (processState) {
                is ProcessState.Error -> handleTodoistAuthError(processState.message)
                ProcessState.Loading -> updateTodoState(isLoading = true,todoProvider = TodoProvidersEnums.TODOIST)
                ProcessState.NotDetermined -> { /* No action needed */ }
                is ProcessState.Success -> handleTodoistAuthSuccess(processState.data)
            }
        }
    }

    private fun handleTodoistAuthError(message: String) {
        if (message == ErrorCodes.UNAUTHORIZED.name) clearTodoistToken()
        updateTodoState(isTodoAuthenticated = false,todoProvider = TodoProvidersEnums.TODOIST)
    }

    private fun handleTodoistAuthSuccess(data: TodoistTokenDTO) {
        data.access_token?.let { token ->
            updateTodoState(isTodoAuthenticated = true, isAuthenticating = false,todoProvider = TodoProvidersEnums.TODOIST)
            saveTodoistToken(token)
            fetchTodoistTasks(token)
        }
    }

    private fun saveTodoistToken(token: String) = coroutineScope.launch {
        userPreferences.setTodoistAccessToken(token)
    }

    private fun fetchTodoistTasks(token: String) = coroutineScope.launch {
        todoistRepo.getTodayTasks(token).collectLatest { processState ->
            when (processState) {
                is ProcessState.Error -> handleTodoistTasksError(processState.message)
                ProcessState.Loading -> updateTodoState(isLoading = true,todoProvider = TodoProvidersEnums.TODOIST)
                ProcessState.NotDetermined -> { /* No action needed */ }
                is ProcessState.Success -> updateTodoState(todoData = processState.data, lastSycned = DateUtils.getCurrentTimeInMillis().toString(), isTodoAuthenticated = true,todoProvider = TodoProvidersEnums.TODOIST)
            }
        }
    }

    private fun handleTodoistTasksError(message: String) {
        if (message == ErrorCodes.UNAUTHORIZED.name) {
            clearTodoistToken()
            showToastEvent("Please connect again")
            updateTodoState(
                isTodoAuthenticated = false,
                isAuthenticating = false,
                isLoading = false,
                todoProvider = TodoProvidersEnums.TODOIST
            )
        } else {
            updateTodoState(
                isLoading =  false,
                error = message,todoProvider = TodoProvidersEnums.TODOIST
            )
        }

    }

    private fun checkTokenAndFetchTasks() = coroutineScope.launch {
        userPreferences.todoistAccessToken.collectLatest { token ->
            if(token == null){
                updateTodoState(isLoading = false, isTodoAuthenticated = false,todoProvider = TodoProvidersEnums.TODOIST)
                return@collectLatest
            }
            token?.let {
                updateTodoState(isTodoAuthenticated = true,todoProvider = TodoProvidersEnums.TODOIST)
                fetchTodoistTasks(it)

            }
        }
    }

    private fun clearTodoistToken() = coroutineScope.launch {
        userPreferences.deleteTodoistToken()
        updateTodoState(isTodoAuthenticated = false, todoProvider = TodoProvidersEnums.TODOIST)
    }

    private fun showToastEvent(e : String) = coroutineScope.launch {
        _effects.emit(HomeViewEffect.ShowToast(e))
    }
    private fun updateWeatherState(
        isLoading: Boolean = false,
        error: String? = null,
        weatherData: WeatherData? = null,
        lastSycned: String? = null
    ) {
        _state.update {
            it.copy(
                weatherState = it.weatherState.copy(
                    isLoading = isLoading,
                    error = error,
                    isLocationPermissionGranted = isPermissionEnabled(Permissions.LOCATION),
                    weatherData = weatherData ?: it.weatherState.weatherData,
                    lastSycned = lastSycned
                ),
            )
        }
    }

    private fun updateTodoState(
        isLoading: Boolean = false,
        isAuthenticating: Boolean = false,
        isTodoAuthenticated: Boolean? = null,
        todoData: List<TodoData>? = null,
        lastSycned: String? = null,
        error: String? = null,
        todoProvider: TodoProvidersEnums
    ) {
        _state.update { currentState ->
            val existingProviderState = currentState.todoState.todoProviderState[todoProvider.name] ?: TodoProviderState()

            val updatedProviderState = existingProviderState.copy(
                isAuthenticating = isAuthenticating,
                isAuthenticated = isTodoAuthenticated ?: existingProviderState.isAuthenticated,
                lastSycned = lastSycned ?: existingProviderState.lastSycned,
                error = error
            )

            val updatedMap = currentState.todoState.todoProviderState.toMutableMap().apply {
                put(todoProvider.name, updatedProviderState)
            }

            currentState.copy(
                todoState = currentState.todoState.copy(
                    todoProviderState = updatedMap,
                    isLoading = isLoading,
                    isAnyTodoAuthenticated = isTodoAuthenticated == true,
                    todoData = todoData
                )
            )
        }
    }



}