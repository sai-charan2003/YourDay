package com.charan.yourday.utils


import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime

object DateUtils {

    @OptIn(FormatStringsInDatetimeFormats::class)
    fun getDateInDDMMYYYY(): String {
        val formatPattern = "dd/MM/yyyy"
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val dateTimeFormat = LocalDateTime.Format {
            byUnicodePattern(formatPattern)
        }
        return currentDate.format(dateTimeFormat)
    }

    fun getGreeting(): String {
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
        return when {
            currentTime.hour in 5..11 -> "Good Morning"
            currentTime.hour in 12..17 -> "Good Afternoon"
            currentTime.hour in 18..21 -> "Good Evening"
            else -> "Good Night"
        }
    }
}