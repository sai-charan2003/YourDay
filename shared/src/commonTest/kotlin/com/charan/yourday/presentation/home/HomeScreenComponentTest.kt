package com.charan.yourday.presentation.home

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.pause
import com.arkivanov.essenty.lifecycle.resume
import com.charan.yourday.data.model.CalendarItems
import com.charan.yourday.data.model.Location
import com.charan.yourday.data.model.TodoData
import com.charan.yourday.data.model.WeatherData
import com.charan.yourday.data.repository.CalendarEventsRepository
import com.charan.yourday.data.repository.LocationServiceRepository
import com.charan.yourday.data.repository.TodoistRepository
import com.charan.yourday.data.repository.UserPreferencesRepository
import com.charan.yourday.data.repository.WeatherRepository
import com.charan.yourday.permission.PermissionManager
import com.charan.yourday.permission.PermissionType
import com.charan.yourday.utils.WeatherUnitsEnums
import com.splendo.kaluga.permissions.base.Permission
import com.splendo.kaluga.permissions.base.PermissionStateRepo
import com.splendo.kaluga.permissions.base.Permissions
import com.splendo.kaluga.permissions.base.PermissionsBuilder
import com.splendo.kaluga.permissions.calendar.CalendarPermission
import com.splendo.kaluga.permissions.location.LocationPermission
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration
import com.splendo.kaluga.permissions.base.PermissionManager as KalugaPermissionManager

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenComponentTest {
    @Test
    fun refreshSkipsDeniedPermissionsAndDisconnectedTodoist() = runTest {
        val fixture = Fixture(this)
        val component = fixture.create()
        advanceUntilIdle()
        component.refresh()
        assertEquals(0, fixture.locationCalls)
        assertEquals(0, fixture.calendarCalls)
        assertEquals(0, fixture.todoCalls)
        assertFalse(component.state.value.calendarData.isCalendarPermissionGranted)
        assertFalse(component.state.value.isRefreshing)
        fixture.lifecycle.destroy()
    }

    @Test
    fun repeatedRefreshSharesRequestsAndWaitsForAllSections() = runTest {
        val fixture = Fixture(this, allowed = true, connected = true)
        val weatherGate = CompletableDeferred<Unit>()
        val calendarGate = CompletableDeferred<Unit>()
        val todoGate = CompletableDeferred<Unit>()
        fixture.weatherFetch = { weatherGate.await(); Result.success(WeatherData()) }
        fixture.calendarFetch = { calendarGate.await(); emptyList() }
        fixture.todoFetch = { todoGate.await(); Result.success(emptyList()) }
        val component = fixture.create()
        advanceUntilIdle()
        val first = async { component.refresh() }
        val second = async { component.refresh() }
        advanceUntilIdle()
        assertTrue(component.state.value.isRefreshing)
        assertEquals(1, fixture.weatherCalls)
        assertEquals(1, fixture.calendarCalls)
        assertEquals(1, fixture.todoCalls)
        // Cached task emissions must not finish the active network request.
        fixture.tasks.value = listOf(TodoData(id = "cached-task"))
        advanceUntilIdle()
        assertTrue(component.state.value.todoState.isLoading)
        weatherGate.complete(Unit)
        calendarGate.complete(Unit)
        advanceUntilIdle()
        assertFalse(first.isCompleted)
        assertTrue(component.state.value.isRefreshing)
        todoGate.complete(Unit)
        advanceUntilIdle()
        first.await()
        second.await()
        assertFalse(component.state.value.isRefreshing)
        assertFalse(component.state.value.todoState.isLoading)
        fixture.lifecycle.destroy()
    }

    @Test
    fun locationAndCalendarExceptionsClearLoadingAndAllowRetry() = runTest {
        val fixture = Fixture(this, allowed = true)
        fixture.locationFetch = { error("Location unavailable") }
        fixture.calendarFetch = { error("Calendar unavailable") }
        val component = fixture.create()
        advanceUntilIdle()
        assertEquals("Location unavailable", component.state.value.weatherState.error)
        assertEquals("Calendar unavailable", component.state.value.calendarData.error)
        assertFalse(component.state.value.weatherState.isLoading)
        assertFalse(component.state.value.calendarData.isLoading)
        fixture.locationFetch = { Location(40.0, -73.0) }
        fixture.calendarFetch = { emptyList() }
        component.refresh()
        assertNull(component.state.value.weatherState.error)
        assertNull(component.state.value.calendarData.error)
        fixture.lifecycle.destroy()
    }

    @Test
    fun missingCoordinatesNeverRequestWeatherAtZeroCoordinates() = runTest {
        val fixture = Fixture(this, allowed = true)
        fixture.locationFetch = { Location(null, -73.0) }
        val component = fixture.create()
        advanceUntilIdle()
        assertEquals(0, fixture.weatherCalls)
        assertEquals("Unable to fetch location", component.state.value.weatherState.error)
        fixture.lifecycle.destroy()
    }

    @Test
    fun destroyingComponentCancelsRequestsAndCollectors() = runTest {
        val fixture = Fixture(this, allowed = true)
        var cancelled = false
        fixture.locationFetch = {
            try { awaitCancellation() } finally { cancelled = true }
        }
        val component = fixture.create()
        advanceUntilIdle()
        component.onEvent(HomeEvent.RefreshData)
        advanceUntilIdle()
        fixture.lifecycle.destroy()
        advanceUntilIdle()
        assertTrue(cancelled)
        assertFalse(component.state.value.isRefreshing)
        assertFalse(component.state.value.weatherState.isLoading)
        assertNull(component.state.value.weatherState.error)
        val stateAfterDestroy = component.state.value
        fixture.tasks.value = listOf(TodoData(id = "cached-task"))
        fixture.weather.value = WeatherData(tempC = 25)
        advanceUntilIdle()
        assertEquals(stateAfterDestroy, component.state.value)
    }

    @Test
    fun permissionRevocationCancelsCalendarWithoutSettingPermissionBackToGranted() = runTest {
        val fixture = Fixture(this, allowed = true)
        var cancelled = false
        fixture.calendarFetch = {
            try { awaitCancellation() } finally { cancelled = true }
        }
        val component = fixture.create()
        advanceUntilIdle()
        fixture.calendarPermissionEvents.send(KalugaPermissionManager.Event.PermissionDenied(false))
        advanceUntilIdle()
        assertTrue(cancelled)
        assertFalse(component.state.value.calendarData.isCalendarPermissionGranted)
        assertFalse(component.state.value.calendarData.isLoading)
        component.refresh()
        assertEquals(1, fixture.calendarCalls)
        fixture.lifecycle.destroy()
    }

    @Test
    fun authorizationAndRepeatedConnectedEmissionsRefreshTodoistOnce() = runTest {
        val fixture = Fixture(this)
        fixture.exchangeToken = { fixture.connected.emit(true); Result.success("token") }
        val component = fixture.create(authorizationId = "code")
        advanceUntilIdle()
        fixture.connected.emit(true)
        advanceUntilIdle()
        assertEquals(1, fixture.todoCalls)
        assertTrue(component.state.value.todoState.isTodoAuthenticated)
        assertFalse(component.state.value.todoState.isAuthenticating)
        fixture.lifecycle.destroy()
    }

    @Test
    fun reconnectingWaitsForTokenExchangeBeforeFetchingTasks() = runTest {
        val fixture = Fixture(this, connected = true)
        val tokenGate = CompletableDeferred<Unit>()
        fixture.exchangeToken = {
            tokenGate.await()
            fixture.connected.emit(true)
            Result.success("new-token")
        }
        val component = fixture.create(authorizationId = "code")
        advanceUntilIdle()
        assertEquals(0, fixture.todoCalls)
        assertTrue(component.state.value.todoState.isAuthenticating)
        tokenGate.complete(Unit)
        advanceUntilIdle()
        assertEquals(1, fixture.todoCalls)
        assertFalse(component.state.value.todoState.isAuthenticating)
        fixture.lifecycle.destroy()
    }

    @Test
    fun authenticationErrorIsAvailableBeforeUiStartsCollectingEffects() = runTest {
        val fixture = Fixture(this)
        val component = fixture.create(errorCode = "access_denied")
        advanceUntilIdle()
        assertEquals(HomeEffect.ShowToast("Unable to authenticate"), component.effects.first())
        assertEquals("Unable to authenticate", component.state.value.todoState.error)
        fixture.lifecycle.destroy()
    }

    @Test
    fun greetingUpdatesOnResumeAndForecastUsesFullDate() = runTest {
        val fixture = Fixture(this)
        val component = fixture.create()
        assertEquals("Good Morning", component.state.value.greetings)
        assertEquals("11/10/2026", component.state.value.currentDateTime)
        fixture.lifecycle.resume()
        fixture.lifecycle.pause()
        fixture.now = LocalDateTime(2026, 10, 12, 18, 30)
        fixture.lifecycle.resume()
        assertEquals("Good Evening", component.state.value.greetings)
        assertEquals("12/10/2026", component.state.value.currentDateTime)
        fixture.weather.value = WeatherData(forecast = listOf(
            WeatherData(time = LocalDateTime(2026, 10, 11, 18, 0)),
            WeatherData(time = LocalDateTime(2026, 10, 12, 18, 0)),
            WeatherData(time = LocalDateTime(2026, 10, 12, 19, 0))
        ))
        advanceUntilIdle()
        assertEquals(1, component.state.value.weatherState.scrollToForecastCurrentTimeIndex)
        fixture.lifecycle.destroy()
    }

    @Test
    fun individualSectionEventsReuseInFlightRequests() = runTest {
        val fixture = Fixture(this, allowed = true, connected = true)
        val gate = CompletableDeferred<Unit>()
        fixture.weatherFetch = { gate.await(); Result.success(WeatherData()) }
        fixture.calendarFetch = { gate.await(); emptyList() }
        fixture.todoFetch = { gate.await(); Result.success(emptyList()) }
        val component = fixture.create()
        advanceUntilIdle()
        repeat(3) {
            component.onEvent(HomeEvent.FetchWeather)
            component.onEvent(HomeEvent.FetchCalendarEvents)
            component.onEvent(HomeEvent.FetchTodo)
        }
        advanceUntilIdle()
        assertEquals(1, fixture.locationCalls)
        assertEquals(1, fixture.weatherCalls)
        assertEquals(1, fixture.calendarCalls)
        assertEquals(1, fixture.todoCalls)
        gate.complete(Unit)
        advanceUntilIdle()
        fixture.lifecycle.destroy()
    }

    @Test
    fun disconnectWaitsForTaskCancellationBeforeClearingCredentials() = runTest {
        val fixture = Fixture(this, connected = true)
        var cancelled = false
        fixture.todoFetch = {
            try { awaitCancellation() } finally { cancelled = true }
        }
        fixture.disconnect = {
            assertTrue(cancelled)
            fixture.connected.emit(false)
        }
        val component = fixture.create()
        advanceUntilIdle()
        component.onEvent(HomeEvent.DisconnectTodoist)
        advanceUntilIdle()
        assertEquals(1, fixture.disconnectCalls)
        assertFalse(component.state.value.todoState.isLoading)
        assertFalse(component.state.value.todoState.isTodoAuthenticated)
        assertNull(component.state.value.todoState.error)
        fixture.lifecycle.destroy()
    }

    @Test
    fun disconnectCancelsTokenExchangeBeforeClearingCredentials() = runTest {
        val fixture = Fixture(this)
        var cancelled = false
        fixture.exchangeToken = {
            try { awaitCancellation() } finally { cancelled = true }
        }
        fixture.disconnect = { assertTrue(cancelled) }
        val component = fixture.create(authorizationId = "code")
        advanceUntilIdle()
        component.onEvent(HomeEvent.DisconnectTodoist)
        advanceUntilIdle()
        assertEquals(1, fixture.disconnectCalls)
        assertEquals(0, fixture.todoCalls)
        assertFalse(component.state.value.todoState.isAuthenticating)
        assertNull(component.state.value.todoState.error)
        fixture.lifecycle.destroy()
    }

    private class Fixture(private val scope: TestScope, private val allowed: Boolean = false, connected: Boolean = false) {
        val lifecycle = LifecycleRegistry().apply { resume() }
        val dispatcher = StandardTestDispatcher(scope.testScheduler)
        var now = LocalDateTime(2026, 10, 11, 10, 30)
        val weather = MutableStateFlow<WeatherData?>(null)
        val tasks = MutableStateFlow<List<TodoData>>(emptyList())
        val connected = MutableSharedFlow<Boolean>(replay = 1).also { it.tryEmit(connected) }
        val calendarPermissionEvents = Channel<KalugaPermissionManager.Event>(Channel.UNLIMITED)
        var locationCalls = 0
        var weatherCalls = 0
        var calendarCalls = 0
        var todoCalls = 0
        var disconnectCalls = 0
        var disconnect: suspend () -> Unit = { this.connected.emit(false) }
        var locationFetch: suspend () -> Location? = { Location(40.0, -73.0) }
        var weatherFetch: suspend () -> Result<WeatherData> = { Result.success(WeatherData()) }
        var calendarFetch: suspend () -> List<CalendarItems> = { emptyList() }
        var todoFetch: suspend () -> Result<List<TodoData>> = { Result.success(emptyList()) }
        var exchangeToken: suspend () -> Result<String> = { Result.success("token") }

        fun create(authorizationId: String? = null, errorCode: String? = null): HomeScreenComponent {
            val builder = PermissionsBuilder()
            builder.registerPermissionStateRepoBuilder<LocationPermission> { permission, context ->
                PermissionStateRepo(createPermissionManager = {
                    FakePermissionManager(permission, allowed, Channel(Channel.UNLIMITED))
                }, coroutineContext = context)
            }
            builder.registerPermissionStateRepoBuilder<CalendarPermission> { permission, context ->
                PermissionStateRepo(createPermissionManager = {
                    FakePermissionManager(permission, allowed, calendarPermissionEvents)
                }, coroutineContext = context)
            }
            return HomeScreenComponent(
                authorizationId = authorizationId,
                errorCode = errorCode,
                componentContext = DefaultComponentContext(lifecycle),
                weatherRepository = object : WeatherRepository {
                    override val weatherData = weather
                    override suspend fun refreshWeather(lat: Double, long: Double): Result<WeatherData> {
                        weatherCalls++
                        return weatherFetch()
                    }
                },
                locationServiceRepository = object : LocationServiceRepository {
                    override suspend fun getCurrentLocation(): Location? { locationCalls++; return locationFetch() }
                },
                calendarEventsRepository = object : CalendarEventsRepository {
                    override suspend fun getCalendarEvents(): List<CalendarItems> { calendarCalls++; return calendarFetch() }
                },
                todoistRepository = object : TodoistRepository {
                    override val tasks = this@Fixture.tasks
                    override val isConnected = this@Fixture.connected
                    override suspend fun requestAuthorization() = Unit
                    override suspend fun exchangeToken(code: String) = exchangeToken()
                    override suspend fun refreshTasks(): Result<List<TodoData>> { todoCalls++; return todoFetch() }
                    override suspend fun disconnect() { disconnectCalls++; this@Fixture.disconnect() }
                },
                userPreferencesRepository = object : UserPreferencesRepository {
                    override val weatherUnits = flowOf("C")
                    override val weatherUnitEnum = flowOf(WeatherUnitsEnums.C)
                    override val todoistAccessToken = flowOf<String?>(null)
                    override val shouldShowOnboarding = flowOf(false)
                    override suspend fun setWeatherUnits(weatherUnits: String) = Unit
                    override suspend fun setWeatherUnit(weatherUnit: WeatherUnitsEnums) = Unit
                    override suspend fun setTodoistAccessToken(token: String) = Unit
                    override suspend fun clearTodoistAccessToken() = Unit
                    override suspend fun setShouldShowOnboarding(shouldShow: Boolean) = Unit
                },
                permissionManager = object : PermissionManager {
                    override fun isPermissionGranted(permissions: PermissionType) = allowed
                    override fun requestPermission(permissions: PermissionType) = Unit
                    override fun requestMultiplePermissions(permissions: List<PermissionType>) = Unit
                    override fun openAppSettings() = Unit
                    override fun observeCalendarPermission() = flowOf(allowed)
                },
                permissions = Permissions(builder, dispatcher),
                currentDateTime = { now },
                dispatcher = dispatcher
            )
        }
    }

    private class FakePermissionManager<P : Permission>(
        override val permission: P,
        private val allowed: Boolean,
        private val channel: Channel<KalugaPermissionManager.Event>
    ) : KalugaPermissionManager<P> {
        override val events = channel.receiveAsFlow()
        override fun requestPermission() = Unit
        override fun startMonitoring(interval: Duration) {
            channel.trySend(if (allowed) KalugaPermissionManager.Event.PermissionGranted else KalugaPermissionManager.Event.PermissionDenied(false))
        }
        override fun stopMonitoring() = Unit
    }
}
