package com.charan.yourday.presentation.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.charan.yourday.data.repository.TodoistRepository
import com.charan.yourday.data.repository.UserPreferencesRepository
import com.charan.yourday.presentation.common.DropDownItem
import com.charan.yourday.utils.appVersion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform

class SettingsScreenComponent(
    componentContext: ComponentContext,
    val onBackClick: () -> Unit,
    val onLicenseClick: () -> Unit = {},
    private val userPreferencesRepository: UserPreferencesRepository = KoinPlatform.getKoin().get(),
    private val todoistRepository: TodoistRepository = KoinPlatform.getKoin().get(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ComponentContext by componentContext {
    private val coroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
    private var temperatureJob: Job? = null
    private var authenticationJob: Job? = null
    private var disconnectJob: Job? = null

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState = _settingsState.asStateFlow()

    init {
        lifecycle.doOnDestroy { coroutineScope.cancel() }
        getSetTemperatureUnits()
        isTodoConnected()
        getAppVersion()
    }

    private fun getSetTemperatureUnits() = coroutineScope.launch {
        userPreferencesRepository.weatherUnits.collectLatest {
            updateSettingsState(
                weatherUnits = it
            )
        }
    }

    private fun getAppVersion() {
        updateSettingsState(
            appVersion = appVersion()
        )
    }

    private fun setTemperature(weatherUnits: String) {
        val previousJob = temperatureJob
        temperatureJob = coroutineScope.launch {
            // Save selections in order, so the last selection is the final saved value.
            previousJob?.join()
            _settingsState.update { it.copy(error = null) }
            try {
                userPreferencesRepository.setWeatherUnits(weatherUnits)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _settingsState.update { it.copy(error = error.message ?: "Unable to save weather units") }
            }
        }
    }

    private fun connectTodoist() {
        if (authenticationJob?.isActive == true || disconnectJob?.isActive == true) return

        authenticationJob = coroutineScope.launch {
            _settingsState.update { it.copy(error = null) }
            try {
                todoistRepository.requestAuthorization()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _settingsState.update { it.copy(error = error.message ?: "Unable to connect Todoist") }
            }
        }
    }

    private fun deleteTodoistToken() {
        if (disconnectJob?.isActive == true) return

        disconnectJob = coroutineScope.launch {
            _settingsState.update { it.copy(error = null) }
            try {
                authenticationJob?.cancelAndJoin()
                todoistRepository.disconnect()
                updateSettingsState(isTodoistConnected = false)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _settingsState.update { it.copy(error = error.message ?: "Unable to disconnect Todoist") }
            }
        }
    }

    private fun isTodoConnected() = coroutineScope.launch {
        userPreferencesRepository.todoistAccessToken.collectLatest { token ->
            updateSettingsState(isTodoistConnected = !token.isNullOrBlank())
        }
    }

    private fun updateDropdownMenuState(show: Boolean) {
        _settingsState.update {
            it.copy(
                showDropDown = show
            )
        }
    }

    fun onEvent(event: SettingsEvents) {
        if (!coroutineScope.isActive) return
        when (event) {
            is SettingsEvents.OnChangeWeatherUnits -> {
                setTemperature(event.weatherUnit)
            }

            is SettingsEvents.ShowDropdownMenu -> {
                updateDropdownMenuState(event.show)
            }

            SettingsEvents.TodoConnect -> {
                when (_settingsState.value.isTodoistConnected) {
                    false -> connectTodoist()
                    true -> deleteTodoistToken()
                    null -> {}
                }
            }

            SettingsEvents.onBack -> {
                onBackClick()
            }

            SettingsEvents.OnLicenseNavigate -> {
                onLicenseClick()
            }
        }
    }

    private fun updateSettingsState(
        weatherUnits: String? = null,
        isTodoistConnected: Boolean? = null,
        appVersion: String? = null,
        dropDownItems: List<DropDownItem>? = null
    ) {
        _settingsState.update {
            it.copy(
                weatherUnits = weatherUnits ?: it.weatherUnits,
                isTodoistConnected = isTodoistConnected ?: it.isTodoistConnected,
                appVersion = appVersion ?: it.appVersion,
                dropDownItems = dropDownItems ?: it.dropDownItems
            )
        }
    }
}
