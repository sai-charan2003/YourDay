package com.charan.yourday.presentation

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import com.charan.yourday.data.model.TodoData
import com.charan.yourday.data.repository.TodoistRepository
import com.charan.yourday.data.repository.UserPreferencesRepository
import com.charan.yourday.permission.PermissionManager
import com.charan.yourday.permission.PermissionType
import com.charan.yourday.presentation.onboarding.OnBoardingEvent
import com.charan.yourday.presentation.onboarding.OnBoardingScreenComponent
import com.charan.yourday.presentation.settings.SettingsEvents
import com.charan.yourday.presentation.settings.SettingsScreenComponent
import com.charan.yourday.utils.WeatherUnitsEnums
import com.splendo.kaluga.permissions.base.Permission
import com.splendo.kaluga.permissions.base.PermissionStateRepo
import com.splendo.kaluga.permissions.base.Permissions
import com.splendo.kaluga.permissions.base.PermissionsBuilder
import com.splendo.kaluga.permissions.calendar.CalendarPermission
import com.splendo.kaluga.permissions.location.LocationPermission
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration
import com.splendo.kaluga.permissions.base.PermissionManager as KalugaPermissionManager

@OptIn(ExperimentalCoroutinesApi::class)
class ComponentJobsTest {
    @Test
    fun settingsSavesRapidUnitSelectionsInOrder() = runTest {
        val fixture = Fixture(this)
        val gate = CompletableDeferred<Unit>()
        val writes = mutableListOf<String>()
        fixture.preferences.saveUnits = { units ->
            writes.add(units)
            if (writes.size == 1) gate.await()
            fixture.preferences.weatherUnits.value = units
        }
        val component = fixture.settings()
        advanceUntilIdle()
        component.onEvent(SettingsEvents.OnChangeWeatherUnits("F"))
        component.onEvent(SettingsEvents.OnChangeWeatherUnits("C"))
        advanceUntilIdle()
        assertEquals(listOf("F"), writes)
        gate.complete(Unit)
        advanceUntilIdle()
        assertEquals(listOf("F", "C"), writes)
        assertEquals("C", component.settingsState.value.weatherUnits)
        fixture.lifecycle.destroy()
    }

    @Test
    fun settingsReusesConnectAndCancelsItOnDestroy() = runTest {
        val fixture = Fixture(this)
        var cancelled = false
        fixture.todoist.authorize = {
            try { awaitCancellation() } finally { cancelled = true }
        }
        val component = fixture.settings()
        advanceUntilIdle()
        repeat(3) { component.onEvent(SettingsEvents.TodoConnect) }
        advanceUntilIdle()
        assertEquals(1, fixture.todoist.authorizationCalls)
        fixture.lifecycle.destroy()
        advanceUntilIdle()
        assertTrue(cancelled)
        val state = component.settingsState.value
        fixture.preferences.weatherUnits.value = "F"
        fixture.preferences.todoistAccessToken.value = "token"
        advanceUntilIdle()
        assertEquals(state, component.settingsState.value)
    }

    @Test
    fun settingsReusesDisconnectAndClearsConnection() = runTest {
        val fixture = Fixture(this)
        fixture.preferences.todoistAccessToken.value = "token"
        val gate = CompletableDeferred<Unit>()
        fixture.todoist.disconnectAction = {
            gate.await()
            fixture.preferences.todoistAccessToken.value = null
        }
        val component = fixture.settings()
        advanceUntilIdle()
        repeat(3) { component.onEvent(SettingsEvents.TodoConnect) }
        advanceUntilIdle()
        assertEquals(1, fixture.todoist.disconnectCalls)
        gate.complete(Unit)
        advanceUntilIdle()
        assertFalse(component.settingsState.value.isTodoistConnected == true)
        fixture.lifecycle.destroy()
    }

    @Test
    fun settingsReportsWriteFailureAndAllowsRetry() = runTest {
        val fixture = Fixture(this)
        fixture.preferences.saveUnits = { error("Write failed") }
        val component = fixture.settings()
        advanceUntilIdle()
        component.onEvent(SettingsEvents.OnChangeWeatherUnits("F"))
        advanceUntilIdle()
        assertEquals("Write failed", component.settingsState.value.error)
        fixture.preferences.saveUnits = { fixture.preferences.weatherUnits.value = it }
        component.onEvent(SettingsEvents.OnChangeWeatherUnits("F"))
        advanceUntilIdle()
        assertEquals("F", component.settingsState.value.weatherUnits)
        assertNull(component.settingsState.value.error)
        fixture.lifecycle.destroy()
    }

    @Test
    fun onboardingTracksTokenExchangeAndCancelsCollectorsOnDestroy() = runTest {
        val fixture = Fixture(this)
        var cancelled = false
        fixture.todoist.exchange = {
            try { awaitCancellation() } finally { cancelled = true }
        }
        val component = fixture.onboarding("code")
        advanceUntilIdle()
        component.onEvent(OnBoardingEvent.ConnectTodoist)
        advanceUntilIdle()
        assertEquals(0, fixture.todoist.authorizationCalls)
        fixture.lifecycle.destroy()
        advanceUntilIdle()
        assertTrue(cancelled)
        fixture.preferences.todoistAccessToken.value = "token"
        advanceUntilIdle()
        assertFalse(component.onBoardingState.value.isTodoistConnected)
        assertNull(component.onBoardingState.value.error)
        component.onEvent(OnBoardingEvent.Finish)
        assertEquals(0, fixture.finishCalls)
    }

