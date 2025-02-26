package com.charan.yourday.data.model

import dev.icerock.moko.resources.ImageResource

data class WeatherData(
    val currentTemperature : String? = null,
    val maxTemperature : String? = null,
    val minTemperature : String? = null,
    val currentCondition : String? = null,
    val temperatureIcon : ImageResource? = null,
    val location : String? = null
)
