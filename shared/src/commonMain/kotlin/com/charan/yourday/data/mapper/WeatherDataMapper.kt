package com.charan.yourday.data.mapper

import com.charan.yourday.data.model.WeatherData
import com.charan.yourday.data.network.responseDTO.WeatherDTO
import com.charan.yourday.utils.WeatherUnits

fun WeatherDTO.mapToWeatherData(units : String)  : WeatherData {
    return when (units) {
        WeatherUnits.C -> WeatherData(
            currentTemperature = this.getCurrentTemperatureInC(),
            maxTemperature = this.getMaxTemperatureInC(),
            minTemperature = this.getMinTemperatureInC(),
            currentCondition = this.getCurrentCondition(),
            temperatureIcon = this.getImageIcon(),
            location = this.getLocation()
        )
        WeatherUnits.F -> WeatherData(
            currentTemperature = this.getCurrentTemperatureInF(),
            maxTemperature = this.getMaxTemperatureInF(),
            minTemperature = this.getMinTemperatureInF(),
            currentCondition = this.getCurrentCondition(),
            temperatureIcon = this.getImageIcon(),
            location = this.getLocation()
        )
        else -> {
            WeatherData()
        }
    }
}