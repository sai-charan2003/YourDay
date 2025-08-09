package com.charan.yourday.data.repository.impl

import com.charan.yourday.data.network.Ktor.ApiService
import com.charan.yourday.data.network.responseDTO.TodoistTokenDTO
import com.charan.yourday.data.network.responseDTO.WeatherDTO
import com.charan.yourday.data.network.responseDTO.WeatherError
import com.charan.yourday.data.repository.WeatherRepo
import com.charan.yourday.utils.ErrorCodes
import com.charan.yourday.utils.ProcessState
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class WeatherRepoImp(private val apiService: ApiService) : WeatherRepo {
    override suspend fun getCurrentWeatherInfo(lat: Double, long: Double): Flow<ProcessState<WeatherDTO?>> =flow{
        emit(ProcessState.Loading)
        try {
            val response = apiService.getCurrentWeather(lat, long)
            print(response  )
            when(response.status){
                HttpStatusCode.OK -> {
                    emit(ProcessState.Success(response.body<WeatherDTO>()))
                }
                HttpStatusCode.Unauthorized -> {
                    emit(ProcessState.Error(ErrorCodes.UNAUTHORIZED.name))
                }
                else -> {
                    emit(ProcessState.Error(response.body<WeatherError>().error?.message ?: "API Error: ${response.status}"))
                }


            }
        } catch (e :Exception){
            emit(ProcessState.Error(e.message ?: "Unknown Error"))
        }



    }


    override suspend fun getCurrentForecast(lat: Double, long: Double): Flow<ProcessState<WeatherDTO>> =flow{
        emit(ProcessState.Loading)
        try {
            val response = apiService.getForecastWeather(lat, long)
            when(response.status){
                HttpStatusCode.OK -> {
                    emit(ProcessState.Success(response.body<WeatherDTO>()))
                }
                HttpStatusCode.Unauthorized -> {
                    emit(ProcessState.Error(ErrorCodes.UNAUTHORIZED.name))
                }
                else -> {
                    emit(ProcessState.Error(response.body<WeatherError>().error?.message ?: "API Error: ${response.status}"))
                }

            }
        } catch (e :Exception){
            print("error for weather $e")
            emit(ProcessState.Error(e.message ?: "Unknown Error"))
        }
    }

}