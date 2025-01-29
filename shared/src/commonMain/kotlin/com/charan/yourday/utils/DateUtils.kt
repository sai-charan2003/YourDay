package com.charan.yourday.utils


import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.plus
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
        return when (currentTime.hour) {
            in 5..11 -> "Good Morning"
            in 12..17 -> "Good Afternoon"
            in 18..21 -> "Good Evening"
            else -> "Good Night"
        }
    }

    fun getCurrentTimeInMillis() : Long {
        return Clock.System.now().toEpochMilliseconds()
    }

    fun getStartOfDay(): Long {
        val timeZone = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        return today.atStartOfDayIn(timeZone).toEpochMilliseconds()
    }

    fun getEndOfDay(): Long {
        val timeZone = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        val tomorrow = today.plus(1, DateTimeUnit.DAY)
        return tomorrow.atStartOfDayIn(timeZone).toEpochMilliseconds() - 1
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    fun getTimeFromTimeMillis(timeMills: Long): String {
        val instant = Instant.fromEpochMilliseconds(timeMills)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val hour = localDateTime.hour % 12
        val adjustedHour = if (hour == 0) 12 else hour
        val amPm = if (localDateTime.hour < 12) "AM" else "PM"

        return "${adjustedHour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}$amPm"
    }
}