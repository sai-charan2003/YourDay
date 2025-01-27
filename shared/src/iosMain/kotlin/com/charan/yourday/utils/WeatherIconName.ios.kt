package com.charan.yourday.utils

import com.charan.yourday.MR

actual class WeatherIconName {
    actual fun weatherIcon(code: String,isDay : Boolean): String? {
        val iconName = weatherIconsMap.entries.find { it.key.toString() == code }?.value
        return if(isDay) iconName?.first else iconName?.second
    }
}