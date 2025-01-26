package com.charan.yourday.data.network


import com.charan.yourday.BuildKonfig
import com.charan.yourday.data.remote.responseDTO.WeatherDTO
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.URLProtocol
import io.ktor.http.append
import io.ktor.http.path

private const val base_url = "api.weatherapi.com/v1"
class ApiService (val client : HttpClient) {

    suspend fun getCurrentWeather(lat : Double, long : Double) : WeatherDTO? {
        try {
            return client.get {
                url {
                    host = base_url
                    path("current.json")
                    parameters.append("q", "$lat,$long")
                    parameters.append("key", BuildKonfig.API_KEY)
                }

            }.body<WeatherDTO>()
        } catch (e:Exception) {
            println("test")
            println(e.message.toString())
        }
        return null
    }

    suspend fun getForecastWeather(lat : Double, long : Double) : WeatherDTO {
        return client.get {
            url {
                host = base_url
                path("forecast.json")
                parameters.append("q","$lat,$long")
                parameters.append("key",BuildKonfig.API_KEY)
            }

        }.body<WeatherDTO>()


    }

}