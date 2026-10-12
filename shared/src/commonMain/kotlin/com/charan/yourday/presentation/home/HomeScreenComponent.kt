package com.charan.yourday.presentation.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.doOnResume
import com.charan.yourday.data.model.WeatherData
import com.charan.yourday.data.repository.CalendarEventsRepository
import com.charan.yourday.data.repository.LocationServiceRepository
import com.charan.yourday.data.repository.TodoistRepository
import com.charan.yourday.data.repository.UserPreferencesRepository
import com.charan.yourday.data.repository.WeatherRepository
import com.charan.yourday.permission.PermissionManager
import com.charan.yourday.presentation.toCurrentWeatherState
import com.charan.yourday.presentation.toForecastWeatherState
import com.charan.yourday.presentation.toTodoDataState
import com.charan.yourday.utils.DateUtils
import com.charan.yourday.utils.DateUtils.getGreeting
import com.charan.yourday.utils.DateUtils.toDDMMYYYY
import com.charan.yourday.utils.ErrorCodes
import com.charan.yourday.utils.OpenURL
import com.splendo.kaluga.permissions.base.PermissionState
import com.splendo.kaluga.permissions.base.Permissions
import com.splendo.kaluga.permissions.base.PermissionsBuilder
import com.splendo.kaluga.permissions.calendar.CalendarPermission
import com.splendo.kaluga.permissions.location.LocationPermission
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import org.koin.mp.KoinPlatform

