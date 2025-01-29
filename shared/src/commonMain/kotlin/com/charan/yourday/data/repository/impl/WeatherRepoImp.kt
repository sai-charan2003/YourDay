package com.charan.yourday.data.repository.impl

import com.charan.yourday.data.network.Ktor.ApiService
import com.charan.yourday.data.network.responseDTO.WeatherDTO
import com.charan.yourday.data.repository.WeatherRepo
import com.charan.yourday.utils.ProcessState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class WeatherRepoImp(private val apiService: ApiService) : WeatherRepo {
    override suspend fun getCurrentWeatherInfo(lat: Double, long: Double): Flow<ProcessState<WeatherDTO?>> {
        val processState = MutableStateFlow<ProcessState<WeatherDTO?>>(ProcessState.Loading)
        try {
            val data = apiService.getCurrentWeather(lat, long)
            processState.emit(ProcessState.Success(data))
        } catch (e:Exception){
            processState.emit(ProcessState.Error(e.message.toString()))
        }
        return processState

    }

    override suspend fun getCurrentForecast(lat: Double, long: Double): Flow<ProcessState<WeatherDTO>> {
        val processState = MutableStateFlow<ProcessState<WeatherDTO>>(ProcessState.Loading)
        try {
            val data = apiService.getForecastWeather(lat, long)
            processState.emit(ProcessState.Success(data))
        } catch (e:Exception){
            println(e)
            processState.emit(ProcessState.Error(e.message.toString()))
        }
        return processState
    }
}