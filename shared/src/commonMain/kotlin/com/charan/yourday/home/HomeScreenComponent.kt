    package com.charan.yourday.home

    import com.arkivanov.decompose.ComponentContext
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
            coroutineScope.launch {
                authorizationId?.let { getTodoistAuthToken(it) }
                errorCode?.let { showToastEvent("Unable to authenticate") }
                checkTokenAndFetchTasks()
            }
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
                is HomeEvent.OnOpenLink -> openURL(intent.url)
                HomeEvent.FetchTodo -> checkTokenAndFetchTasks()
                HomeEvent.RefreshData -> refreshData()
            }
        }

        private fun refreshData() {
            _state.update { it.copy(isRefreshing = true) }
            coroutineScope.launch {
                val job1 = launch { getLocation() }
                val job2 = launch { fetchCalendarEvents() }
                val job3 = launch { checkTokenAndFetchTasks() }
                job1.join()
                job2.join()
                job3.join()
                _state.update { it.copy(isRefreshing = false) }
            }


        }



        private fun getLocation() = coroutineScope.launch {
            updateWeatherState(isLoading = true)
            locationServiceRepo.getCurrentLocation()?.let {
                fetchWeatherData(it.latitude!!, it.longitude!!)
            } ?: showToastEvent("Unable to fetch the location")
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
                updateWeatherState(weatherData = weatherData, lastSycned = DateUtils.getCurrentTimeInMillis().toString())


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
            updateTodoState(isAuthenticating = true)
            todoistRepo.requestAuthorization()
        }

        private fun getTodoistAuthToken(authorizationId: String) = coroutineScope.launch {
            todoistRepo.getAccessToken(authorizationId).collectLatest { processState ->
                when (processState) {
                    is ProcessState.Error -> handleTodoistAuthError(processState.message)
                    ProcessState.Loading -> updateTodoState(isLoading = true)
                    ProcessState.NotDetermined -> { /* No action needed */ }
                    is ProcessState.Success -> handleTodoistAuthSuccess(processState.data)
                }
            }
        }

        private fun handleTodoistAuthError(message: String) {
            if (message == ErrorCodes.UNAUTHORIZED.name) clearTodoistToken()
            updateTodoState(isTodoAuthenticated = false)
        }

        private fun handleTodoistAuthSuccess(data: TodoistTokenDTO) {
            data.access_token?.let { token ->
                updateTodoState(isTodoAuthenticated = true, isAuthenticating = false)
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
                    ProcessState.Loading -> updateTodoState(isLoading = true)
                    ProcessState.NotDetermined -> { /* No action needed */ }
                    is ProcessState.Success -> updateTodoState(todoData = processState.data, lastSycned = DateUtils.getCurrentTimeInMillis().toString())
                }
            }
        }

        private fun handleTodoistTasksError(message: String) {
            if (message == ErrorCodes.UNAUTHORIZED.name) {
                clearTodoistToken()
            }
            showToastEvent("Please connect again")
            _state.update {
                it.copy(
                    todoState =  it.todoState.copy(
                        isTodoAuthenticated =  false,
                        isAuthenticating = false,
                        isLoading = false
                    )
                )
            }

        }

        private fun checkTokenAndFetchTasks() = coroutineScope.launch {
            userPreferences.todoistAccessToken.collectLatest { token ->
                if(token == null){
                    updateTodoState(isLoading = false, isTodoAuthenticated = false)
                    return@collectLatest
                }
                token?.let {
                    fetchTodoistTasks(it)
                    updateTodoState(isTodoAuthenticated = true)
                }
            }
        }

        private fun clearTodoistToken() = coroutineScope.launch {
            userPreferences.deleteTodoistToken()
            updateTodoState(isTodoAuthenticated = false)
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
                        error = if(error.isNullOrEmpty().not()) error else it.weatherState.error,
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
            lastSycned : String? = null
        ) {
            _state.update {
                it.copy(
                    todoState = it.todoState.copy(
                        isLoading = isLoading,
                        isAuthenticating = isAuthenticating,
                        isTodoAuthenticated = isTodoAuthenticated ?: it.todoState.isTodoAuthenticated,
                        todoData = todoData ?: it.todoState.todoData,
                        lastSycned = lastSycned
                    ),
                )
            }
        }


    }