class HomeScreenComponent(
    val authorizationId: String?,
    private val errorCode: String?,
    private val onSettingsOpen: () -> Unit = {},
    componentContext: ComponentContext,
    private val weatherRepository: WeatherRepository = KoinPlatform.getKoin().get(),
    private val locationServiceRepository: LocationServiceRepository = KoinPlatform.getKoin().get(),
    private val permissionManager: PermissionManager = KoinPlatform.getKoin().get(),
    private val calendarEventsRepository: CalendarEventsRepository = KoinPlatform.getKoin().get(),
    private val todoistRepository: TodoistRepository = KoinPlatform.getKoin().get(),
    private val userPreferencesRepository: UserPreferencesRepository = KoinPlatform.getKoin().get(),
    private val permissions: Permissions = Permissions(KoinPlatform.getKoin().get<PermissionsBuilder>()),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ComponentContext by componentContext {

    private val coroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
    private var weatherJob: Job? = null
    private var calendarJob: Job? = null
    private var todoRefreshJob: Job? = null
    private var refreshJob: Job? = null
    private var authenticationJob: Job? = null
    private var disconnectJob: Job? = null
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<HomeEffect>()
    val effects = _effects.asSharedFlow()

    private val locationPermission = LocationPermission(background = false, precise = true)
    private val calendarPermission = CalendarPermission()

    init {
        lifecycle.doOnDestroy {
            coroutineScope.cancel()
        }
        lifecycle.doOnResume { refreshDateAndGreetings() }
        authorizationId?.let { handleAuthorizationCode(it) }
        errorCode?.let {
            _state.update { state -> state.copy(todoState = state.todoState.copy(error = "Unable to authenticate")) }
            sendEffect(HomeEffect.ShowToast("Unable to authenticate"))
        }
        observeLocationPermission()
        observeCalendarPermission()
        observeWeatherData()
        observeTodoData()
        refreshData()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.RequestLocationPermission -> handleLocationPermission()
            is HomeEvent.RequestCalendarPermission -> handleCalendarPermission()
            HomeEvent.ConnectTodoist -> requestTodoistAuthentication()
            HomeEvent.FetchWeather -> fetchLocationAndWeather()
            HomeEvent.FetchCalendarEvents -> fetchCalendarEvents()
            HomeEvent.DisconnectTodoist -> disconnectTodoist()
            HomeEvent.OpenSettingsPage -> {
                updateDropdownMenuState(false)
                onSettingsOpen()
            }
            is HomeEvent.OnOpenLink -> openURL(event.url)
            HomeEvent.FetchTodo -> refreshTodoTasks()
            HomeEvent.RefreshData -> refreshData()
            is HomeEvent.ShowDropdownMenu -> updateDropdownMenuState(event.show)
        }
    }

    private fun updateDropdownMenuState(show: Boolean) {
        _state.update { it.copy(showDropDown = show) }
    }

    suspend fun refresh() {
        withContext(dispatcher) {
            refreshData()?.join()
        }
    }

    private fun refreshData(): Job? {
        if (!coroutineScope.isActive) return null
        if (refreshJob?.isActive == true) return refreshJob

        refreshJob = coroutineScope.launch {
            refreshDateAndGreetings()
            updateDropdownMenuState(false)
            _state.update { it.copy(isRefreshing = true) }
            try {
                val jobs = listOfNotNull(
                    fetchLocationAndWeather(),
                    fetchCalendarEvents(),
                    refreshTodoTasks()
                )
                jobs.joinAll()
            } finally {
                _state.update { it.copy(isRefreshing = false) }
            }
        }
        return refreshJob
    }

    private fun refreshDateAndGreetings() {
        val localDateTime = DateUtils.getCurrentDateTime()
        _state.update {
            it.copy(
                greetings = localDateTime.getGreeting(),
                currentDateTime = localDateTime.toDDMMYYYY()
            )
        }
    }

    private fun observeLocationPermission() = coroutineScope.launch {
        permissions[locationPermission].filter { it !is PermissionState.Uninitialized }.collectLatest { permissionState ->
            val isGranted = permissionState is PermissionState.Allowed
            _state.update {
                it.copy(weatherState = it.weatherState.copy(isLocationPermissionGranted = isGranted))
            }
            if (isGranted) fetchLocationAndWeather() else weatherJob?.cancelAndJoin()
        }
    }

    private fun observeCalendarPermission() = coroutineScope.launch {
        permissions[calendarPermission].filter { it !is PermissionState.Uninitialized }.collectLatest { permissionState ->
            val isGranted = permissionState is PermissionState.Allowed
            _state.update {
                it.copy(calendarData = it.calendarData.copy(isCalendarPermissionGranted = isGranted))
            }
            if (isGranted) fetchCalendarEvents() else calendarJob?.cancelAndJoin()
        }
    }

    private fun handleLocationPermission() = coroutineScope.launch {
        when (permissions[locationPermission].peekState()) {
            is PermissionState.Allowed -> fetchLocationAndWeather()
            is PermissionState.Denied.Requestable,
            is PermissionState.Uninitialized -> permissions.request(locationPermission)
            is PermissionState.Denied.Locked -> permissionManager.openAppSettings()
            else -> {}
        }
    }

    private fun handleCalendarPermission() = coroutineScope.launch {
        when (permissions[calendarPermission].peekState()) {
            is PermissionState.Allowed -> fetchCalendarEvents()
            is PermissionState.Denied.Requestable,
            is PermissionState.Uninitialized -> permissions.request(calendarPermission)
            is PermissionState.Denied.Locked -> permissionManager.openAppSettings()
            else -> {}
        }
    }

    private fun fetchLocationAndWeather(): Job? {
        if (!isLocationPermissionGranted()) return null
        if (weatherJob?.isActive == true) return weatherJob

        weatherJob = coroutineScope.launch {
            _state.update {
                it.copy(weatherState = it.weatherState.copy(isLoading = true, error = null))
            }
            try {
                val location = locationServiceRepository.getCurrentLocation()
                val lat = location?.latitude
                val long = location?.longitude
                if (lat == null || long == null) error("Unable to fetch location")
                weatherRepository.refreshWeather(lat, long).getOrThrow()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _state.update {
                    it.copy(weatherState = it.weatherState.copy(
                        error = error.message ?: "Failed to fetch weather"
                    ))
                }
            } finally {
                _state.update { it.copy(weatherState = it.weatherState.copy(isLoading = false)) }
            }
        }
        return weatherJob
    }

    private fun openURL(url: String) {
        OpenURL.openURL(url)
    }

    private fun isLocationPermissionGranted(): Boolean {
        return permissions[locationPermission].peekState() is PermissionState.Allowed
    }

    private fun isCalendarPermissionGranted(): Boolean {
        return permissions[calendarPermission].peekState() is PermissionState.Allowed
    }

    private fun observeWeatherData() = coroutineScope.launch {
        combine(
            weatherRepository.weatherData,
            userPreferencesRepository.weatherUnitEnum
        ) { weatherData, weatherUnit ->
            Pair(weatherData, weatherUnit)
        }.collectLatest { (weatherData, weatherUnit) ->
            if (weatherData != null) {
                val currentWeatherState = weatherData.toCurrentWeatherState(weatherUnit)
                val forecastWeatherState = weatherData.forecast?.toForecastWeatherState(weatherUnit) ?: emptyList()
                _state.update {
                    it.copy(
                        weatherState = it.weatherState.copy(
                            currentWeather = currentWeatherState,
                            forecastWeather = forecastWeatherState,
                            weatherUnits = weatherUnit.name,
                            scrollToForecastCurrentTimeIndex = calculateCurrentForecastIndex(weatherData.forecast ?: emptyList())
                        )
                    )
                }
            }
        }
    }

    private fun calculateCurrentForecastIndex(
        forecast: List<WeatherData>,
    ): Int {
        val now = DateUtils.getCurrentDateTime()
        return forecast.indexOfFirst { item ->
            item.time?.let { it.date == now.date && it.hour == now.hour } == true
        }.coerceAtLeast(0)
    }

    private fun observeTodoData() = coroutineScope.launch {
        launch {
            todoistRepository.tasks.collectLatest { tasks ->
                _state.update {
                    it.copy(
                        todoState = it.todoState.copy(
                            todoData = tasks.toTodoDataState()
                        )
                    )
                }
            }
        }
        launch {
            todoistRepository.isConnected.distinctUntilChanged().collectLatest { isConnected ->
                _state.update {
                    it.copy(
                        todoState = it.todoState.copy(
                            isTodoAuthenticated = isConnected
                        )
                    )
                }
                if (isConnected) {
                    // Wait for token exchange before fetching tasks with the new credentials.
                    authenticationJob?.join()
                    refreshTodoTasks()
                } else {
                    todoRefreshJob?.cancelAndJoin()
                }
            }
        }
    }

    private fun fetchCalendarEvents(): Job? {
        if (!isCalendarPermissionGranted()) return null
        if (calendarJob?.isActive == true) return calendarJob

        calendarJob = coroutineScope.launch {
            _state.update {
                it.copy(calendarData = it.calendarData.copy(isLoading = true, error = null))
            }
            try {
                val events = calendarEventsRepository.getCalendarEvents()
                _state.update { it.copy(calendarData = it.calendarData.copy(calendarData = events)) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _state.update {
                    it.copy(calendarData = it.calendarData.copy(
                        error = error.message ?: "Failed to fetch calendar events"
                    ))
                }
            } finally {
                _state.update { it.copy(calendarData = it.calendarData.copy(isLoading = false)) }
            }
        }
        return calendarJob
    }

    private fun requestTodoistAuthentication() {
        if (authenticationJob?.isActive == true || disconnectJob?.isActive == true) return

        authenticationJob = coroutineScope.launch {
            _state.update { it.copy(todoState = it.todoState.copy(error = null)) }
            try {
                todoistRepository.requestAuthorization()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                val message = error.message ?: "Authentication failed"
                _state.update { it.copy(todoState = it.todoState.copy(error = message)) }
                sendEffect(HomeEffect.ShowToast(message))
            }
        }
    }

    private fun handleAuthorizationCode(code: String) {
        if (authenticationJob?.isActive == true || disconnectJob?.isActive == true) return

        authenticationJob = coroutineScope.launch {
            _state.update { it.copy(todoState = it.todoState.copy(isAuthenticating = true, error = null)) }
            try {
                todoRefreshJob?.cancelAndJoin()
                todoistRepository.exchangeToken(code).getOrThrow()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                val message = error.message ?: "Authentication failed"
                _state.update { it.copy(todoState = it.todoState.copy(error = message)) }
                sendEffect(HomeEffect.ShowToast(message))
            } finally {
                _state.update { it.copy(todoState = it.todoState.copy(isAuthenticating = false)) }
            }
        }
    }

    private fun refreshTodoTasks(): Job? {
        if (disconnectJob?.isActive == true) return null
        if (authenticationJob?.isActive == true) return null
        if (!_state.value.todoState.isTodoAuthenticated) return null
        if (todoRefreshJob?.isActive == true) return todoRefreshJob

        todoRefreshJob = coroutineScope.launch {
            _state.update { it.copy(todoState = it.todoState.copy(isLoading = true, error = null)) }
            try {
                todoistRepository.refreshTasks().getOrThrow()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                val message = error.message ?: "Failed to fetch tasks"
                if (message == ErrorCodes.UNAUTHORIZED.name) {
                    disconnectTodoist()
                    sendEffect(HomeEffect.ShowToast("Session expired. Please connect again."))
                } else {
                    _state.update { it.copy(todoState = it.todoState.copy(error = message)) }
                    sendEffect(HomeEffect.ShowToast(message))
                }
            } finally {
                _state.update { it.copy(todoState = it.todoState.copy(isLoading = false)) }
            }
        }
        return todoRefreshJob
    }

    private fun disconnectTodoist() {
        if (disconnectJob?.isActive == true) return

        disconnectJob = coroutineScope.launch {
            try {
                authenticationJob?.cancelAndJoin()
                todoRefreshJob?.cancelAndJoin()
                todoistRepository.disconnect()
                _state.update {
                    it.copy(todoState = it.todoState.copy(
                        isTodoAuthenticated = false,
                        todoToken = null,
                        todoData = null,
                        isLoading = false,
                        isAuthenticating = false,
                        error = null
                    ))
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                val message = error.message ?: "Failed to disconnect"
                _state.update { it.copy(todoState = it.todoState.copy(error = message)) }
                sendEffect(HomeEffect.ShowToast(message))
            }
        }
    }

    private fun sendEffect(effect: HomeEffect) = coroutineScope.launch {
        _effects.emit(effect)
    }
}
