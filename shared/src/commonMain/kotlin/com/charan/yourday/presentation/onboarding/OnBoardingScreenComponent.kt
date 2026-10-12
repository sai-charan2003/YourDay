package com.charan.yourday.presentation.onboarding

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.charan.yourday.data.repository.TodoistRepository
import com.charan.yourday.data.repository.UserPreferencesRepository
import com.charan.yourday.permission.PermissionManager
import com.charan.yourday.permission.PermissionType
import com.splendo.kaluga.permissions.base.PermissionState
import com.splendo.kaluga.permissions.base.Permissions
import com.splendo.kaluga.permissions.base.PermissionsBuilder
import com.splendo.kaluga.permissions.calendar.CalendarPermission
import com.splendo.kaluga.permissions.location.LocationPermission
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform

class OnBoardingScreenComponent(
    componentContext: ComponentContext,
    val onFinish: () -> Unit,
    val authorizationId: String? = null,
    private val todoistRepository: TodoistRepository = KoinPlatform.getKoin().get(),
    private val userPreferencesRepository: UserPreferencesRepository = KoinPlatform.getKoin().get(),
    private val permissionManager: PermissionManager = KoinPlatform.getKoin().get(),
    private val permissions: Permissions = Permissions(KoinPlatform.getKoin().get<PermissionsBuilder>()),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ComponentContext by componentContext {

    private val coroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
    private var locationPermissionJob: Job? = null
    private var calendarPermissionJob: Job? = null
    private var authenticationJob: Job? = null
    private val locationPermission = LocationPermission(background = false, precise = true)
    private val calendarPermission = CalendarPermission()

    private val _onBoardingState = MutableStateFlow(OnBoardingState())
    val onBoardingState = _onBoardingState.asStateFlow()

    private val _onBoardingEffect = MutableSharedFlow<OnBoardingViewEffect>()
    val onBoardingEffect = _onBoardingEffect.asSharedFlow()

    init {
        lifecycle.doOnDestroy { coroutineScope.cancel() }
        authorizationId?.let { handleAuthorizationCode(it) }
        observeLocationPermission()
        observeCalendarPermission()
        observeTodoistAuth()
    }

    private fun observeLocationPermission() = coroutineScope.launch {
        permissions[locationPermission].filter { it !is PermissionState.Uninitialized }.collectLatest { permissionState ->
            val isGranted = permissionState is PermissionState.Allowed
            _onBoardingState.update { it.copy(isLocationPermissionGranted = isGranted) }
        }
    }

    private fun observeCalendarPermission() = coroutineScope.launch {
        permissions[calendarPermission].filter { it !is PermissionState.Uninitialized }.collectLatest { permissionState ->
            val isGranted = permissionState is PermissionState.Allowed
            _onBoardingState.update { it.copy(isCalendarPermissionGranted = isGranted) }
        }
    }

    private fun observeTodoistAuth() = coroutineScope.launch {
        userPreferencesRepository.todoistAccessToken.collectLatest { token ->
            _onBoardingState.update { it.copy(isTodoistConnected = !token.isNullOrBlank()) }
        }
    }

    fun onEvent(event: OnBoardingEvent) {
        if (!coroutineScope.isActive) return
        when (event) {
            is OnBoardingEvent.RequestLocationPermission,
            is OnBoardingEvent.onLocationPermissionGrant -> handleLocationPermission()

            is OnBoardingEvent.RequestCalendarPermission,
            is OnBoardingEvent.onCalenderPermissionGrant -> handleCalendarPermission()

            is OnBoardingEvent.ConnectTodoist,
            is OnBoardingEvent.onTodoistConnect -> connectTodoist()

            is OnBoardingEvent.Finish -> onFinish()
            is OnBoardingEvent.CheckPermissions -> checkPermissions()
        }
    }

    private fun handleLocationPermission() {
        if (locationPermissionJob?.isActive == true) return

        locationPermissionJob = coroutineScope.launch {
            _onBoardingState.update { it.copy(error = null) }
            try {
                when (permissions[locationPermission].peekState()) {
                    is PermissionState.Allowed -> {
                        _onBoardingState.update { it.copy(isLocationPermissionGranted = true) }
                    }
                    is PermissionState.Denied.Requestable,
                    is PermissionState.Uninitialized -> permissions.request(locationPermission)
                    is PermissionState.Denied.Locked -> permissionManager.openAppSettings()
                    else -> {}
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _onBoardingState.update { it.copy(error = error.message ?: "Unable to request location permission") }
            }
        }
    }

    private fun handleCalendarPermission() {
        if (calendarPermissionJob?.isActive == true) return

        calendarPermissionJob = coroutineScope.launch {
            _onBoardingState.update { it.copy(error = null) }
            try {
                when (permissions[calendarPermission].peekState()) {
                    is PermissionState.Allowed -> {
                        _onBoardingState.update { it.copy(isCalendarPermissionGranted = true) }
                    }
                    is PermissionState.Denied.Requestable,
                    is PermissionState.Uninitialized -> permissions.request(calendarPermission)
                    is PermissionState.Denied.Locked -> permissionManager.openAppSettings()
                    else -> {}
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _onBoardingState.update { it.copy(error = error.message ?: "Unable to request calendar permission") }
            }
        }
    }

    private fun connectTodoist() {
        if (authenticationJob?.isActive == true) return

        authenticationJob = coroutineScope.launch {
            _onBoardingState.update { it.copy(error = null) }
            try {
                todoistRepository.requestAuthorization()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _onBoardingState.update { it.copy(error = error.message ?: "Unable to connect Todoist") }
            }
        }
    }

    private fun handleAuthorizationCode(code: String) {
        if (authenticationJob?.isActive == true) return

        authenticationJob = coroutineScope.launch {
            _onBoardingState.update { it.copy(error = null) }
            try {
                todoistRepository.exchangeToken(code).getOrThrow()
                _onBoardingState.update { it.copy(isTodoistConnected = true) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _onBoardingState.update { it.copy(error = error.message ?: "Authentication failed") }
            }
        }
    }

    private fun checkPermissions() {
        _onBoardingState.update {
            it.copy(
                isLocationPermissionGranted = permissionManager.isPermissionGranted(PermissionType.LOCATION),
                isCalendarPermissionGranted = permissionManager.isPermissionGranted(PermissionType.CALENDAR)
            )
        }
    }
}
