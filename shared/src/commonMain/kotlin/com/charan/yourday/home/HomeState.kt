package com.charan.yourday.home

import com.charan.yourday.data.model.CalenderItems
import com.charan.yourday.data.model.TodoData
import com.charan.yourday.data.model.WeatherData
import com.charan.yourday.data.network.responseDTO.WeatherDTO
import com.charan.yourday.utils.ProcessState
import dev.icerock.moko.resources.ImageResource

data class HomeState(
    val weatherState: WeatherState = WeatherState(),
    val todoState: TodoState = TodoState(),
    val calenderData: CalenderState = CalenderState(),
    val isRefreshing : Boolean = false
)

data class WeatherState(
    val isLoading : Boolean = false,
    val weatherData : WeatherData? = null,
    val error : String? = null,
    val isLocationPermissionGranted : Boolean = false,
    val lastSycned: String? = null
)

data class TodoState(
    val isAuthenticating : Boolean = false,
    val isLoading : Boolean = true,
    val todoData : List<TodoData>? = null,
    val error : String? = null,
    val isTodoAuthenticated: Boolean = false,
    val todoToken : String? = null,
    val lastSycned : String? = null
)

data class CalenderState(
    val calenderData : List<CalenderItems>? = null,
    val isLoading: Boolean = false,
    val isCalenderPermissionGranted : Boolean = false,
    val error : String? = null,
    val lastSycned: String? = null
)


