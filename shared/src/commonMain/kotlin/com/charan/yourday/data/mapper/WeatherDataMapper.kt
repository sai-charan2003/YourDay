package com.charan.yourday.data.mapper

import com.charan.yourday.data.model.WeatherData
import com.charan.yourday.data.network.responseDTO.WeatherDTO
import com.charan.yourday.utils.DateUtils
import com.charan.yourday.utils.WeatherIconName
import com.charan.yourday.utils.WeatherUnits

fun WeatherDTO.mapToWeatherData(units : String)  : WeatherData {
    return when (units) {
        WeatherUnits.C -> WeatherData(
            currentTemperature = this.getCurrentTemperatureInC(),
            maxTemperature = this.getMaxTemperatureInC(),
            minTemperature = this.getMinTemperatureInC(),
            currentCondition = this.getCurrentCondition(),
            temperatureIcon = this.getImageIcon(),
            location = this.getLocation(),
            forecast = this.mapForecast(WeatherUnits.C)
        )
        WeatherUnits.F -> WeatherData(
            currentTemperature = this.getCurrentTemperatureInF(),
            maxTemperature = this.getMaxTemperatureInF(),
            minTemperature = this.getMinTemperatureInF(),
            currentCondition = this.getCurrentCondition(),
            temperatureIcon = this.getImageIcon(),
            location = this.getLocation(),
            forecast = this.mapForecast(WeatherUnits.F)
        )
        else -> {
            WeatherData()
        }
    }
}

fun WeatherDTO.mapForecast(units: String) : List<WeatherData>?{
    val currentMillis = DateUtils.getCurrentTimeInMillis()
    val forecastWeather = this.forecast?.forecastday?.firstOrNull()?.hour?.filter { (it.timeEpoch?.times(
        1000
    ))!! > currentMillis }
    println(forecastWeather)
    val weatherData = forecastWeather?.map {
        println(it.timeEpoch)
            WeatherData(
                currentTemperature = if(units == WeatherUnits.C) "${it.tempC.toString()} C°" else "${it.tempF.toString()} F°",
                temperatureIcon = WeatherIconName.weatherIcon(it.condition?.code?.toInt()?.toString() ?: "0",it.isDay == 1.0),
                time = it.timeEpoch?.times(1000)
                    ?.let { it1 -> DateUtils.getTimeFromTimeMillis(it1) },
                currentCondition = it.condition?.text

            )
    }
    return weatherData


}