    @Test
    fun onboardingReusesConnectRequests() = runTest {
        val fixture = Fixture(this)
        fixture.todoist.authorize = { awaitCancellation() }
        val component = fixture.onboarding()
        advanceUntilIdle()
        repeat(3) { component.onEvent(OnBoardingEvent.ConnectTodoist) }
        advanceUntilIdle()
        assertEquals(1, fixture.todoist.authorizationCalls)
        fixture.lifecycle.destroy()
    }

    @Test
    fun onboardingReportsTokenExchangeFailure() = runTest {
        val fixture = Fixture(this)
        fixture.todoist.exchange = { Result.failure(IllegalStateException("Bad code")) }
        val component = fixture.onboarding("code")
        advanceUntilIdle()
        assertEquals("Bad code", component.onBoardingState.value.error)
        assertFalse(component.onBoardingState.value.isTodoistConnected)
        fixture.lifecycle.destroy()
    }

    @Test
    fun onboardingReusesQueuedPermissionRequests() = runTest {
        val fixture = Fixture(this)
        val component = fixture.onboarding()
        advanceUntilIdle()
        repeat(3) {
            component.onEvent(OnBoardingEvent.RequestLocationPermission)
            component.onEvent(OnBoardingEvent.RequestCalendarPermission)
        }
        advanceUntilIdle()
        assertEquals(1, fixture.locationRequests)
        assertEquals(1, fixture.calendarRequests)
        fixture.lifecycle.destroy()
    }

    private class Fixture(scope: TestScope) {
        val lifecycle = LifecycleRegistry().apply { resume() }
        private val dispatcher = StandardTestDispatcher(scope.testScheduler)
        val preferences = FakePreferences()
        val todoist = FakeTodoist(preferences)
        var finishCalls = 0
        var locationRequests = 0
        var calendarRequests = 0

        fun settings() = SettingsScreenComponent(
            componentContext = DefaultComponentContext(lifecycle),
            onBackClick = {},
            userPreferencesRepository = preferences,
            todoistRepository = todoist,
            dispatcher = dispatcher
        )

        fun onboarding(code: String? = null): OnBoardingScreenComponent {
            val builder = PermissionsBuilder()
            builder.registerPermissionStateRepoBuilder<LocationPermission> { permission, context ->
                PermissionStateRepo(createPermissionManager = {
                    FakePermissionManager(permission) { locationRequests++ }
                }, coroutineContext = context)
            }
            builder.registerPermissionStateRepoBuilder<CalendarPermission> { permission, context ->
                PermissionStateRepo(createPermissionManager = {
                    FakePermissionManager(permission) { calendarRequests++ }
                }, coroutineContext = context)
            }
            return OnBoardingScreenComponent(
                componentContext = DefaultComponentContext(lifecycle),
                onFinish = { finishCalls++ },
                authorizationId = code,
                todoistRepository = todoist,
                userPreferencesRepository = preferences,
                permissions = Permissions(builder, dispatcher),
                permissionManager = object : PermissionManager {
                    override fun isPermissionGranted(permissions: PermissionType) = false
                    override fun requestPermission(permissions: PermissionType) = Unit
                    override fun requestMultiplePermissions(permissions: List<PermissionType>) = Unit
                    override fun openAppSettings() = Unit
                    override fun observeCalendarPermission() = flowOf(false)
                },
                dispatcher = dispatcher
            )
        }
    }

    private class FakePreferences : UserPreferencesRepository {
        override val weatherUnits = MutableStateFlow("C")
        override val weatherUnitEnum = weatherUnits.map { WeatherUnitsEnums.valueOf(it) }
        override val todoistAccessToken = MutableStateFlow<String?>(null)
        override val shouldShowOnboarding = MutableStateFlow(true)
        var saveUnits: suspend (String) -> Unit = { weatherUnits.value = it }
        override suspend fun setWeatherUnits(weatherUnits: String) = saveUnits(weatherUnits)
        override suspend fun setWeatherUnit(weatherUnit: WeatherUnitsEnums) { weatherUnits.value = weatherUnit.name }
        override suspend fun setTodoistAccessToken(token: String) { todoistAccessToken.value = token }
        override suspend fun clearTodoistAccessToken() { todoistAccessToken.value = null }
        override suspend fun setShouldShowOnboarding(shouldShow: Boolean) { shouldShowOnboarding.value = shouldShow }
    }

    private class FakeTodoist(private val preferences: FakePreferences) : TodoistRepository {
        override val tasks = MutableStateFlow<List<TodoData>>(emptyList())
        override val isConnected = preferences.todoistAccessToken.map { !it.isNullOrBlank() }
        var authorizationCalls = 0
        var disconnectCalls = 0
        var authorize: suspend () -> Unit = {}
        var exchange: suspend () -> Result<String> = { Result.success("token") }
        var disconnectAction: suspend () -> Unit = { preferences.clearTodoistAccessToken() }
        override suspend fun requestAuthorization() { authorizationCalls++; authorize() }
        override suspend fun exchangeToken(code: String) = exchange()
        override suspend fun refreshTasks() = Result.success(emptyList<TodoData>())
        override suspend fun disconnect() { disconnectCalls++; disconnectAction() }
    }

    private class FakePermissionManager<P : Permission>(
        override val permission: P,
        private val onRequest: () -> Unit
    ) : KalugaPermissionManager<P> {
        private val channel = Channel<KalugaPermissionManager.Event>(Channel.UNLIMITED)
        override val events = channel.receiveAsFlow()
        override fun requestPermission() = onRequest()
        override fun startMonitoring(interval: Duration) {
            channel.trySend(KalugaPermissionManager.Event.PermissionDenied(false))
        }
        override fun stopMonitoring() = Unit
    }
}
