    package com.charan.yourday.home

    import com.arkivanov.decompose.ComponentContext
    import com.charan.yourday.data.model.WeatherData
    import com.charan.yourday.data.network.responseDTO.TodoistTokenDTO
    import com.charan.yourday.data.network.responseDTO.WeatherDTO
    import com.charan.yourday.data.repository.CalenderEventsRepo
    import com.charan.yourday.data.repository.LocationServiceRepo
    import com.charan.yourday.data.repository.TodoistRepo
    import com.charan.yourday.data.repository.WeatherRepo
    import com.charan.yourday.permission.PermissionManager
    import com.charan.yourday.permission.Permissions
    import com.charan.yourday.utils.ErrorCodes
    import com.charan.yourday.utils.ProcessState
    import kotlinx.coroutines.flow.MutableSharedFlow
    import com.charan.yourday.utils.UserPreferencesStore
    import com.charan.yourday.utils.WeatherUnits
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
        private val authorizationId: String?,
        private val errorCode: String?,
        private val onSettingsOpen: () -> Unit,
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
            if (authorizationId != null) {
                getTodoistAuthToken(authorizationId)
            }
            if(errorCode !=null){
                coroutineScope.launch {
                    _effects.emit(HomeViewEffect.ShowToast("Unable to authenticate"))
                }
            }
            checkTokenAndFetchTasks()
        }

        fun onEvent(intent: HomeEvent) {
            when (intent) {
                is HomeEvent.RequestLocationPermission -> handleLocationPermission(intent.showRationale)
                is HomeEvent.RequestCalendarPermission -> handleCalendarPermission(intent.showRationale)
                HomeEvent.ConnectTodoist -> requestTodoistAuthentication()
                HomeEvent.FetchWeather -> getLocation()
                HomeEvent.FetchCalendarEvents -> fetchCalendarEvents()
                HomeEvent.DisconnectTodoist -> clearTodoistToken()
                HomeEvent.OpenSettingsPage -> onSettingsOpen()

            }
        }



        private fun getLocation() = coroutineScope.launch {
            val location = locationServiceRepo.getCurrentLocation()
            if (location != null) {
                _state.update { currentState ->
                    currentState.copy(
                        weatherState = currentState.weatherState.copy(
                            isLoading = true,
                            isLocationPermissionGranted = true
                        )
                    )
                }
                fetchWeatherData(lat = location.latitude!!, long = location.longitude!!)
            }
        }

        private fun fetchWeatherData(lat: Double, long: Double) = coroutineScope.launch {
            weatherRepo.getCurrentForecast(lat, long).collectLatest { processState ->
                when (processState) {
                    is ProcessState.Error -> {
                        _state.update {
                            it.copy(
                                weatherState = it.weatherState.copy(
                                    isLoading = false,
                                    error = processState.message,
                                    isLocationPermissionGranted = isPermissionEnabled(Permissions.LOCATION)
                                )
                            )
                        }
                    }
                    ProcessState.Loading -> {
                        _state.update {
                            it.copy(
                                weatherState = it.weatherState.copy(
                                    isLoading = true,
                                    isLocationPermissionGranted = isPermissionEnabled(Permissions.LOCATION)
                                )
                            )
                        }
                    }
                    ProcessState.NotDetermined -> {
                        _state.update {
                            it.copy(
                                weatherState = it.weatherState.copy(
                                    isLoading = false
                                )
                            )
                        }
                    }
                    is ProcessState.Success -> handleWeatherSuccess(processState.data)
                }
            }
        }

        private suspend fun handleWeatherSuccess(data: WeatherDTO) {
            userPreferences.weatherUnits.collectLatest { units ->
                val weatherData = when (units) {
                    WeatherUnits.C -> WeatherData(
                        currentTemperature = data.getCurrentTemperatureInC(),
                        maxTemperature = data.getMaxTemperatureInC(),
                        minTemperature = data.getMinTemperatureInC(),
                        currentCondition = data.getCurrentCondition(),
                        temperatureIcon = data.getImageIcon(),
                        location = data.getLocation()
                    )
                    WeatherUnits.F -> WeatherData(
                        currentTemperature = data.getCurrentTemperatureInF(),
                        maxTemperature = data.getMaxTemperatureInF(),
                        minTemperature = data.getMinTemperatureInF(),
                        currentCondition = data.getCurrentCondition(),
                        temperatureIcon = data.getImageIcon(),
                        location = data.getLocation()
                    )
                    else -> {
                        WeatherData()
                    }
                }

                _state.update {
                    it.copy(
                        weatherState = it.weatherState.copy(
                            isLoading = false,
                            isLocationPermissionGranted = isPermissionEnabled(Permissions.LOCATION),
                            weatherData = weatherData
                        )
                    )
                }
            }
        }


        private fun handleLocationPermission(shouldShowRationale: Boolean) = coroutineScope.launch{
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
            _state.update {
                it.copy(
                    calenderData = it.calenderData.copy(
                        isCalenderPermissionGranted = isPermissionEnabled(Permissions.CALENDER),
                        calenderData = calendarEventsRepo.getCalenderEvents()
                    )
                )
            }
        }


        private fun requestTodoistAuthentication() = coroutineScope.launch {
            _state.update {
                it.copy(
                    todoState = it.todoState.copy(
                        isAuthenticating = true
                    )
                )
            }
            todoistRepo.requestAuthorization()
        }

        private fun getTodoistAuthToken(authorizationId: String) {
            coroutineScope.launch {
                todoistRepo.getAccessToken(authorizationId).collectLatest { state ->
                    when (state) {
                        is ProcessState.Error -> handleTodoistAuthError(state.message)
                        ProcessState.Loading -> {
                            _state.update {
                                it.copy(
                                    todoState = it.todoState.copy(
                                        isLoading = true
                                    )
                                )
                            }
                        }
                        ProcessState.NotDetermined -> { /* No action needed */ }
                        is ProcessState.Success -> handleTodoistAuthSuccess(state.data)
                    }
                }
            }
        }

        private fun handleTodoistAuthError(message: String) {
            if (message == ErrorCodes.UNAUTHORIZED.name) {
                clearTodoistToken()
            }

            _state.update {
                it.copy(
                    todoState = it.todoState.copy(
                        isLoading = false,
                        isAuthenticating = false,
                        isTodoAuthenticated = false
                    )
                )
            }
        }

        private fun handleTodoistAuthSuccess(data: TodoistTokenDTO) {
            _state.update {
                it.copy(
                    todoState = it.todoState.copy(
                        isLoading = false,
                        isAuthenticating = false,
                        isTodoAuthenticated = true
                    )
                )
            }

            data.access_token?.let { token ->
                saveTodoistToken(token)
                fetchTodoistTasks(token)
            }
        }

        private fun saveTodoistToken(token: String) = coroutineScope.launch {
            userPreferences.setTodoistAccessToken(token)
        }

        private fun fetchTodoistTasks(token: String) {
            coroutineScope.launch {
                todoistRepo.getTodayTasks(token).collectLatest { state ->
                    when (state) {
                        is ProcessState.Error -> {
                            _state.update {
                                it.copy(
                                    todoState = TodoState(
                                        isLoading = false,
                                        error = state.message
                                    )
                                )
                            }
                        }
                        ProcessState.Loading -> {  }
                        ProcessState.NotDetermined -> {  }
                        is ProcessState.Success -> {
                            _state.update {
                                it.copy(
                                    todoState = TodoState(
                                        isTodoAuthenticated = true,
                                        isAuthenticating = false,
                                        isLoading = false,
                                        todoData = state.data
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        private fun checkTokenAndFetchTasks() = coroutineScope.launch {
            userPreferences.todoistAccessToken.collectLatest { token ->
                if (token != null) {
                    _state.update {
                        it.copy(
                            todoState = TodoState(
                                isTodoAuthenticated = true
                            )
                        )
                    }
                    fetchTodoistTasks(token)
                }
            }
        }

        private fun clearTodoistToken() = coroutineScope.launch {
            userPreferences.deleteTodoistToken()
            _state.update {
                it.copy(
                    todoState = TodoState(
                        isTodoAuthenticated = false,
                        isAuthenticating = false,
                        isLoading = false
                    )
                )
            }
        }


    }